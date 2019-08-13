package com.github.jntakpe.restdocs.dsl.core

import com.github.jntakpe.restdocs.dsl.Nested
import com.github.jntakpe.restdocs.dsl.Root
import com.github.jntakpe.restdocs.dsl.annotations.RestDocsAutoDslMarker
import kotlin.reflect.KClassifier
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

/**
 * Superclass for all DSL receiver.
 * In charge of applying the documentation to main properties in ApiDoc.kt
 * and calls each field getter to make sure it has been documented, throws an error otherwise
 */
@RestDocsAutoDslMarker
abstract class DocBuilder(private val apiDocProperty: KMutableProperty<Fields>) {
    protected val self: Nested = Root()

    fun build(): Nested {
        callGetters()
        apiDocProperty.setter.call(self.fields)
        return self
    }

    private fun callGetters() {
        this::class.members.asSequence()
            .filterIsInstance<KProperty1<DocBuilder, *>>()
            .filter { it.returnType.toString() == "kotlin.String" }
            .forEach { it.get(this) }
    }
}

/**
 * Delegate property for a simple field
 */
fun doc(
    parent: Nested,
    prop: KProperty<*>,
    apiDocProp: KMutableProperty<Fields>? = null,
    classifier: KMutableProperty<KClassifier?>? = null
) = FieldDoc(parent, prop, apiDocProp, classifier)

class FieldDoc(
    private val parent: Nested,
    private val prop: KProperty<*>,
    private val apiDocProp: KMutableProperty<Fields>?,
    private val classifier: KMutableProperty<KClassifier?>?
) {
    private var desc: String? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = desc.undocumentedError(property)

    operator fun setValue(thisRef: Any?, property: KProperty<*>, desc: String) {
        this.desc = desc
        classifier?.call()?.also { parent.extField(it, prop, desc) }
            ?: apiDocProp?.call()?.also { parent.field(prop, desc, it) }
            ?: parent.field(prop, desc)
    }
}

/**
 * Delegate property for a property in ApiDoc.kt
 */
fun mainDoc() = MainDoc()

class MainDoc {
    private var fields: Fields? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = fields.undocumentedError(property, "object")

    operator fun setValue(thisRef: Any?, property: KProperty<*>, desc: Fields) {
        this.fields = desc
    }
}

/**
 * Delegate property for an external field
 */
fun extField() = ExtField()

class ExtField {
    private var classifier: KClassifier? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = classifier

    operator fun setValue(thisRef: Any?, property: KProperty<*>, desc: KClassifier?) {
        this.classifier = desc
    }
}

fun <T> T?.undocumentedError(property: KProperty<*>, kind: String = "field"): T {
    return this ?: throw IllegalStateException("Undocumented $kind ${property.name}")
}
