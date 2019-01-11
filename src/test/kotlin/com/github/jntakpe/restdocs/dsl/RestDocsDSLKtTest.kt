package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.JsonDescriptor.list
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

object RestDocsDSLSpec
    : Spek({
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
                           assertThat(fields.map { it.type }).containsExactly(BOOLEAN, NUMBER, STRING)
                       }
                       it("should set predefined fields to json") {
                           val predefined = root {
                               boolean("first", "description")
                               number("second", "description")
                               string("third", "description")
                           }
                           val fields = JsonDescriptor.root {
                               fields += predefined
                           }
                           assertThat(fields).hasSize(3)
                           assertThat(fields.map { it.type }).containsExactly(BOOLEAN, NUMBER, STRING)
                       }
                       it("should add predefined fields to json") {
                           val predefined = root {
                               boolean("first", "description")
                               number("second", "description")
                               string("third", "description")
                           }
                           val fields = JsonDescriptor.root {
                               nil("real first", "description")
                               fields += predefined
                               varies("real last", "description")
                           }
                           assertThat(fields).hasSize(5)
                           assertThat(fields.map { it.type }).containsExactly(NULL, BOOLEAN, NUMBER, STRING, VARIES)
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
                       it("should create a nested with array") {
                           val fields = root {
                               json("nested", "nested object") {
                                   array("array", "array field") {
                                       string("child", "child field")
                                   }
                               }
                           }
                           assertThat(fields).hasSize(3)
                           assertThat(fields.map { it.path }).containsExactly("nested", "nested.array", "nested.array[].child")
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
                                   .containsExactly("nested", "nested.child", "nested.multi", "nested.multi.faraway", "nested.bool")
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
                                   .containsExactly("nested", "nested.child", "nested.multi", "nested.multi.faraway", "nested.bool")
                       }
                   }
                   context("is array") {
                       it("should create an array with fields and a new array field") {
                           val fields = list("Simple array") {
                               boolean("first", "description")
                               number("second", "description")
                           }
                           assertThat(fields.map { it.path }).containsExactly("[]", "[].first", "[].second")
                       }
                       it("should add predefined fields with array prefix") {
                           val predefined = root {
                               boolean("first", "description")
                               number("second", "description")
                               string("third", "description")
                           }
                           val fields = list("Simple array") { fields += predefined }
                           assertThat(fields.map { it.path }.drop(1)).allMatch { it.startsWith("[].") }.contains("[].first")
                       }
                       it("should create an array preserving order") {
                           val fields = list("Simple array") {
                               boolean("first", "description")
                               number("second", "description")
                               string("third", "description")
                           }
                           assertThat(fields).hasSize(4)
                           assertThat(fields.map { it.type }).containsExactly(ARRAY, BOOLEAN, NUMBER, STRING)
                       }
                       it("should create a primitive type array") {
                           assertThat(root { array("primitiveArray", "") }.map { it.path }).containsExactly("primitiveArray")
                       }
                       it("should create an array with nested json") {
                           val fields = list("Simple array") {
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
