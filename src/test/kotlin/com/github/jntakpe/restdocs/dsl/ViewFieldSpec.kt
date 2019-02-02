package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.JsonDescriptor.root
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object ViewFieldSpec : Spek({
                                describe("A field descriptor list") {
                                    context("with view") {
                                        it("should keep field since no view specified") {
                                            val fields = root {
                                                boolean("first", "description")
                                            }.withView(Int::class)
                                            assertThat(fields).isNotEmpty
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
                                }
                            })
