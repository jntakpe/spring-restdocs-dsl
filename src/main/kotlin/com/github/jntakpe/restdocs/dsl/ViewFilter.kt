package com.github.jntakpe.restdocs.dsl

import com.github.jntakpe.restdocs.dsl.Field.Companion.VIEWS_ATTR
import org.springframework.restdocs.payload.FieldDescriptor

/**
 * Filters fields with the views attribute [VIEWS_ATTR] that contains the [view] parameter.
 * @param view accepted view
 * @param strict removes fields without any view specified
 * @return a [FieldDescriptor] list that contains the [view] parameter
 */
fun List<FieldDescriptor>.withView(view: View, strict: Boolean = false) = filter { it.views()?.contains(view) ?: !strict }

/**
 * Filters fields with the views attribute [VIEWS_ATTR] that does not contains the [view] parameter or no view at all
 * @param view refused view
 * @param strict removes fields without any view specified
 * @return a [FieldDescriptor] list that does not contains with the [view] parameter
 */
fun List<FieldDescriptor>.withoutView(view: View, strict: Boolean = false) = filter { it.views()?.contains(view)?.not() ?: !strict }

private fun FieldDescriptor.views() = attributes[VIEWS_ATTR] as Views?
