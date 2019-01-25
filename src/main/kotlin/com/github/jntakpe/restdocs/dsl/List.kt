package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation

/**
 * Root JSON array
 * @see Nested
 */
class List(description: String) : Nested("[].") {

    init {
        fields.add(PayloadDocumentation.fieldWithPath("[]").description(description).type(JsonFieldType.ARRAY))
    }
}
