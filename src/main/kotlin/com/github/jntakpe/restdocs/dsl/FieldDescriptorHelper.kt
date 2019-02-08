package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.Field.Companion.VIEWS_ATTR
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.snippet.Attributes.Attribute

fun FieldDescriptor.opt(optional: Boolean) = takeIf { optional }?.optional() ?: this

fun FieldDescriptor.views(views: Views): FieldDescriptor {
    return if (views.isNotEmpty()) {
        val existing = attributes[VIEWS_ATTR] as Views?
        attributes(Attribute(VIEWS_ATTR, existing?.apply { addAll(views) } ?: views))
    } else {
        this
    }
}
