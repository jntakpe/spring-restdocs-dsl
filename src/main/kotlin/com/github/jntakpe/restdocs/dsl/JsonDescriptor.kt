package com.github.jntakpe.restdocs.dsl

/**
 * Documents JSON response body
 */
object JsonDescriptor {

    /**
     * Creates a new JSON object documentation list of fields
     * @param init function with [Root] receiver used to document JSON object fields
     * @return documented fields
     */
    fun root(init: Root.() -> Unit) = Root().apply { init() }.fields

    /**
     * Creates a new JSON array documentation list of fields
     * @param init function with [Root] receiver used to document JSON array fields
     * @return documented fields
     */
    fun list(description: String, init: List.() -> Unit) = List(description).apply { init() }.fields
}
