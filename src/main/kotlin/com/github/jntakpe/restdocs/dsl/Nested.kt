package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.Field.Companion.VIEWS_ATTR
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath

/**
 * Represent nested field like a JSON object or an array
 * @param basePath base path of parents objects
 */
@RestDocsMarker
abstract class Nested(private val basePath: String, private val views: Views = mutableSetOf()) {

    val fields: MutableList<FieldDescriptor> = arrayListOf()

    fun array(name: String, description: String, vararg view: View, optional: Boolean = false): Array {
        return field(Array(name, description, view.toMutableSet().apply { addAll(views) }, optional, arrayPath(name)))
    }

    fun array(
        name: String,
        description: String,
        vararg view: View,
        optional: Boolean = false,
        block: Array.() -> Unit
    ): Array {
        return nested(
            Array(name, description, view.toMutableSet().apply { addAll(views) }, optional, arrayPath(name)),
            block
        )
    }

    fun boolean(name: String, description: String, vararg view: View, optional: Boolean = false): Bool {
        return field(Bool(name, description, view.toMutableSet().apply { addAll(views) }, optional))
    }

    fun nil(name: String, description: String, vararg view: View, optional: Boolean = false): Nil {
        return field(Nil(name, description, view.toMutableSet().apply { addAll(views) }, optional))
    }

    fun number(name: String, description: String, vararg view: View, optional: Boolean = false): Number {
        return field(Number(name, description, view.toMutableSet().apply { addAll(views) }, optional))
    }

    fun string(name: String, description: String, vararg view: View, optional: Boolean = false): Text {
        return field(Text(name, description, view.toMutableSet().apply { addAll(views) }, optional))
    }

    fun json(
        name: String,
        description: String,
        vararg view: View,
        optional: Boolean = false,
        block: Json.() -> Unit
    ): Json {
        return nested(
            Json(name, description, view.toMutableSet().apply { addAll(views) }, optional, "$basePath$name."),
            block
        )
    }

    fun varies(name: String, description: String, vararg view: View, optional: Boolean = false): Varies {
        return field(Varies(name, description, view.toMutableSet().apply { addAll(views) }, optional))
    }

    operator fun MutableCollection<in FieldDescriptor>.plusAssign(elements: Iterable<FieldDescriptor>) {
        addAll(elements.map { it.rebase() })
    }

    private fun arrayPath(name: String) = "$basePath$name[]."

    private fun <T : Field> field(field: T): T = field.also { fields.add(buildField(field)) }

    private fun <T> nested(field: T, block: T.() -> Unit) where T : Nested, T : Field = field.also {
        it.block()
        fields.add(buildField(field))
        fields.addAll(field.fields)
    }

    private fun <T : Field> buildField(field: T) = field.apply { views.addAll(this@Nested.views) }.build(basePath)

    private fun FieldDescriptor.rebase(): FieldDescriptor {
        val mergedViews = ((attributes[VIEWS_ATTR] as? Views ?: mutableSetOf()) + views).toMutableSet()
        return fieldWithPath("$basePath$path").type(type).description(description).opt(isOptional).views(mergedViews)
    }
}
