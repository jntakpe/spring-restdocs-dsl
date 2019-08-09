package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.FieldDescriptor
import kotlin.reflect.KProperty

/**
 * Like [Nested.field] but enforce type in case a Jackson (De)Serializer is used
 * @param T actual type of the property once (de)serialized
 */
inline fun <reified T> Nested.field(
    property: KProperty<*>,
    description: String,
    of: MutableList<FieldDescriptor>? = null,
    noinline block: (Nested.() -> Unit)? = null
) : Field {
    return fieldOf(T::class, property, description, of, block)
}