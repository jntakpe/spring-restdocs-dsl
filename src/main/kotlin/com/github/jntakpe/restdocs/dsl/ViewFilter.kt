package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.Field.Companion.VIEWS_ATTR
import org.springframework.restdocs.payload.FieldDescriptor

/**
 * Filters fields with the views attribute [VIEWS_ATTR] that contains the [view] parameter
 * @param view accepted view
 * @return a [FieldDescriptor] list that contains the [view] parameter
 */
fun List<FieldDescriptor>.withView(view: View) = filter { it.views()?.contains(view) ?: true }

/**
 * Filters fields with the views attribute [VIEWS_ATTR] that does not contains the [view] parameter
 * @param view refused view
 * @return a [FieldDescriptor] list that does not contains with the [view] parameter
 */
fun List<FieldDescriptor>.withoutView(view: View) = filter { it.views()?.contains(view)?.not() ?: true }

private fun FieldDescriptor.views() = attributes[VIEWS_ATTR] as Views?
