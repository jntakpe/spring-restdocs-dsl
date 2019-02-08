package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.Field.Companion.VIEWS_ATTR
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.snippet.Attributes.Attribute

object FieldDescriptorHelperKtSpec : Spek({
    describe("A field descriptor opt function") {
        it("should make field optional") {
            assertThat(fieldWithPath("someField").opt(true).isOptional).isTrue()
        }
        it("should not make field optional") {
            assertThat(fieldWithPath("someField").opt(false).isOptional).isFalse()
        }
    }
    describe("A field descriptor view function") {
        it("should add view to no attr field") {
            val field = fieldWithPath("someField").views(mutableSetOf(Int::class))
            assertThat(field.attributes).containsKey(VIEWS_ATTR).containsValue(mutableSetOf(Int::class))
        }
        it("should add view to empty views") {
            val field = fieldWithPath("someField").attributes(Attribute(VIEWS_ATTR, mutableSetOf<View>()))
            val attributes = field.views(mutableSetOf(Int::class)).attributes
            assertThat(attributes).containsKey(VIEWS_ATTR).containsValue(mutableSetOf(Int::class))
        }
        it("should add view to existing views") {
            val field = fieldWithPath("someField").attributes(Attribute(VIEWS_ATTR, mutableSetOf(String::class)))
            val attributes = field.views(mutableSetOf(Int::class)).attributes
            assertThat(attributes).containsKey(VIEWS_ATTR).containsValue(mutableSetOf(Int::class, String::class))
        }
        it("should not edit existing views") {
            val field = fieldWithPath("someField").attributes(Attribute(VIEWS_ATTR, mutableSetOf(String::class)))
            val attributes = field.views(mutableSetOf()).attributes
            assertThat(attributes).containsKey(VIEWS_ATTR).containsValue(mutableSetOf(String::class))
        }
    }
})
