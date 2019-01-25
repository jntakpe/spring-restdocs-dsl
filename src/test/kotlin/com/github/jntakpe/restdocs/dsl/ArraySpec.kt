package com.github.jntakpe.restdocs.dsl

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.springframework.restdocs.payload.JsonFieldType

object ArraySpec : Spek({
                            describe("An array") {
                                context("is flat") {
                                    it("should create an array with fields and a new array field") {
                                        val fields = JsonDescriptor.list("Simple array") {
                                            boolean("first", "description")
                                            number("second", "description")
                                        }
                                        assertThat(fields.map { it.path }).containsExactly("[]", "[].first", "[].second")
                                    }
                                    it("should add predefined fields with array prefix") {
                                        val predefined = JsonDescriptor.root {
                                            boolean("first", "description")
                                            number("second", "description")
                                            string("third", "description")
                                        }
                                        val fields = JsonDescriptor.list("Simple array") { fields += predefined }
                                        assertThat(fields.map { it.path }.drop(1)).allMatch { it.startsWith("[].") }
                                                .contains("[].first")
                                    }
                                    it("should create an array preserving order") {
                                        val fields = JsonDescriptor.list("Simple array") {
                                            boolean("first", "description")
                                            number("second", "description")
                                            string("third", "description")
                                        }
                                        assertThat(fields).hasSize(4)
                                        assertThat(fields.map { it.type })
                                                .containsExactly(JsonFieldType.ARRAY,
                                                                 JsonFieldType.BOOLEAN,
                                                                 JsonFieldType.NUMBER,
                                                                 JsonFieldType.STRING)
                                    }
                                    it("should create a primitive type array") {
                                        assertThat(JsonDescriptor.root { array("primitiveArray", "") }.map { it.path })
                                                .containsExactly("primitiveArray")
                                    }
                                    it("should create an array with nested json") {
                                        val fields = JsonDescriptor.list("Simple array") {
                                            boolean("boolean", "description")
                                            json("nested", "nested") {
                                                number("child", "child")
                                                array("arrayChild", "Nested array") {
                                                    string("multi", "Multi nest")
                                                }
                                            }
                                            string("string", "description")
                                        }
                                        assertThat(fields.map { it.path })
                                                .containsExactly("[]",
                                                                 "[].boolean",
                                                                 "[].nested",
                                                                 "[].nested.child",
                                                                 "[].nested.arrayChild",
                                                                 "[].nested.arrayChild[].multi",
                                                                 "[].string")
                                    }
                                }
                            }
                        })
