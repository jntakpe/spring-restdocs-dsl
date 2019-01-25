package com.github.jntakpe.restdocs.dsl

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.springframework.restdocs.payload.JsonFieldType

object NestedSpec : Spek({
                             describe("A simple nested field") {
                                 context("A single level JSON") {
                                     it("should document an array field") {
                                         val name = "array"
                                         val description = "Simple array"
                                         val fields = JsonDescriptor.root {
                                             array(name, description) {}
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(JsonFieldType.ARRAY)
                                     }
                                     it("should document a boolean field") {
                                         val name = "bool"
                                         val description = "Simple boolean"
                                         val fields = JsonDescriptor.root {
                                             boolean(name, description)
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(JsonFieldType.BOOLEAN)
                                     }
                                     it("should document an object field") {
                                         val name = "object"
                                         val description = "Simple object"
                                         val fields = JsonDescriptor.root {
                                             json(name, description) {}
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(JsonFieldType.OBJECT)
                                     }
                                     it("should document a null field") {
                                         val name = "null"
                                         val description = "Simple null"
                                         val fields = JsonDescriptor.root {
                                             nil(name, description)
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(JsonFieldType.NULL)
                                     }
                                     it("should document a number field") {
                                         val name = "number"
                                         val description = "Simple number"
                                         val fields = JsonDescriptor.root {
                                             number(name, description)
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(JsonFieldType.NUMBER)
                                     }
                                     it("should document a string field") {
                                         val name = "string"
                                         val description = "Simple string"
                                         val fields = JsonDescriptor.root {
                                             string(name, description)
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(JsonFieldType.STRING)
                                     }
                                     it("should document a field with a variety of different types") {
                                         val name = "varies"
                                         val description = "Simple varies"
                                         val fields = JsonDescriptor.root {
                                             varies(name, description)
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(JsonFieldType.VARIES)
                                     }
                                     it("should document field with prefix") {
                                         val prefix = "parentObj."
                                         val name = "field"
                                         val descriptor = Text(name, " Some field", false).build(prefix)
                                         assertThat(descriptor.path).isEqualTo("$prefix$name")
                                     }
                                 }
                                 context("is nested") {
                                     it("should create a nested object") {
                                         val fields = JsonDescriptor.root {
                                             json("nested", "nested object") {
                                                 string("child", "child field")
                                             }
                                         }
                                         assertThat(fields).hasSize(2)
                                         assertThat(fields.map { it.path }).containsExactly("nested", "nested.child")
                                     }
                                     it("should create a nested with array") {
                                         val fields = JsonDescriptor.root {
                                             json("nested", "nested object") {
                                                 array("array", "array field") {
                                                     string("child", "child field")
                                                 }
                                             }
                                         }
                                         assertThat(fields).hasSize(3)
                                         assertThat(fields.map { it.path })
                                                 .containsExactly("nested", "nested.array", "nested.array[].child")
                                     }
                                     it("should create a nested multi level object preserving order") {
                                         val fields = JsonDescriptor.root {
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
                                         assertThat(paths)
                                                 .containsExactly("nested",
                                                                  "nested.child",
                                                                  "nested.multi",
                                                                  "nested.multi.faraway",
                                                                  "nested.bool")
                                     }
                                     it("should add predefined fields to nested json with prefix") {
                                         val predefined = JsonDescriptor.root {
                                             number("faraway", "grandchild")
                                         }
                                         val fields = JsonDescriptor.root {
                                             json("nested", "nested object") {
                                                 string("child", "child field")
                                                 json("multi", "second level") {
                                                     fields += predefined
                                                 }
                                                 boolean("bool", "boolean")
                                             }
                                         }
                                         assertThat(fields.map { it.path })
                                                 .containsExactly("nested",
                                                                  "nested.child",
                                                                  "nested.multi",
                                                                  "nested.multi.faraway",
                                                                  "nested.bool")
                                     }
                                 }
                             }
                         })
