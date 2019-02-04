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

    fun array(name: String, description: String, vararg view: View, optional: Boolean = false): Array {
        return field(Array(name, description, view.toSet(), optional, arrayPath(name)))
    }

    fun array(name: String, description: String, vararg view: View, optional: Boolean = false, block: Array.() -> Unit): Array {
        return nested(Array(name, description, view.toSet(), optional, arrayPath(name)), block)
    }

    fun boolean(name: String, description: String, vararg view: View, optional: Boolean = false): Bool {
        return field(Bool(name, description, view.toSet(), optional))
    }

    fun nil(name: String, description: String, vararg view: View, optional: Boolean = false): Nil {
        return field(Nil(name, description, view.toSet(), optional))
    }

    fun number(name: String, description: String, vararg view: View, optional: Boolean = false): Number {
        return field(Number(name, description, view.toSet(), optional))
    }

    fun string(name: String, description: String, vararg view: View, optional: Boolean = false): Text {
        return field(Text(name, description, view.toSet(), optional))
    }

    fun json(name: String, description: String, vararg view: View, optional: Boolean = false, block: Json.() -> Unit): Json {
        return nested(Json(name, description, view.toSet(), optional, "$basePath$name."), block)
    }

    fun varies(name: String, description: String, vararg view: View, optional: Boolean = false): Varies {
        return field(Varies(name, description, view.toSet(), optional))
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