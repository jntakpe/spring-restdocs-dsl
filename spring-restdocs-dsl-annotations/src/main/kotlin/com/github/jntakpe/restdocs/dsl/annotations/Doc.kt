package com.github.jntakpe.restdocs.dsl.annotations

import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * Annotate a class to automatically generate a DSL from it in order to document it
 */
@Target(CLASS)
@Retention(SOURCE)
@Inherited
annotation class Doc {
    companion object {
        const val PACKAGE = "com.github.jntakpe.restdocs.dsl.annotations.Doc"
    }
}