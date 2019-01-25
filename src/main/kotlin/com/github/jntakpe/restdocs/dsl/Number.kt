package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.JsonFieldType

/**
 * JSON number field
 * @see Number
 */
class Number(override val name: String, override val description: String, override val optional: Boolean, override val views: Views)
    : Field {

    override val type = JsonFieldType.NUMBER
}
