package com.github.jntakpe.restdocs.dsl

import com.fasterxml.jackson.annotation.JsonView
import com.github.jntakpe.restdocs.dsl.Field.Companion.VIEWS_ATTR
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

typealias ArrayBlock = Array.() -> Unit
typealias JsonBlock = Json.() -> Unit

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
        block: ArrayBlock
    ): Array {
        return nested(
            Array(name, description, view.toMutableSet().apply { addAll(views) }, optional, arrayPath(name)),
            block
        )
    }

    fun array(
        property: KProperty<*>,
        description: String,
        of: MutableList<FieldDescriptor>? = null,
        block: ArrayBlock? = null
    ): Array {
        // Looks ugly but functions literals are a mess to cast
        val b: ArrayBlock? = block ?: if (of != null) {
            { fields += of!! }
        } else {
            null as? ArrayBlock
        }
        return b?.let {
            property.run { array(name, description, *viewsArray(), optional = isOptional(), block = it) }
        } ?: property.run { array(name, description, *viewsArray(), optional = isOptional()) }
    }

    fun boolean(name: String, description: String, vararg view: View, optional: Boolean = false): Bool {
        return field(Bool(name, description, view.toMutableSet().apply { addAll(views) }, optional))
    }

    fun boolean(property: KProperty<*>, description: String): Bool {
        return property.run { boolean(name, description, *viewsArray(), optional = isOptional()) }
    }

    fun nil(name: String, description: String, vararg view: View): Nil {
        return field(Nil(name, description, view.toMutableSet().apply { addAll(views) }, false))
    }

    fun nil(property: KProperty<Any?>, description: String): Nil {
        return property.run { nil(name, description, *viewsArray()) }
    }

    fun number(name: String, description: String, vararg view: View, optional: Boolean = false): Number {
        return field(Number(name, description, view.toMutableSet().apply { addAll(views) }, optional))
    }

    fun number(property: KProperty<*>, description: String): Number {
        return property.run { number(name, description, *viewsArray(), optional = isOptional()) }
    }

    fun string(name: String, description: String, vararg view: View, optional: Boolean = false): Text {
        return field(Text(name, description, view.toMutableSet().apply { addAll(views) }, optional))
    }

    fun string(property: KProperty<*>, description: String): Text {
        return property.run { string(name, description, *viewsArray(), optional = isOptional()) }
    }

    /**
     * Documents any object's field.
     * Does type inference on [property] return type
     * so you can use it for any kind of field (primitive, json, array of primitive/json)
     * except for always null fields (use [nil] in this case).
     * Infers [JsonView] and nullability.
     * @param property a [KProperty] like Pet::nickname
     * @param description description of the field
     * @param of a nullable object description in case of json or array of json
     * like field(Pet::store, "Pet's store", storeDesc)
     * @param block a nullable function literal with self as receiver for appending descriptions to self
     * like field(Pet::store, "Pet's store") { fields += storeDesc }. Has precedence over [of]
     */
    fun field(
        property: KProperty<*>,
        description: String,
        of: MutableList<FieldDescriptor>? = null,
        block: (Nested.() -> Unit)? = null
    ): Field {
        return fieldOf(property.returnType.classifier, property, description, of, block)
    }

    fun fieldOf(
        type: KClassifier?,
        property: KProperty<*>,
        description: String,
        of: MutableList<FieldDescriptor>? = null,
        block: (Nested.() -> Unit)? = null
    ): Field {
        return when (type) {
            String::class, Byte::class, ByteArray::class -> string(property, description)
            Boolean::class -> boolean(property, description)
            Short::class, Int::class, Long::class, Float::class, Double::class -> number(property, description)
            Any::class, JvmType.Object::class -> varies(property, description)
            Collection::class, List::class, Set::class -> array(property, description, of, block)
            else -> json(property, description, of, block as? Json.() -> Unit)
        }
    }

    fun extField(type: KClassifier?, property: KProperty<*>, description: String): Field {
        return when (type) {
            String::class, Byte::class, ByteArray::class -> string(property, description)
            Boolean::class -> boolean(property, description)
            Short::class, Int::class, Long::class, Float::class, Double::class -> number(property, description)
            else -> varies(property, description)
        }
    }

    fun json(
        name: String,
        description: String,
        vararg view: View,
        optional: Boolean = false,
        block: JsonBlock
    ): Json {
        return nested(
            Json(name, description, view.toMutableSet().apply { addAll(views) }, optional, "$basePath$name."),
            block
        )
    }

    fun json(
        property: KProperty<*>,
        description: String,
        of: MutableList<FieldDescriptor>? = null,
        block: JsonBlock? = null
    ): Json {
        // Looks ugly but functions literals are a mess to cast
        val b: JsonBlock = block ?: if (of != null) {
            { fields += of!! }
        } else {
            {} as JsonBlock
        }
        return property.run { json(name, description, *viewsArray(), optional = isOptional(), block = b) }
    }

    fun varies(name: String, description: String, vararg view: View, optional: Boolean = false): Varies {
        return field(Varies(name, description, view.toMutableSet().apply { addAll(views) }, optional))
    }

    fun varies(property: KProperty<Any?>, description: String): Varies {
        return property.run { varies(name, description, *viewsArray(), optional = isOptional()) }
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
