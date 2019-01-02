package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.JsonDescriptor.root
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.springframework.restdocs.payload.JsonFieldType.BOOLEAN
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.STRING

object RestDocsDSLSpec : Spek(
        {
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
                    it("should document field with prefix") {
                        val prefix = "parentObj."
                        val name = "field"
                        val descriptor = Text(name, " Some field", false).build(prefix)
                        assertThat(descriptor.path).isEqualTo("$prefix$name")
                    }
                }
            }
            describe("A JSON") {
                context("is empty") {
                    it("should have an empty field list") {
                        assertThat(root {}).isEmpty()
                    }
                }
                context("is flat") {
                    it("should create a single string field") {
                        assertThat(root {
                            string("single", "Lonely field")
                        }).isNotEmpty.hasSize(1)
                    }
                    it("should not prefix path") {
                        val name = "single"
                        assertThat(root {
                            string(name, "Lonely field")
                        }.first().path).isEqualTo(name)
                    }
                    it("should create multiple fields preserving order") {
                        val paths = listOf("first", "second", "third")
                        val fields = root {
                            string(paths[0], "description")
                            string(paths[1], "description")
                            string(paths[2], "description")
                        }
                        assertThat(fields).hasSize(3)
                        assertThat(fields.map { it.path }).containsExactlyElementsOf(paths)
                    }
                    it("should create multiple fields of different type") {
                        val fields = root {
                            boolean("first", "description")
                            number("second", "description")
                            string("third", "description")
                        }
                        assertThat(fields).hasSize(3)
                        assertThat(fields.map { it.type }).containsExactly(BOOLEAN, NUMBER, STRING)
                    }
                }
                context("is nested") {
                    it("should create a nested object") {
                        val fields = root {
                            json("nested", "nested object") {
                                string("child", "child field")
                            }
                        }
                        assertThat(fields).hasSize(2)
                        assertThat(fields.map { it.path }).containsExactly("nested", "nested.child")
                    }
                    it("should create a nested multi level object preserving order") {
                        val fields = root {
                            json("nested", "nested object") {
                                string("child", "child field")
                                json("multi", "second level") {
                                    number("faraway", "grandchild")
                                }
                                boolean("bool", "boolean")
                            }
                        }
                        assertThat(fields).hasSize(5)
                        val paths = fields.map { it.path }
                        assertThat(paths).containsExactly("nested", "nested.child", "nested.multi", "nested.multi.faraway", "nested.bool")
                    }
                }
            }
        })
