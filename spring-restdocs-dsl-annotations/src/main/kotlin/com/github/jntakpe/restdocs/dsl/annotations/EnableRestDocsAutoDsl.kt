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
    val packages: Array<String> = []
) {
    companion object {
        const val PACKAGE = "com.github.jntakpe.restdocs.dsl.annotations.EnableRestDocsAutoDsl"
    }
}