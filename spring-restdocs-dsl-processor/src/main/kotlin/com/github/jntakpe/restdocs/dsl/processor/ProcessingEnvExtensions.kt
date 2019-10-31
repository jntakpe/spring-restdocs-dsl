package com.github.jntakpe.restdocs.dsl.processor

import com.github.jntakpe.restdocs.dsl.processor.ApiDocumentedProcessor.Companion.KAPT_KOTLIN_GENERATED_OPTION_NAME
import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.tools.Diagnostic.Kind.ERROR

fun ProcessingEnvironment.generatedDir(): String {
    return options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: error("Target directory for generated Dsl not found")
}

fun ProcessingEnvironment.makeFile() = File(generatedDir()).apply { mkdir() }

fun ProcessingEnvironment.error(msg: String): Nothing {
    messager.printMessage(ERROR, msg)
    throw IllegalStateException(msg)
}

inline fun <reified T : Annotation> RoundEnvironment.findClassesAnnotatedBy(): Set<Element> = getElementsAnnotatedWith(T::class.java)