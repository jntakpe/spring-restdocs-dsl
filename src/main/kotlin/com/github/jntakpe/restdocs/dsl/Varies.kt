package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.JsonFieldType

/**
 * JSON field without any defined type
 * @see Field
 */
class Varies(override val name: String, override val description: String, override val optional: Boolean) : Field {

    override val type = JsonFieldType.VARIES
}
