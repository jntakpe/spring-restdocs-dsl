package com.github.jntakpe.restdocs.dsl.processor

fun String.firstLetterToLowerCase(): String = this[0].toLowerCase() + substring(1 until length)
