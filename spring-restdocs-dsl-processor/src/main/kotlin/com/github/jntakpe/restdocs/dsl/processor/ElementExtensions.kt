package com.github.jntakpe.restdocs.dsl.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

fun Element.isSimpleType() = asType().isSimpleType()

private fun TypeMirror.isSimpleType() = asTypeName() in simpleTypes || kind in simplePrimitives

private fun TypeMirror.isComplexType() = !isSimpleType()

fun Element.isSimpleArrayType() = isArrayOfType { isSimpleType() }

fun Element.isComplexArrayType() = isArrayOfType { isComplexType() }

fun Element.singleParameterizedType(): TypeMirror? = asDeclaredType()?.typeArguments?.firstOrNull()

fun TypeMirror?.simpleName(): String? = toString().split(".").last()

private fun Element.isArrayOfType(of: TypeMirror.() -> Boolean): Boolean {
    return isArrayType() && asDeclaredType()?.typeArguments?.all(of) ?: false
}

private fun Element.isArrayType() = asType().run {
    asTypeName() in arrayTypes ||
            rawType()?.let { it in arrayTypes } ?: false ||
            kind in arrayPrimitives
}

private fun Element.asDeclaredType(): DeclaredType? = asType() as? DeclaredType
private fun TypeMirror.rawType(): ClassName? = (asTypeName() as? ParameterizedTypeName)?.rawType
