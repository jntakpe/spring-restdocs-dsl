package com.github.jntakpe.restdocs.dsl.core

import org.springframework.restdocs.payload.FieldDescriptor

typealias Fields = MutableList<FieldDescriptor>
typealias InitDsl<T> = T.() -> Unit

inline fun <reified T : DocBuilder> applyDsl(init: InitDsl<T>): Fields {
    return T::class.constructors.first().call().apply(init).build().fields
}