package com.github.jntakpe.restdocs.dsl.processor.test

import com.github.jntakpe.restdocs.dsl.discoveredByPackageDoc
import com.github.jntakpe.restdocs.dsl.frodoDoc
import com.github.jntakpe.restdocs.dsl.processor.test.custom.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.springframework.restdocs.payload.JsonFieldType.*

object DslConfigurationTest : Spek({
    describe("Auto dsl configuration test with @EnableRestDocsAutoDsl") {
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
        context("trimming suffixes") {
            it("should generate a frodo and sam functions out of FrodoApiDto and SamDto") {
                sam { age = "Sam's age" }
                frodo { friend = "Always Sam" }
                assertThat(frodoDoc.map { it.path }).containsExactly(
                    FrodoApiDto::friend.name,
                    "${FrodoApiDto::friend.name}.${SamDto::age.name}"
                )
                assertThat(frodoDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(frodoDoc.map { it.isOptional }).containsOnly(false)
                assertThat(frodoDoc.map { it.type }).containsExactly(OBJECT, NUMBER)
                assertThat(frodoDoc.map { it.description }).containsExactly(
                    "Always Sam",
                    "Sam's age"
                )
            }
        }
    }
})