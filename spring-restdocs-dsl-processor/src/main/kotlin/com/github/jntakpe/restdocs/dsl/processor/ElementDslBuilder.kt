package com.github.jntakpe.restdocs.dsl.processor

import com.github.jntakpe.restdocs.dsl.annotations.Doc
import com.github.jntakpe.restdocs.dsl.annotations.EnableRestDocsAutoDsl
import com.github.jntakpe.restdocs.dsl.annotations.RestDocsAutoDslMarker
import com.github.jntakpe.restdocs.dsl.core.DocBuilder
import com.github.jntakpe.restdocs.dsl.processor.ApiDocBuilder.Companion.PACKAGE
import com.github.jntakpe.restdocs.dsl.processor.ApiDocBuilder.Companion.toApiDocProperty
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.PUBLIC
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind.FIELD
import javax.lang.model.element.PackageElement
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClassifier

/**
 * For each class annotated with [Doc], produces a file suffixed with DocDsl
 * containing DocBuilder class for this class and a DSL function invoked by consumer to document
 * fields of this class
 */
class ElementDslBuilder(
    private val element: Element,
    private val env: ProcessingEnvironment,
    private val apiDocBuilder: ApiDocBuilder,
    private val config: List<EnableRestDocsAutoDsl>
) {

    init {
        apiDocBuilder.addElement(element)
    }

    companion object {
        val fieldsType = ClassName("$PACKAGE.core", "Fields")
        val kClassifierType = KClassifier::class.asTypeName().copy(nullable = true)
    }

    private fun packageOfClass(): PackageElement = env.elementUtils.getPackageOf(element)

    private fun dslFunctionName(): String {
        return element.simpleName.toString().removeTrailingSuffixes(config).firstLetterToLowerCase()
    }

    private fun dslFunction(): FunSpec.Builder {
        return FunSpec.builder(dslFunctionName())
            .addModifiers(PUBLIC)
            .addParameter("init", LambdaTypeName.get(dslClass(), emptyList(), UNIT))
            .addStatement("return applyDsl(init)")
            .returns(fieldsType)
    }

    private fun dslClass(): ClassName = ClassName(packageOfClass().toString(), className())

    private fun className() = "${element.simpleName}DocDsl"

    private fun Element.toApiDocRef(): String = "::${toApiDocProperty(config)}"

    private fun TypeMirror.toApiDocRef(): String = "::${toApiDocProperty(config)}"

    private fun docBuilder(): Pair<TypeSpec, List<Pair<Element, Boolean>>> {
        val properties = properties()
        return TypeSpec.classBuilder(className())
            .addAnnotation(RestDocsAutoDslMarker::class)
            .superclass(DocBuilder::class)
            .addSuperclassConstructorParameter(element.toApiDocRef())
            .addProperties(properties.map { (p, _) -> p })
            .build() to properties.mapNotNull { (_, e) -> e }
    }

    private fun properties(): List<Pair<PropertySpec, Pair<Element, Boolean>?>> {
        return element.enclosedElements
            .filter { it.kind == FIELD }
            .map {
                val (d, e) = it.toDelegates(element)
                it.toDslProperty(d) to e
            }
    }

    private fun Element.toDelegates(owner: Element): Pair<CodeBlock, Pair<Element, Boolean>?> {
        val (selfApiDocRef, el) = nestedToApiDocRef()
        return CodeBlock.builder()
            .addStatement("doc(self, ${owner.simpleName}::$simpleName $selfApiDocRef)")
            .build() to el
    }

    private fun Element.toDslProperty(delegates: CodeBlock): PropertySpec {
        return PropertySpec.builder("$simpleName", String::class)
            .mutable()
            .delegate(delegates)
            .build()
    }

    private fun Element.nestedToApiDocRef(): Pair<String, Pair<Element, Boolean>?> {
        val isNotDocumented = isNotDocumented()
        val isExt = isNotDocumented && !isSimpleType() && !isSimpleArrayType() && !isComplexArrayType()
        if (isExt) {
            apiDocBuilder.addElement(this, true).build()
            // TODO complex but worth it -> goal is to generate a DSL for external classes also
//            ElementDslBuilder(this, env, apiDocBuilder).build()
        }
        return takeUnless { it.isSimpleType() || it.isSimpleArrayType() }
            ?.let { if (isExt) ", ${toApiDocRef()}, ${asType().toApiDocRef()}" else ", ${toApiDocRef()}" }
            ?.let { it to (this to isExt) }
            ?: ("" to null)
    }

    private fun Element.isNotDocumented(): Boolean {
        return asType() !in apiDocBuilder.elements
            .filter { (_, isExt) -> !isExt }
            .map { (e, _) -> e.asType() }
    }

    private fun file(): FileSpec.Builder {
        val (docBuilder, imports) = docBuilder()
        return FileSpec.builder(packageOfClass().toString(), className())
            .addImport("$PACKAGE.core", "doc")
            .addImport("$PACKAGE.core", "applyDsl")
            .addImport(PACKAGE, element.toApiDocProperty(config))
            .apply {
                imports.forEach { (el, isExt) ->
                    if (isExt) addImport(PACKAGE, el.asType().toApiDocProperty(config))
                    addImport(PACKAGE, el.toApiDocProperty(config))
                }
            }
            .addFunction(dslFunction().build())
            .addType(docBuilder)
    }

    fun build() {
        apiDocBuilder.build()
        file().build().writeTo(env.makeFile())
    }
}
