package com.github.jntakpe.restdocs.dsl.processor.test

import com.github.jntakpe.restdocs.dsl.discoveredByPackageDoc
import com.github.jntakpe.restdocs.dsl.processor.test.custom.DiscoveredByPackage
import com.github.jntakpe.restdocs.dsl.processor.test.custom.discoveredByPackage
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.springframework.restdocs.payload.JsonFieldType.STRING

object PackageDiscoveryTest : Spek({
    describe("Auto dsl by package discovery test") {
        context("in custom package") {
            it("should generate a valid dsl out of DiscoveredByPackage class") {
                discoveredByPackage { name = "By package" }
                assertThat(discoveredByPackageDoc.map { it.path }).containsExactly(DiscoveredByPackage::name.name)
                assertThat(discoveredByPackageDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(discoveredByPackageDoc.map { it.isOptional }).containsExactly(false)
                assertThat(discoveredByPackageDoc.map { it.type }).containsExactly(STRING)
                assertThat(discoveredByPackageDoc.map { it.description }).containsExactly("By package")
            }
        }
    }
})