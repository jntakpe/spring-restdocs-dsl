package com.github.jntakpe.restdocs.dsl.processor.test

import com.github.jntakpe.restdocs.dsl.annotations.EnableRestDocsAutoDsl

@EnableRestDocsAutoDsl(
    packages = ["com.github.jntakpe.restdocs.dsl.processor.test.custom"],
    trimSuffixes = ["ApiDto", "Dto"]
)
class App