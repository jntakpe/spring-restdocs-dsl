package com.github.jntakpe.restdocs.dsl.processor.test

import com.github.jntakpe.restdocs.dsl.*
import com.github.jntakpe.restdocs.dsl.JsonDescriptor.root
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType.*

object AutoDslTest : Spek({
    describe("Auto dsl test") {
        context("on simple fields") {
            it("should document a text field") {
                val someTextDoc = someText { name = "Some text" }
                assertThat(someTextDoc.map { it.path }).containsExactly(SomeText::name.name)
                assertThat(someTextDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(someTextDoc.map { it.isOptional }).containsExactly(false)
                assertThat(someTextDoc.map { it.type }).containsExactly(STRING)
                assertThat(someTextDoc.map { it.description }).containsExactly("Some text")
            }
            it("should document an optional text field") {
                val noneTextDoc = noneText { name = "None text" }
                assertThat(noneTextDoc.map { it.path }).containsExactly(NoneText::name.name)
                assertThat(noneTextDoc.map { it.isOptional }).containsExactly(true)
                assertThat(noneTextDoc.map { it.type }).containsExactly(STRING)
                assertThat(noneTextDoc.map { it.description }).containsExactly("None text")
            }
            it("should document a boolean field") {
                val someBoolDoc = someBool { ok = "Some bool" }
                assertThat(someBoolDoc.map { it.path }).containsExactly(SomeBool::ok.name)
                assertThat(someBoolDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(someBoolDoc.map { it.isOptional }).containsExactly(false)
                assertThat(someBoolDoc.map { it.type }).containsExactly(BOOLEAN)
                assertThat(someBoolDoc.map { it.description }).containsExactly("Some bool")
            }
            it("should document an optional boolean field") {
                val noneBoolDoc = noneBool { ok = "None bool" }
                assertThat(noneBoolDoc.map { it.path }).containsExactly(NoneBool::ok.name)
                assertThat(noneBoolDoc.map { it.isOptional }).containsExactly(true)
                assertThat(noneBoolDoc.map { it.type }).containsExactly(BOOLEAN)
                assertThat(noneBoolDoc.map { it.description }).containsExactly("None bool")
            }
            it("should document a number field") {
                val someNumberDoc = someNumber {
                    ageShort = "Some short"
                    ageInt = "Some int"
                    ageLong = "Some long"
                    ageFloat = "Some float"
                    ageDouble = "Some double"
                }
                assertThat(someNumberDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(someNumberDoc.map { it.isOptional }).containsOnly(false)
                assertThat(someNumberDoc.map { it.type }).containsOnly(NUMBER)
                assertThat(someNumberDoc.map { it.path }).containsExactly(
                    SomeNumber::ageShort.name,
                    SomeNumber::ageInt.name,
                    SomeNumber::ageLong.name,
                    SomeNumber::ageFloat.name,
                    SomeNumber::ageDouble.name
                )
                assertThat(someNumberDoc.map { it.description }).containsExactly(
                    "Some short",
                    "Some int",
                    "Some long",
                    "Some float",
                    "Some double"
                )
            }
            it("should document an optional number field") {
                val noneNumberDoc = noneNumber { age = "None number" }
                assertThat(noneNumberDoc.map { it.path }).containsExactly(NoneNumber::age.name)
                assertThat(noneNumberDoc.map { it.isOptional }).containsExactly(true)
                assertThat(noneNumberDoc.map { it.type }).containsExactly(NUMBER)
                assertThat(noneNumberDoc.map { it.description }).containsExactly("None number")
            }
            it("should document a varying field") {
                val someVariesDoc = someVaries { noIdea = "Some varies" }
                assertThat(someVariesDoc.map { it.path }).containsExactly(SomeVaries::noIdea.name)
                assertThat(someVariesDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(someVariesDoc.map { it.isOptional }).containsExactly(false)
                assertThat(someVariesDoc.map { it.type }).containsExactly(VARIES)
                assertThat(someVariesDoc.map { it.description }).containsExactly("Some varies")
            }
            it("should document an optional varying field") {
                val noneTextDoc = noneVaries { noIdea = "None varies" }
                assertThat(noneTextDoc.map { it.path }).containsExactly(NoneVaries::noIdea.name)
                assertThat(noneTextDoc.map { it.isOptional }).containsExactly(true)
                assertThat(noneTextDoc.map { it.type }).containsExactly(VARIES)
                assertThat(noneTextDoc.map { it.description }).containsExactly("None varies")
            }
        }
        context("on ApiDoc wrapper") {
            it("should wrap in apiDoc and document object") {
                val desc = "This will be in ApiDoc"
                fromApiDoc { name = desc }
                assertThat(fromApiDocDoc.map { it.path }).containsOnly(FromApiDoc::name.name)
                assertThat(fromApiDocDoc.map { it.description }).containsOnly(desc)
                assertThat(fromApiDocDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(fromApiDocDoc.map { it.isOptional }).containsExactly(false)
                assertThat(fromApiDocDoc.map { it.type }).containsExactly(STRING)
            }
            it("should wrap root and nested in apiDoc and document nested objects") {
                nestedClass { name = "I am nested" }
                rootClass { nestedClass = "Some Nested object" }
                assertThat(rootClassDoc.map { it.path }).containsExactly(
                    RootClass::nestedClass.name,
                    "${RootClass::nestedClass.name}.${NestedClass::name.name}"
                )
                assertThat(rootClassDoc.map { it.description }).containsExactly(
                    "Some Nested object",
                    "I am nested"
                )
                assertThat(rootClassDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(rootClassDoc.map { it.isOptional }).containsOnly(false)
                assertThat(rootClassDoc.map { it.type }).containsExactly(OBJECT, STRING)
            }
        }
        context("on complex fields") {
            it("should document simple collections") {
                someSimpleCollection {
                    listNames = "List of String"
                    setNames = "Set of String"
                    collectionNames = "Collection of String"
                }
                assertThat(someSimpleCollectionDoc.map { it.path }).containsExactly(
                    SomeSimpleCollection::listNames.name,
                    SomeSimpleCollection::setNames.name,
                    SomeSimpleCollection::collectionNames.name
                )
                assertThat(someSimpleCollectionDoc.map { it.description }).containsExactly(
                    "List of String",
                    "Set of String",
                    "Collection of String"
                )
                assertThat(someSimpleCollectionDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(someSimpleCollectionDoc.map { it.isOptional }).containsOnly(false)
                assertThat(someSimpleCollectionDoc.map { it.type }).containsOnly(ARRAY)
            }
            it("should document simple optional collections") {
                noneSimpleCollection {
                    listNames = "Optional List of String"
                    setNames = "Optional Set of String"
                    collectionNames = "Optional Collection of String"
                }
                assertThat(noneSimpleCollectionDoc.map { it.path }).containsExactly(
                    NoneSimpleCollection::listNames.name,
                    NoneSimpleCollection::setNames.name,
                    NoneSimpleCollection::collectionNames.name
                )
                assertThat(noneSimpleCollectionDoc.map { it.description }).containsExactly(
                    "Optional List of String",
                    "Optional Set of String",
                    "Optional Collection of String"
                )
                assertThat(noneSimpleCollectionDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(noneSimpleCollectionDoc.map { it.isOptional }).containsOnly(true)
                assertThat(noneSimpleCollectionDoc.map { it.type }).containsOnly(ARRAY)
            }
            it("should document simple complex collections") {
                store { city = "City of Store" }
                someComplexCollection {
                    listStore = "List of Store"
                    setStore = "Set of Store"
                    collectionStore = "Collection of Store"
                }
                assertThat(someComplexCollectionDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(someComplexCollectionDoc.map { it.isOptional }).containsOnly(false)
                assertThat(someComplexCollectionDoc.map { it.path }).containsExactly(
                    SomeComplexCollection::listStore.name,
                    "${SomeComplexCollection::listStore.name}[].${Store::city.name}",
                    SomeComplexCollection::setStore.name,
                    "${SomeComplexCollection::setStore.name}[].${Store::city.name}",
                    SomeComplexCollection::collectionStore.name,
                    "${SomeComplexCollection::collectionStore.name}[].${Store::city.name}"
                )
                assertThat(someComplexCollectionDoc.map { it.description }).containsExactly(
                    "List of Store", "City of Store",
                    "Set of Store", "City of Store",
                    "Collection of Store", "City of Store"
                )
                assertThat(someComplexCollectionDoc.map { it.type }).containsExactly(
                    ARRAY, STRING,
                    ARRAY, STRING,
                    ARRAY, STRING
                )
            }
            it("should document very complex nested objects") {
                flag { colors = "Colors of the flag" }
                country { flag = "Flag of the country" }
                beer {
                    degree = "Alcohol degree"
                    from = "Country from which beer is from"
                }
                bar {
                    name = "Name of the bar"
                    beers = "Available beers at this bar"
                }
                assertThat(barDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(barDoc.map { it.isOptional }).containsOnly(false)
                assertThat(barDoc.map { it.path }).containsExactly(
                    Bar::name.name,
                    Bar::beers.name,
                    "${Bar::beers.name}[].${Beer::degree.name}",
                    "${Bar::beers.name}[].${Beer::from.name}",
                    "${Bar::beers.name}[].${Beer::from.name}[].${Country::flag.name}",
                    "${Bar::beers.name}[].${Beer::from.name}[].${Country::flag.name}.${Flag::colors.name}"
                )
                assertThat(barDoc.map { it.description }).containsExactly(
                    "Name of the bar",
                    "Available beers at this bar",
                    "Alcohol degree",
                    "Country from which beer is from",
                    "Flag of the country",
                    "Colors of the flag"
                )
                assertThat(barDoc.map { it.type }).containsExactly(
                    STRING, ARRAY,
                    NUMBER, ARRAY,
                    OBJECT,
                    ARRAY
                )
            }
            it("should document external types") {
                offsetDateTimeType = String::class
                flight { departure = "Departure time" }
                assertThat(flightDoc.map { it.path }).containsExactly(Flight::departure.name)
                assertThat(flightDoc.map { it.description }).containsExactly("Departure time")
                assertThat(flightDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(flightDoc.map { it.isOptional }).containsOnly(false)
                assertThat(flightDoc.map { it.type }).containsOnly(STRING)
            }
            it("should document external redundant types") {
                offsetDateTimeType = String::class
                otherFlight { departure = "Other departure time" }
                assertThat(otherFlightDoc.map { it.path }).containsExactly(OtherFlight::departure.name)
                assertThat(otherFlightDoc.map { it.description }).containsExactly("Other departure time")
                assertThat(otherFlightDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(otherFlightDoc.map { it.isOptional }).containsOnly(false)
                assertThat(otherFlightDoc.map { it.type }).containsOnly(STRING)
            }
            it("should document external nested types") {
                localDateTimeType = String::class
                dateType = Double::class
                durationType = String::class
                timeFrame {
                    start = "Date and time without offset"
                    finishTimestamp = "Timestamp when it ends"
                }
                journey {
                    duration = "Duration of the journey"
                    timeFrame = "Duration milestones"
                }
                assertThat(journeyDoc.map { it.path }).containsExactly(
                    Journey::duration.name,
                    Journey::timeFrame.name,
                    "${Journey::timeFrame.name}.${TimeFrame::start.name}",
                    "${Journey::timeFrame.name}.${TimeFrame::finishTimestamp.name}"
                )
                assertThat(journeyDoc.map { it.description }).containsExactly(
                    "Duration of the journey",
                    "Duration milestones",
                    "Date and time without offset",
                    "Timestamp when it ends"
                )
                assertThat(journeyDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(journeyDoc.map { it.isOptional }).containsOnly(false)
                assertThat(journeyDoc.map { it.type }).containsExactly(
                    STRING, OBJECT,
                    STRING, NUMBER
                )
            }
//            WARNING: fails if called unitary, depends on another object initialized in another context
            it("should not fail once reusing previously defined objects") {
                reusingExisting {
                    store = "Some store"
                }
                assertThat(reusingExistingDoc.map { it.path }).containsExactly(
                    ReusingExisting::store.name,
                    "${ReusingExisting::store.name}.${Store::city.name}"
                )
                assertThat(reusingExistingDoc.map { it.description }).containsExactly(
                    "Some store",
                    "City of Store"
                )
                assertThat(reusingExistingDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(reusingExistingDoc.map { it.isOptional }).containsOnly(false)
                assertThat(reusingExistingDoc.map { it.type }).containsExactly(OBJECT, STRING)
            }
            it("should enable documenting fields of not annotated with @Doc") {
                unannotatedDoc = root {
                    field(Unannotated::name, "Unannotated name")
                    field(Unannotated::age, "Unannotated age")
                }
                usingNotAnnotated { unannotated = "Some unannotated nested object" }
                assertThat(usingNotAnnotatedDoc.map { it.path }).containsExactly(
                    UsingNotAnnotated::unannotated.name,
                    "${UsingNotAnnotated::unannotated.name}.${Unannotated::name.name}",
                    "${UsingNotAnnotated::unannotated.name}.${Unannotated::age.name}"
                )
                assertThat(usingNotAnnotatedDoc.map { it.description }).containsExactly(
                    "Some unannotated nested object",
                    "Unannotated name",
                    "Unannotated age"
                )
                assertThat(usingNotAnnotatedDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(usingNotAnnotatedDoc.map { it.isOptional }).containsOnly(false)
                assertThat(usingNotAnnotatedDoc.map { it.type }).containsExactly(
                    OBJECT,
                    STRING, NUMBER
                )
            }
        }
    }
})

fun FieldDescriptor.viewAttr() = attributes[Field.VIEWS_ATTR] as? Views