package com.github.jntakpe.restdocs.dsl

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.payload.RequestFieldsSnippet
import org.springframework.restdocs.payload.ResponseFieldsSnippet

/**
 * Transform a [List] of [FieldDescriptor] into a [RequestFieldsSnippet]
 * @receiver any [List] of [FieldDescriptor]
 * @return a [RequestFieldsSnippet]
 */
fun MutableList<FieldDescriptor>.asReq(): RequestFieldsSnippet = requestFields(this)

/**
 * Transform a [List] of [FieldDescriptor] into a [ResponseFieldsSnippet]
 * @receiver any [List] of [FieldDescriptor]
 * @return a [ResponseFieldsSnippet]
 */
fun MutableList<FieldDescriptor>.asResp(): ResponseFieldsSnippet = responseFields(this)