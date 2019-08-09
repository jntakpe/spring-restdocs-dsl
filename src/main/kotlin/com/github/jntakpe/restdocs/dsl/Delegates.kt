package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.JsonDescriptor.list
import com.github.jntakpe.restdocs.dsl.JsonDescriptor.root
import org.springframework.restdocs.payload.FieldDescriptor
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Property delegates for initializing an object description in a val.
 * Use it like val petDoc by obj { field(Pet:: ...) }
 */
fun obj(init: Root.() -> Unit) = ObjectDoc(init)

class ObjectDoc(private val init: Root.() -> Unit) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = root(init)
}

/**
 * Property delegates for initializing an array of objects description in a val providing a description
 * Use it like val petsDoc by arrDesc(petDesc, "Array of pets")
 */
fun arrDesc(of: MutableList<FieldDescriptor>, description: String) = ListDoc(description, of)

/**
 * Property delegates for initializing an array of objects description in a val.
 * Unlike [arrDesc], description in inferred from given Type [T].
 * Use it like val petsDoc by arr<Pet>(petDesc)
 */
inline fun <reified T> arr(of: MutableList<FieldDescriptor>, description: String = "Array of ${T::class.toPluralName()}") = ListDoc(description, of)

class ListDoc(private val description: String, private val of: MutableList<FieldDescriptor>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = list(description) { fields += of }
}

// TODO improve, maybe with a third party
fun KClass<*>.toPluralName() = "${simpleName?.toLowerCase().orEmpty()}s"