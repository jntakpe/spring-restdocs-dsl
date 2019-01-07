package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.JsonFieldType.ARRAY
import org.springframework.restdocs.payload.JsonFieldType.BOOLEAN
import org.springframework.restdocs.payload.JsonFieldType.NULL
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.OBJECT
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.JsonFieldType.VARIES
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
abstract class Nested(protected val basePath: String) {

    val fields: MutableList<FieldDescriptor> = arrayListOf()

    fun array(name: String, description: String, optional: Boolean = false, block: Array.() -> Unit): Array {
        val arrayName = "$name[]"
        val fullPath = if (basePath.isEmpty()) "$arrayName." else "$basePath$arrayName."
        return nested(Array(arrayName, description, optional, fullPath), block)
    }

    fun boolean(name: String, description: String, optional: Boolean = false) = field(Bool(name, description, optional))

    fun nil(name: String, description: String, optional: Boolean = false) = field(Nil(name, description, optional))

    fun number(name: String, description: String, optional: Boolean = false) = field(Number(name, description, optional))

    fun string(name: String, description: String, optional: Boolean = false) = field(Text(name, description, optional))

    fun json(name: String, description: String, optional: Boolean = false, block: Json.() -> Unit): Json {
        val fullPath = if (basePath.isEmpty()) "$name." else "$basePath$name."
        return nested(Json(name, description, optional, fullPath), block)
    }

    fun varies(name: String, description: String, optional: Boolean = false) = field(Varies(name, description, optional))

    operator fun MutableCollection<in FieldDescriptor>.plusAssign(elements: Iterable<FieldDescriptor>) {
        addAll(elements.map { it.rebase() })
    }

    private fun <T : Field> field(field: T): T = field.also { fields.add(field.build(basePath)) }

    private fun <T> nested(field: T, block: T.() -> Unit) where T : Nested, T : Field = field.also {
        it.block()
        fields.add(field.build(basePath))
        fields.addAll(field.fields)
    }

    private fun FieldDescriptor.rebase() = fieldWithPath("$basePath$path").type(type).description(description).opt()

    private fun FieldDescriptor.opt() = takeIf { isOptional }?.optional() ?: this
}

class Root : Nested("")

class List(description: String) : Nested("[].") {
    init {
        fields.add(fieldWithPath("[]").description(description).type(ARRAY))
    }
}

class Array(override val name: String, override val description: String, override val optional: Boolean, path: String)
    : Nested(path), Field {

    override val type = ARRAY
}

class Bool(override val name: String, override val description: String, override val optional: Boolean) : Field {
    override val type = BOOLEAN
}

class Json(override val name: String, override val description: String, override val optional: Boolean, path: String)
    : Nested(path), Field {

    override val type = OBJECT
}

class Nil(override val name: String, override val description: String, override val optional: Boolean) : Field {
    override val type = NULL
}

class Number(override val name: String, override val description: String, override val optional: Boolean) : Field {
    override val type = NUMBER
}

class Text(override val name: String, override val description: String, override val optional: Boolean) : Field {
    override val type = STRING
}

class Varies(override val name: String, override val description: String, override val optional: Boolean) : Field {
    override val type = VARIES
}

object JsonDescriptor {
    fun root(init: Root.() -> Unit) = Root().apply { init() }.fields
    fun list(description: String, init: List.() -> Unit) = List(description).apply { init() }.fields
}
