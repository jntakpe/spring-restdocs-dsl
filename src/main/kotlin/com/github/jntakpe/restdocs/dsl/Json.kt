package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.JsonFieldType

/**
 * JSON object field
 * @see Nested
 * @see Field
 */
class Json(override val name: String, override val description: String, override val optional: Boolean, path: String)
    : Nested(path), Field {

    override val type = JsonFieldType.OBJECT
}
