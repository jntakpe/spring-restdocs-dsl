package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.JsonFieldType

/**
 * JSON text field
 * @see Field
 */
class Text(override val name: String, override val description: String, override val views: Views, override val optional: Boolean)
    : Field {

    override val type = JsonFieldType.STRING
}
