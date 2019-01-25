package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.Field.Companion.VIEWS_ATTR
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object FieldSpec : Spek({
                            describe("A documented field") {
                                context("default build implementation") {
                                    it("should document field without prefix") {
                                        val name = "field"
                                        val description = "Some field"
                                        val descriptor = Text(name, description, false, emptySet()).build("")
                                        assertThat(descriptor.path).isEqualTo(name)
                                        assertThat(descriptor.isOptional).isFalse()
                                        assertThat(descriptor.description).isEqualTo(description)
                                    }
                                    it("should document optional field") {
                                        val descriptor = Text("field", "Some field", true, emptySet()).build("")
                                        assertThat(descriptor.isOptional).isTrue()
                                    }
                                    it("should add view attribute") {
                                        val views = setOf(String::class, Int::class)
                                        val descriptor = Text("field", "Some field", true, views).build("")
                                        assertThat(descriptor.attributes).isNotEmpty
                                        assertThat(descriptor.attributes[VIEWS_ATTR]).isInstanceOf(Set::class.java)
                                        assertThat(descriptor.attributes[VIEWS_ATTR] as Views).hasSameSizeAs(views).containsAll(views)
                                    }
                                }
                            }
                        })
