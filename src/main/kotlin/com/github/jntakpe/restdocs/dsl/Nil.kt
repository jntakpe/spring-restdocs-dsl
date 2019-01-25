package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.JsonFieldType

/**
 * JSON null field
 * @see Field
 */
class Nil(override val name: String, override val description: String, override val optional: Boolean) : Field {

    override val type = JsonFieldType.NULL
}
