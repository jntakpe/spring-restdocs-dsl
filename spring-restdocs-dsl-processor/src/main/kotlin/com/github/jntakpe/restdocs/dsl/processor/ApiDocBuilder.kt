package com.github.jntakpe.restdocs.dsl.processor

import com.github.jntakpe.restdocs.dsl.annotations.Doc
import com.github.jntakpe.restdocs.dsl.processor.ElementDslBuilder.Companion.kClassifierType
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import java.time.OffsetDateTime
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror

/**
 * Generates ApiDoc.kt file holding all properties with documentation of any field annotated with [Doc].
 * Contains also properties for external type (not annotated with [Doc]) with 2 variants :
 * one suffixed with Type for specifying that it is (de)serialized into/from a raw Type
 * (like [OffsetDateTime] is serialized into a String most of the time)
 * and one suffixed with Doc if it comes from a third party library (e.g. Spring) and cannot be annotated with [Doc]
 */
class ApiDocBuilder(private val env: ProcessingEnvironment) {

    companion object {
        const val PACKAGE = "com.github.jntakpe.restdocs.dsl"
        const val FILENAME = "ApiDoc"
        fun Element.toApiDocProperty(): String {
            val name = takeIf { isComplexArrayType() }?.singleParameterizedType()?.simpleName() ?: simpleName.toString()
            return "${name.firstLetterToLowerCase()}Doc"
        }

        fun TypeMirror.toApiDocProperty(): String = "${simpleName()!!.firstLetterToLowerCase()}Type"
    }

    val elements: MutableSet<Pair<Element, Boolean>> = mutableSetOf()

    fun addElement(e: Element, asExternal: Boolean = false) = apply {
        (e to asExternal)
            .takeIf { (new, isExt) -> (new.simpleName to isExt) !in elements.map { (e, isE) -> (e.simpleName to isE) } }
            ?.also { elements.add(it) }
    }

    private fun properties(): List<PropertySpec> {
        return elements.flatMap { (el, ext) ->
            val elementDslProperty = el.toDslProperty(mainDoc())
            if (ext) {
                listOf(elementDslProperty, el.asType().toDslProperty(extField()))
            } else listOf(elementDslProperty)
        }
    }

    private fun Element.toDslProperty(delegates: CodeBlock): PropertySpec {
        return PropertySpec.builder(toApiDocProperty(), ElementDslBuilder.fieldsType)
            .mutable()
            .delegate(delegates)
            .build()
    }

    private fun TypeMirror.toDslProperty(delegates: CodeBlock): PropertySpec {
        return PropertySpec.builder(toApiDocProperty(), kClassifierType)
            .mutable()
            .delegate(delegates)
            .build()
    }

    private fun mainDoc() = CodeBlock.builder().addStatement("mainDoc()").build()

    private fun extField() = CodeBlock.builder().addStatement("extField()").build()

    private fun file(): FileSpec.Builder {
        return FileSpec.builder(PACKAGE, FILENAME)
            .addImport("$PACKAGE.core", "mainDoc")
            .addImport("$PACKAGE.core", "extField")
            .apply { properties().forEach { addProperty(it) } }
    }

    fun build() = file().build().writeTo(env.makeFile())
}