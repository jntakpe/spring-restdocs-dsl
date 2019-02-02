package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.JsonFieldType

/**
 * JSON array field
 * @see Nested
 * @see Field
 */
class Array(override val name: String,
            override val description: String,
            override val views: Views,
            override val optional: Boolean,
            path: String) : Nested(path), Field {

    override val type = JsonFieldType.ARRAY
}
