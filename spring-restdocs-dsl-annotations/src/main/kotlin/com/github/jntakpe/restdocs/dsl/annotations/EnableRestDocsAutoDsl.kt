package com.github.jntakpe.restdocs.dsl.annotations

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * Configures globally restdocs autodsl
 */
@Target(CLASS)
@Retention(SOURCE)
annotation class EnableRestDocsAutoDsl(val value: Array<String> = [])