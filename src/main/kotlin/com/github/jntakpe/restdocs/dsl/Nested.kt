package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation

/**
 * Represent nested field like a JSON object or an array
 * @param basePath base path of parents objects
 */
@RestDocsMarker
abstract class Nested(private val basePath: String) {

    val fields: MutableList<FieldDescriptor> = arrayListOf()

    fun array(name: String, description: String, optional: Boolean = false, views: Views = emptySet()): Array {
        return field(Array(name, description, optional, views, arrayPath(name)))
    }

    fun array(name: String, description: String, optional: Boolean = false, views: Views = emptySet(), block: Array.() -> Unit): Array {
        return nested(Array(name, description, optional, views, arrayPath(name)), block)
    }

    fun boolean(name: String, description: String, optional: Boolean = false, views: Views = emptySet()): Bool {
        return field(Bool(name, description, optional, views))
    }

    fun nil(name: String, description: String, optional: Boolean = false, views: Views = emptySet()): Nil {
        return field(Nil(name, description, optional, views))
    }

    fun number(name: String, description: String, optional: Boolean = false, views: Views = emptySet()): Number {
        return field(Number(name, description, optional, views))
    }

    fun string(name: String, description: String, optional: Boolean = false, views: Views = emptySet()): Text {
        return field(Text(name, description, optional, views))
    }

    fun json(name: String, description: String, optional: Boolean = false, views: Views = emptySet(), block: Json.() -> Unit): Json {
        return nested(Json(name, description, optional, views, "$basePath$name."), block)
    }

    fun varies(name: String, description: String, optional: Boolean = false, views: Views = emptySet()): Varies {
        return field(Varies(name, description, optional, views))
    }

    operator fun MutableCollection<in FieldDescriptor>.plusAssign(elements: Iterable<FieldDescriptor>) {
        addAll(elements.map { it.rebase() })
    }

    private fun arrayPath(name: String) = "$basePath$name[]."

    private fun <T : Field> field(field: T): T = field.also { fields.add(field.build(basePath)) }

    private fun <T> nested(field: T, block: T.() -> Unit) where T : Nested, T : Field = field.also {
        it.block()
        fields.add(field.build(basePath))
        fields.addAll(field.fields)
    }

    private fun FieldDescriptor.rebase() = PayloadDocumentation.fieldWithPath("$basePath$path").type(type).description(description).opt()

    private fun FieldDescriptor.opt() = takeIf { isOptional }?.optional() ?: this
}
