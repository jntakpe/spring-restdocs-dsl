package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.JsonDescriptor.root
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.springframework.restdocs.payload.JsonFieldType.BOOLEAN
import org.springframework.restdocs.payload.JsonFieldType.NULL

object ViewFieldSpec : Spek({
    describe("A field descriptor list") {
        context("with view") {
            it("should keep field since no view specified") {
                val fields = root {
                    boolean("first", "description")
                }.withView(Int::class)
                assertThat(fields).isNotEmpty
            }
            it("should remove field since no view specified and strict") {
                val fields = root {
                    boolean("first", "description")
                }.withView(Int::class, true)
                assertThat(fields).isEmpty()
            }
            it("should remove one field since view does not match") {
                val fields = root {
                    boolean("first", "description", String::class, Boolean::class)
                }.withView(Int::class)
                assertThat(fields).isEmpty()
            }
            it("should keep field since exact match") {
                val fields = root {
                    boolean("first", "description", Int::class)
                }.withView(Int::class)
                assertThat(fields).isNotEmpty
            }
            it("should keep field since one match") {
                val fields = root {
                    boolean("first", "description", String::class, Int::class)
                }.withView(Int::class)
                assertThat(fields).isNotEmpty
            }
        }
        context("without view") {
            it("should keep field since no view specified") {
                val fields = root {
                    boolean("first", "description")
                }.withoutView(Int::class)
                assertThat(fields).isNotEmpty
            }
            it("should remove field since no view specified and strict") {
                val fields = root {
                    boolean("first", "description")
                }.withoutView(Int::class, true)
                assertThat(fields).isEmpty()
            }
            it("should remove one field since view does match") {
                val fields = root {
                    boolean("first", "description", String::class, Int::class)
                }.withoutView(Int::class)
                assertThat(fields).isEmpty()
            }
            it("should keep field since view does not match") {
                val fields = root {
                    boolean("first", "description", String::class)
                }.withoutView(Int::class)
                assertThat(fields).isNotEmpty
            }
            it("should remove field since one match") {
                val fields = root {
                    boolean("first", "description", String::class, Int::class)
                }.withoutView(Int::class)
                assertThat(fields).isEmpty()
            }
        }
        context("with optional") {
            it("should map field to optional") {
                val fields = root {
                    boolean("first", "description", String::class)
                }.withOptional(String::class)
                assertThat(fields.first().isOptional).isTrue()
            }
            it("should not edit field if no view specified") {
                val fields = root {
                    boolean("first", "description")
                }.withOptional(String::class)
                assertThat(fields.first().isOptional).isFalse()
            }
            it("should not edit field if no view specified") {
                val fields = root {
                    boolean("first", "description", Int::class)
                }.withOptional(String::class)
                assertThat(fields.first().isOptional).isFalse()
            }
        }
        context("with null") {
            it("should map field to type null") {
                val fields = root {
                    boolean("first", "description", String::class)
                }.withNull(String::class)
                assertThat(fields.first().type).isEqualTo(NULL)
            }
            it("should not edit field to null if no view specified") {
                val fields = root {
                    boolean("first", "description")
                }.withNull(String::class)
                assertThat(fields.first().type).isEqualTo(BOOLEAN)
            }
            it("should not edit field to null if no view specified") {
                val fields = root {
                    boolean("first", "description", Int::class)
                }.withNull(String::class)
                assertThat(fields.first().type).isEqualTo(BOOLEAN)
            }
        }
    }
})
