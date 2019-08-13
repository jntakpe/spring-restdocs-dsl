package com.github.jntakpe.restdocs.dsl.processor

import com.squareup.kotlinpoet.*
import javax.lang.model.type.TypeKind

val simplePrimitives: List<TypeKind> = listOf(
        TypeKind.BOOLEAN,
        TypeKind.BYTE,
        TypeKind.SHORT,
        TypeKind.INT,
        TypeKind.LONG,
        TypeKind.CHAR,
        TypeKind.FLOAT,
        TypeKind.DOUBLE,
        TypeKind.VOID
)

val arrayPrimitives: List<TypeKind> = listOf(TypeKind.ARRAY)

val kSimpleTypes: List<ClassName> = listOf(
        ANY,
        UNIT,
        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        LONG,
        CHAR,
        FLOAT,
        DOUBLE,
        STRING,
        CHAR_SEQUENCE,
        COMPARABLE,
        THROWABLE,
        ANNOTATION,
        NOTHING,
        NUMBER
)

val javaLang = "java.lang"

val javaSimpleTypes: List<ClassName> = listOf(
        ClassName(javaLang, "Object"),
        ClassName(javaLang, "Void"),
        ClassName(javaLang, "Boolean"),
        ClassName(javaLang, "Byte"),
        ClassName(javaLang, "Short"),
        ClassName(javaLang, "Integer"),
        ClassName(javaLang, "Long"),
        ClassName(javaLang, "Char"),
        ClassName(javaLang, "Float"),
        ClassName(javaLang, "Double"),
        ClassName(javaLang, "String"),
        ClassName(javaLang, "CharSequence"),
        ClassName(javaLang, "Comparable"),
        ClassName(javaLang, "Throwable"),
        ClassName("$javaLang.annotations", "Annotation"),
        ClassName(javaLang, "Number")
)

val simpleTypes: List<ClassName> = kSimpleTypes + javaSimpleTypes

val kArrayTypes: List<ClassName> = listOf(
        ARRAY,
        ITERABLE,
        COLLECTION,
        LIST,
        SET,
        MUTABLE_ITERABLE,
        MUTABLE_COLLECTION,
        MUTABLE_LIST,
        MUTABLE_SET,
        MUTABLE_MAP,
        MUTABLE_MAP_ENTRY,
        BOOLEAN_ARRAY,
        BYTE_ARRAY,
        CHAR_ARRAY,
        SHORT_ARRAY,
        INT_ARRAY,
        LONG_ARRAY,
        FLOAT_ARRAY,
        DOUBLE_ARRAY,
        U_BYTE,
        U_SHORT,
        U_INT,
        U_LONG,
        U_BYTE_ARRAY,
        U_SHORT_ARRAY,
        U_INT_ARRAY,
        U_LONG_ARRAY
)

val javaLangReflect = "$javaLang.reflect"
val javaUtil = "java.util"

val javaArrayTypes: List<ClassName> = listOf(
    ClassName(javaLangReflect, "Array"),
    ClassName(javaLang, "Iterable"),
    ClassName(javaUtil, "Collection"),
    ClassName(javaUtil, "List"),
    ClassName(javaUtil, "Set")
)

val arrayTypes: List<ClassName> = kArrayTypes + javaArrayTypes