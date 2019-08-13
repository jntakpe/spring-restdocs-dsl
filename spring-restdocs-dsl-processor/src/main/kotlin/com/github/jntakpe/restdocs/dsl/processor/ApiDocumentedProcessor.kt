package com.github.jntakpe.restdocs.dsl.processor

import com.github.jntakpe.restdocs.dsl.annotations.Doc
import com.github.jntakpe.restdocs.dsl.processor.ApiDocumentedProcessor.Companion.KAPT_KOTLIN_GENERATED_OPTION_NAME
import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * Processor intercepting classes annotated with [Doc] and generating a DSL from them
 */
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(Doc.PACKAGE)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class ApiDocumentedProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val apiDocBuilder = ApiDocBuilder(processingEnv)
        roundEnv.findClasses<Doc>().toList()
            .map { ElementDslBuilder(it, processingEnv, apiDocBuilder) }
            .forEach { it.build() }
        return true
    }
}