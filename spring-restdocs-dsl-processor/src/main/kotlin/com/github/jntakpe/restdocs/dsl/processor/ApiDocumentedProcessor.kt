package com.github.jntakpe.restdocs.dsl.processor

import com.github.jntakpe.restdocs.dsl.annotations.Doc
import com.github.jntakpe.restdocs.dsl.annotations.EnableRestDocsAutoDsl
import com.github.jntakpe.restdocs.dsl.processor.ApiDocumentedProcessor.Companion.KAPT_KOTLIN_GENERATED_OPTION_NAME
import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion.RELEASE_8
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

/**
 * Processor intercepting classes annotated with [Doc] and generating a DSL from them
 */
@AutoService(Processor::class)
@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationTypes(EnableRestDocsAutoDsl.PACKAGE)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class ApiDocumentedProcessor : AbstractProcessor() {

    private lateinit var elements: Elements

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        elements = processingEnv.elementUtils
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val enableRestDocsAutoDsl = roundEnv.enableRestDocsAutoDsl()
        val apiDocBuilder = ApiDocBuilder(processingEnv, enableRestDocsAutoDsl)
        val allClasses = roundEnv.findClassesByDocAnnotation() + enableRestDocsAutoDsl.findClassesByPackage()
        allClasses
            .map { ElementDslBuilder(it, processingEnv, apiDocBuilder, enableRestDocsAutoDsl) }
            .forEach { it.build() }
        return true
    }

    private fun RoundEnvironment.findClassesByDocAnnotation() = findClassesAnnotatedBy<Doc>().toList()

    private fun List<EnableRestDocsAutoDsl>.findClassesByPackage(): List<Element> {
        return flatMap { it.packages.toList() }
            .flatMap { elements.getPackageElement(it).enclosedElements }
    }

    private fun RoundEnvironment.enableRestDocsAutoDsl(): List<EnableRestDocsAutoDsl> {
        return findClassesAnnotatedBy<EnableRestDocsAutoDsl>()
            .map { it.getAnnotation(EnableRestDocsAutoDsl::class.java) }
    }
}