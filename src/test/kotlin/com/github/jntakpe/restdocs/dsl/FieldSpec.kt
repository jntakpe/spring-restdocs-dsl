package com.github.jntakpe.restdocs.dsl

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object FieldSpec : Spek({
                            describe("A documented field") {
                                context("default build implementation") {
                                    it("should document field without prefix") {
                                        val name = "field"
                                        val description = "Some field"
                                        val descriptor = Text(name, description, false).build("")
                                        assertThat(descriptor.path).isEqualTo(name)
                                        assertThat(descriptor.isOptional).isFalse()
                                        assertThat(descriptor.description).isEqualTo(description)
                                    }
                                    it("should document optional field") {
                                        val descriptor = Text("field", "Some field", true).build("")
                                        assertThat(descriptor.isOptional).isTrue()
                                    }
                                }
                            }
                        })
