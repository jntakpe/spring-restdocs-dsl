package com.github.jntakpe.restdocs.dsl.processor

import com.github.jntakpe.restdocs.dsl.annotations.EnableRestDocsAutoDsl

fun String.firstLetterToLowerCase(): String = this[0].toLowerCase() + substring(1 until length)

/**
 * Removes trailing suffixes from String as configured in [EnableRestDocsAutoDsl.trimSuffixes].
 * Code is ugly on purpose otherwise fails because of javax processing
 */
fun String.removeTrailingSuffixes(config: List<EnableRestDocsAutoDsl>): String {
    val trimSuffixes = config.flatMap { it.trimSuffixes.toList() }
    return if (trimSuffixes.any { endsWith(it) }) {
        val matchingSuffix = trimSuffixes.filter { endsWith(it) }.maxBy { it.length }!!
        substringBefore(matchingSuffix)
    } else this
}