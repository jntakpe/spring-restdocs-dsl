package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.snippet.Attributes.Attribute

/**
 * Represent a simple field used to document non-nested fields
 */
internal interface Field {

    companion object {
        const val VIEWS_ATTR = "jsonViews"
    }

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
     * Views that applies to the field
     */
    val views: Views

    /**
     * Builds a Spring REST Docs [FieldDescriptor] object prefixing the field with the [path] parameter
     * @param path prefix to apply to field [name]
     * @return Spring REST Docs [FieldDescriptor]
     */
    fun build(path: String) = fieldWithPath("$path$name").type(type).description(description).opt().attr()

    private fun FieldDescriptor.opt() = takeIf { optional }?.optional() ?: this

    private fun FieldDescriptor.attr() = takeIf { views.isNotEmpty() }?.attributes(Attribute(VIEWS_ATTR, views)) ?: this
}
