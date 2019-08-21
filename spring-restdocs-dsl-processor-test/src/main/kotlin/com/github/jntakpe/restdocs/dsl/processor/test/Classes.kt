package com.github.jntakpe.restdocs.dsl.processor.test

import com.fasterxml.jackson.annotation.JsonView
import com.github.jntakpe.restdocs.dsl.annotations.Doc
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*

@Doc
data class SomeText(val name: String)

@Doc
data class NoneText(val name: String?)

@Doc
data class SomeBool(val ok: Boolean)

@Doc
data class NoneBool(val ok: Boolean?)

@Doc
data class SomeNumber(
    val ageShort: Short,
    val ageInt: Int,
    val ageLong: Long,
    val ageFloat: Float,
    val ageDouble: Double
)

@Doc
data class NoneNumber(val age: Int?)

@Doc
data class SomeVaries(val noIdea: Any)

@Doc
data class NoneVaries(val noIdea: Any?)

interface ViewA
interface ViewB

@Doc
data class WithJsonView(
    @field:JsonView(value = [ViewA::class]) val viewField: String,
    @get:JsonView(value = [ViewA::class]) val viewGetter: String,
    @set:JsonView(value = [ViewA::class]) var viewSetter: String,
    @field:JsonView(value = [ViewA::class, ViewB::class]) val manyViews: String
)

@Doc
data class FromApiDoc(val name: String)

@Doc
data class RootClass(val nestedClass: NestedClass)

@Doc
data class NestedClass(val name: String)

@Doc
data class SomeSimpleCollection(
    val listNames: List<String>,
    val setNames: Set<String>,
    val collectionNames: Collection<String>
)

@Doc
data class NoneSimpleCollection(
    val listNames: List<String>?,
    val setNames: Set<String>?,
    val collectionNames: Collection<String>?
)

@Doc
data class Store(val city: String)

@Doc
data class SomeComplexCollection(
    val listStore: List<Store>,
    val setStore: Set<Store>,
    val collectionStore: Collection<Store>
)

@Doc
class Flag(val colors: List<String>)

@Doc
data class Country(val flag: Flag)

@Doc
data class Beer(val degree: Float, val from: List<Country>)

@Doc
data class Bar(val name: String, val beers: List<Beer>)

@Doc
data class Flight(val departure: OffsetDateTime)

@Doc
data class OtherFlight(val departure: OffsetDateTime)

@Doc
data class TimeFrame(val start: LocalDateTime, val finishTimestamp: Date)

@Doc
data class Journey(val duration: Duration, val timeFrame: TimeFrame)

@Doc
data class ReusingExisting(val store: Store)

data class Unannotated(val name: String, val age: Int)

@Doc
data class UsingNotAnnotated(val unannotated: Unannotated)