package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.JsonFieldType

/**
 * JSON boolean field
 * @see Field
 */
class Bool(override val name: String, override val description: String, override val optional: Boolean) : Field {

    override val type = JsonFieldType.BOOLEAN
}
