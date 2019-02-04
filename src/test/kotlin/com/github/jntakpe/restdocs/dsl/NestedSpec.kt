package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.JsonDescriptor.root
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.springframework.restdocs.payload.JsonFieldType.ARRAY
import org.springframework.restdocs.payload.JsonFieldType.BOOLEAN
import org.springframework.restdocs.payload.JsonFieldType.NULL
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.OBJECT
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.JsonFieldType.VARIES

object NestedSpec : Spek({
                             describe("A simple nested field") {
                                 context("A single level JSON") {
                                     it("should document an array field") {
                                         val name = "array"
                                         val description = "Simple array"
                                         val fields = root {
                                             array(name, description) {}
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(ARRAY)
                                     }
                                     it("should document a boolean field") {
                                         val name = "bool"
                                         val description = "Simple boolean"
                                         val fields = root {
                                             boolean(name, description)
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(BOOLEAN)
                                     }
                                     it("should document an object field") {
                                         val name = "object"
                                         val description = "Simple object"
                                         val fields = root {
                                             json(name, description) {}
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(OBJECT)
                                     }
                                     it("should document a null field") {
                                         val name = "null"
                                         val description = "Simple null"
                                         val fields = root {
                                             nil(name, description)
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(NULL)
                                     }
                                     it("should document a number field") {
                                         val name = "number"
                                         val description = "Simple number"
                                         val fields = root {
                                             number(name, description)
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(NUMBER)
                                     }
                                     it("should document a string field") {
                                         val name = "string"
                                         val description = "Simple string"
                                         val fields = root {
                                             string(name, description)
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(STRING)
                                     }
                                     it("should document a field with a variety of different types") {
                                         val name = "varies"
                                         val description = "Simple varies"
                                         val fields = root {
                                             varies(name, description)
                                         }
                                         assertThat(fields).hasSize(1)
                                         val field = fields.first()
                                         assertThat(field.path).isEqualTo(name)
                                         assertThat(field.description).isEqualTo(description)
                                         assertThat(field.type).isEqualTo(VARIES)
                                     }
                                     it("should document field with prefix") {
                                         val prefix = "parentObj."
                                         val name = "field"
                                         val descriptor = Text(name, " Some field", mutableSetOf(), false).build(prefix)
                                         assertThat(descriptor.path).isEqualTo("$prefix$name")
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
                                     it("should create a nested object propagating views to child") {
                                         val fields = root {
                                             json("nested", "nested object", String::class) {
                                                 string("child", "child field")
                                             }
                                         }
                                         assertThat(fields).allMatch { it.attributes.containsKey(Field.VIEWS_ATTR) }
                                     }
                                     it("should create a nested with array") {
                                         val fields = root {
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
                                         assertThat(paths)
                                                 .containsExactly("nested",
                                                                  "nested.child",
                                                                  "nested.multi",
                                                                  "nested.multi.faraway",
                                                                  "nested.bool")
                                     }
                                     it("should add predefined fields to nested json with prefix") {
                                         val predefined = root {
                                             number("faraway", "grandchild")
                                         }
                                         val fields = root {
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
