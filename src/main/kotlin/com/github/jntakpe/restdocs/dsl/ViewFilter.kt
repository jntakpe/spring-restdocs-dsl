package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.Field.Companion.VIEWS_ATTR
import org.springframework.restdocs.payload.FieldDescriptor

/**
 * Filters fields with the views attribute [VIEWS_ATTR] that contains the [view] parameter
 * @param view accepted view
 * @return a [FieldDescriptor] list that compliant with the [view] parameter
 */
fun List<FieldDescriptor>.withView(view: View) = filter { it.views()?.contains(view) ?: true }

private fun FieldDescriptor.views() = attributes[VIEWS_ATTR] as Views?
