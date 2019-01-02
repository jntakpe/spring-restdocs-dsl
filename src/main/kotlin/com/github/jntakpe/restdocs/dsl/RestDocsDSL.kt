package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.JsonFieldType.BOOLEAN
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.OBJECT
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath

@DslMarker
internal annotation class RestDocsMarker

internal interface Field {

    val name: String
    val type: JsonFieldType
    val description: String
    val optional: Boolean

    fun build(path: String) = fieldWithPath("$path$name").type(type).description(description).opt()

    private fun FieldDescriptor.opt() = takeIf { optional }?.optional() ?: this
}

@RestDocsMarker
abstract class Nested(private val path: String) {

    internal val children = arrayListOf<FieldDescriptor>()

    fun boolean(name: String, description: String, optional: Boolean = false) = field(Bool(name, description, optional))

    fun number(name: String, description: String, optional: Boolean = false) = field(Number(name, description, optional))

    fun string(name: String, description: String, optional: Boolean = false) = field(Text(name, description, optional))

    fun json(name: String, description: String, optional: Boolean = false, block: Json.() -> Unit): Json {
        val fullPath = if (path.isEmpty()) "$name." else "$path$name."
        return nested(Json(name, description, optional, fullPath), block)
    }

    private fun <T : Field> field(field: T) = also { children.add(field.build(path)) }

    private fun <T> nested(field: T, block: T.() -> Unit) where T : Nested, T : Field = field.also {
        it.block()
        children.add(field.build(path))
        children.addAll(field.children)
    }
}

class Root : Nested("")

class Bool(override val name: String, override val description: String, override val optional: Boolean) : Field {
    override val type = BOOLEAN
}

class Json(override val name: String, override val description: String, override val optional: Boolean, path: String)
    : Nested(path), Field {

    override val type = OBJECT
}

class Number(override val name: String, override val description: String, override val optional: Boolean) : Field {
    override val type = NUMBER
}

class Text(override val name: String, override val description: String, override val optional: Boolean) : Field {
    override val type = STRING
}

object JsonDescriptor {
    fun root(init: Root.() -> Unit) = Root().apply { init() }.children
}
