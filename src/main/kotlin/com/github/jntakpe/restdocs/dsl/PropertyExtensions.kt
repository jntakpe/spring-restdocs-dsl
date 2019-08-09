package com.github.jntakpe.restdocs.dsl

import com.fasterxml.jackson.annotation.JsonView
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

fun KProperty<*>.isOptional(): Boolean = returnType.isMarkedNullable

fun KProperty<*>.views(): Views = findAnnotations<JsonView>().flatMap { it.value.toList() }.toMutableSet()

fun KProperty<*>.viewsArray(): kotlin.Array<View> = views().toTypedArray()

inline fun <reified T : Annotation> KProperty<*>.findAnnotations(): kotlin.Array<out T> {
    return (findFieldAnnotations<T>() + findGetterAnnotations() + findSetterAnnotations()).toTypedArray()
}

inline fun <reified T : Annotation> KProperty<*>.findFieldAnnotations(): Set<T> {
    return javaField?.getAnnotationsByType(T::class.java)?.toSet() ?: emptySet()
}

inline fun <reified T : Annotation> KProperty<*>.findGetterAnnotations(): Set<T> {
    return getter.annotations.filterIsInstance<T>().toSet()
}

inline fun <reified T : Annotation> KProperty<*>.findSetterAnnotations(): Set<T> {
    return (this as? KMutableProperty)?.setter?.annotations?.filterIsInstance<T>()?.toSet() ?: emptySet()
}