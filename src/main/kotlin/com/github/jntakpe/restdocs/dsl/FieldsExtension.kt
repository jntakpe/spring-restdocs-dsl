package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.JsonDescriptor.list
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
): Field {
    return fieldOf(T::class, property, description, of, block)
}

/**
 * Given a doc of a field, turn into a doc of a list of this field
 * If description is not provided, defaults to 'Array of <plural class name>'
 */
inline fun <reified T> MutableList<FieldDescriptor>.asList(description: String? = null): MutableList<FieldDescriptor> {
    val desc = description ?: "Array of ${T::class.toPluralName()}"
    return list(desc) { fields += this@asList}
}