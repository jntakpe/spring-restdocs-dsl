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
                val descriptor = Text(name, description, mutableSetOf(), false).build("")
                assertThat(descriptor.path).isEqualTo(name)
                assertThat(descriptor.isOptional).isFalse()
                assertThat(descriptor.description).isEqualTo(description)
            }
            it("should document optional field") {
                val descriptor = Text("field", "Some field", mutableSetOf(), true).build("")
                assertThat(descriptor.isOptional).isTrue()
            }
            it("should add view attribute") {
                val views: Views = mutableSetOf(String::class, Int::class)
                val descriptor = Text("field", "Some field", views, true).build("")
                assertThat(descriptor.attributes).isNotEmpty
                assertThat(descriptor.attributes[VIEWS_ATTR]).isInstanceOf(Set::class.java)
                assertThat(descriptor.attributes[VIEWS_ATTR] as Views).hasSameSizeAs(views).containsAll(views)
            }
            it("should preserve original views") {
                val predefined = JsonDescriptor.root {
                    boolean("first", "description", Enum::class)
                    number("second", "description", Int::class, String::class)
                    string("third", "description")
                }
                val fields = JsonDescriptor.root {
                    nil("real first", "description")
                    fields += predefined
                    varies("real last", "description")
                }
                val first = fields.find { it.path == "first" }?.attributes
                assertThat(first).isNotEmpty
                assertThat(first!![VIEWS_ATTR]).isNotNull.isInstanceOf(Set::class.java)
                val firstViews = first[VIEWS_ATTR] as Views
                assertThat(firstViews).hasSize(1).contains(Enum::class)
                val second = fields.find { it.path == "second" }?.attributes
                assertThat(second).isNotEmpty
                assertThat(second!![VIEWS_ATTR]).isNotNull.isInstanceOf(Set::class.java)
                val secondViews = second[VIEWS_ATTR] as Views
                assertThat(secondViews).hasSize(2).contains(Int::class, String::class)
            }
            it("should preserve original views in nested object") {
                val predefined = JsonDescriptor.root {
                    boolean("first", "description", Enum::class)
                    number("second", "description", Int::class)
                    string("third", "description")
                }
                val fields = JsonDescriptor.root {
                    nil("real first", "description")
                    json("nested", "description") {
                        fields += predefined
                    }
                    varies("real last", "description")
                }
                val first = fields.find { it.path == "nested.first" }?.attributes
                assertThat(first).isNotEmpty
                assertThat(first!![VIEWS_ATTR]).isNotNull.isInstanceOf(Set::class.java)
                val views = first[VIEWS_ATTR] as Views
                assertThat(views).hasSize(1).contains(Enum::class)
            }
        }
    }
})
