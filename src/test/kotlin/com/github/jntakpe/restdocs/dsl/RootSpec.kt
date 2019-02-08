package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.JsonDescriptor.root
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.springframework.restdocs.payload.JsonFieldType.*

object RootSpec : Spek({
    describe("A JSON") {
        context("is empty") {
            it("should have an empty field list") {
                assertThat(root {}).isEmpty()
            }
        }
        context("is flat") {
            it("should create a root string field") {
                assertThat(root {
                    string("root", "Lonely field")
                }).isNotEmpty.hasSize(1)
            }
            it("should not prefix path") {
                val name = "root"
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
                assertThat(fields.map { it.type })
                    .containsExactly(BOOLEAN, NUMBER, STRING)
            }
            it("should set predefined fields to json") {
                val predefined = root {
                    boolean("first", "description")
                    number("second", "description")
                    string("third", "description")
                }
                val fields = root {
                    fields += predefined
                }
                assertThat(fields).hasSize(3)
                assertThat(fields.map { it.type })
                    .containsExactly(BOOLEAN, NUMBER, STRING)
            }
            it("should add predefined fields to json") {
                val predefined = root {
                    boolean("first", "description")
                    number("second", "description")
                    string("third", "description")
                }
                val fields = root {
                    nil("real first", "description")
                    fields += predefined
                    varies("real last", "description")
                }
                assertThat(fields).hasSize(5)
                assertThat(fields.map { it.type })
                    .containsExactly(
                        NULL,
                        BOOLEAN,
                        NUMBER,
                        STRING,
                        VARIES
                    )
            }
        }
    }
})
