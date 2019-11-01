package com.github.jntakpe.restdocs.dsl.annotations

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * Configures globally restdocs autodsl
 */
@Target(CLASS)
@Retention(SOURCE)
annotation class EnableRestDocsAutoDsl(
    /**
     * Packages to scan containing classes to generate DSLs from
     */
    val packages: Array<String> = [],
    /**
     * Case insensitive list of suffixes to remove from generated Dsl functions
     * e.g. given an PetDto class, instead of generating `petDto {}` will generate a `pet {}` function
     */
    val trimSuffixes: Array<String> = []
) {
    companion object {
        const val PACKAGE = "com.github.jntakpe.restdocs.dsl.annotations.EnableRestDocsAutoDsl"
    }
}