package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation

/**
 * Represent a simple field used to document non-nested fields
 */
internal interface Field {

    /**
     * Name of the field to document. Represent the relative path of the field.
     */
    val name: String
    /**
     * JSON type of the field
     */
    val type: JsonFieldType
    /**
     * Documentation describing the field
     */
    val description: String
    /**
     * Indicates if the field is optional
     */
    val optional: Boolean

    /**
     * Builds a Spring REST Docs [FieldDescriptor] object prefixing the field with the [path] parameter
     * @param path prefix to apply to field [name]
     * @return Spring REST Docs [FieldDescriptor]
     */
    fun build(path: String) = PayloadDocumentation.fieldWithPath("$path$name").type(type).description(description).opt()

    private fun FieldDescriptor.opt() = takeIf { optional }?.optional() ?: this
}
