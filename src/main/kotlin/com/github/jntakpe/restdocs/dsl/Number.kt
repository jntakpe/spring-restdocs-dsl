package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.JsonFieldType

/**
 * JSON number field
 * @see Number
 */
class Number(override val name: String, override val description: String, override val views: Views, override val optional: Boolean)
    : Field {

    override val type = JsonFieldType.NUMBER
}
