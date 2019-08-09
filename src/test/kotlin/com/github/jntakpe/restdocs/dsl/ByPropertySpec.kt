package com.github.jntakpe.restdocs.dsl

import com.fasterxml.jackson.annotation.JsonView
import com.github.jntakpe.restdocs.dsl.Field.Companion.VIEWS_ATTR
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType.*
import java.time.Duration

interface Owner
interface Merchant

object ByPropertySpec : Spek({
    describe("Any payload") {
        context("with simple fields") {
            it("should create text fields") {
                data class Pet(val name: String)

                val nameDesc = "Pet's name"
                val petDoc by obj {
                    field(Pet::name, nameDesc)
                }
                val petDesc = petDoc.find { it.path == Pet::name.name }!!
                assertThat(petDesc.description).isEqualTo(nameDesc)
                assertThat(petDesc.isOptional).isFalse()
                assertThat(petDesc.viewAttr()).isNull()
                assertThat(petDesc.type).isEqualTo(STRING)
            }
            it("should create optional text fields") {
                data class Pet(val name: String?)

                val petDoc by obj { field(Pet::name, "") }
                val petDesc = petDoc.find { it.path == Pet::name.name }!!
                assertThat(petDesc.isOptional).isTrue()
                assertThat(petDesc.type).isEqualTo(STRING)
            }
            it("should create boolean fields") {
                data class Pet(val alive: Boolean)

                val aliveDesc = "is Pet alive"
                val petDoc by obj {
                    field(Pet::alive, aliveDesc)
                }
                val petDesc = petDoc.find { it.path == Pet::alive.name }!!
                assertThat(petDesc.description).isEqualTo(aliveDesc)
                assertThat(petDesc.isOptional).isFalse()
                assertThat(petDesc.viewAttr()).isNull()
                assertThat(petDesc.type).isEqualTo(BOOLEAN)
            }
            it("should create optional boolean fields") {
                data class Pet(val alive: Boolean?)

                val aliveDesc = "is Pet alive"
                val petDoc by obj {
                    field(Pet::alive, aliveDesc)
                }
                val petDesc = petDoc.find { it.path == Pet::alive.name }!!
                assertThat(petDesc.isOptional).isTrue()
                assertThat(petDesc.type).isEqualTo(BOOLEAN)
            }
            it("should create number fields") {
                data class Pet(
                    val ageShort: Short,
                    val ageInt: Int,
                    val ageLong: Long,
                    val ageFloat: Float,
                    val ageDouble: Double
                )

                val ageDesc = "Pet's age"
                val petDoc by obj {
                    field(Pet::ageShort, ageDesc)
                    field(Pet::ageInt, ageDesc)
                    field(Pet::ageLong, ageDesc)
                    field(Pet::ageFloat, ageDesc)
                    field(Pet::ageDouble, ageDesc)
                }
                assertThat(petDoc.map { it.description }).containsOnly(ageDesc)
                assertThat(petDoc.map { it.isOptional }).containsOnly(false)
                assertThat(petDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(petDoc.map { it.type }).containsOnly(NUMBER)
                assertThat(petDoc.map { it.path }).containsExactly(
                    Pet::ageShort.name,
                    Pet::ageInt.name,
                    Pet::ageLong.name,
                    Pet::ageFloat.name,
                    Pet::ageDouble.name
                )
            }
            it("should create optional number fields") {
                data class Pet(
                    val ageShort: Short?,
                    val ageInt: Int?,
                    val ageLong: Long?,
                    val ageFloat: Float?,
                    val ageDouble: Double?
                )

                val petDoc by obj {
                    field(Pet::ageShort, "")
                    field(Pet::ageInt, "")
                    field(Pet::ageLong, "")
                    field(Pet::ageFloat, "")
                    field(Pet::ageDouble, "")
                }
                assertThat(petDoc.map { it.isOptional }).containsOnly(true)
            }
            it("should create nil fields") {
                data class Pet(val name: String? = null)

                val nameDesc = "Pet's name, always null"
                val petDoc by obj {
                    nil(Pet::name, nameDesc)
                }
                val petDesc = petDoc.find { it.path == Pet::name.name }!!
                assertThat(petDesc.description).isEqualTo(nameDesc)
                assertThat(petDesc.isOptional).isFalse()
                assertThat(petDesc.viewAttr()).isNull()
                assertThat(petDesc.type).isEqualTo(NULL)
            }
            it("should create varies fields") {
                data class Pet(val avatar: Any, val food: Any?)

                val avatarDesc = "Pet's avatar"
                val foodDesc = "Pet's favorite food"
                val petDoc by obj {
                    field(Pet::avatar, avatarDesc)
                    field(Pet::food, foodDesc)
                }
                assertThat(petDoc.map { it.description }).containsExactly(avatarDesc, foodDesc)
                assertThat(petDoc.map { it.isOptional }).containsExactly(false, true)
                assertThat(petDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(petDoc.map { it.type }).containsOnly(VARIES)
                assertThat(petDoc.map { it.path }).containsExactly(Pet::avatar.name, Pet::food.name)
            }
            it("should infer json views") {
                data class Pet(
                    val noView: String,
                    @field:JsonView(value = [Owner::class]) val oneView: String,
                    @field:JsonView(value = [Owner::class, Merchant::class]) val twoView: String
                )

                val petDoc by obj {
                    field(Pet::noView, "")
                    field(Pet::oneView, "")
                    field(Pet::twoView, "")
                }
                assertThat(petDoc.map { it.viewAttr() }).containsExactly(
                    null,
                    mutableSetOf(Owner::class),
                    mutableSetOf(Owner::class, Merchant::class)
                )
            }
            it("should infer json views on getters/steers") {
                data class Pet(
                    @get:JsonView(value = [Owner::class]) val getterView: String,
                    @set:JsonView(value = [Owner::class]) var setterView: String
                )

                val petDoc by obj {
                    field(Pet::getterView, "")
                    field(Pet::setterView, "")
                }
                assertThat(petDoc.map { it.viewAttr() }).containsOnly(mutableSetOf(Owner::class))
            }
            it("should allow forcing type") {
                data class Pet(val ttl: Duration, val ttd: Duration?)

                val ttlDesc = "Pet's time to live"
                val ttdDesc = "Pet's time to death"
                val petDoc by obj {
                    field<String>(Pet::ttl, ttlDesc)
                    field<String?>(Pet::ttd, ttdDesc)
                }
                assertThat(petDoc.map { it.description }).containsExactly(ttlDesc, ttdDesc)
                assertThat(petDoc.map { it.path }).containsExactly(Pet::ttl.name, Pet::ttd.name)
                assertThat(petDoc.map { it.isOptional }).containsExactly(false, true)
                assertThat(petDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(petDoc.map { it.type }).containsOnly(STRING)
            }
        }
        context("with complex fields") {
            it("should create simple array fields") {
                data class Pet(
                    val nicknamesList: List<String>,
                    val nicknamesSet: Set<String>,
                    val nicknamesCollection: Collection<String>
                )

                val nicknamesDesc = "Pet's nicknames"
                val petDoc by obj {
                    field(Pet::nicknamesList, nicknamesDesc)
                    field(Pet::nicknamesSet, nicknamesDesc)
                    field(Pet::nicknamesCollection, nicknamesDesc)
                }
                assertThat(petDoc.map { it.description }).containsOnly(nicknamesDesc)
                assertThat(petDoc.map { it.isOptional }).containsOnly(false)
                assertThat(petDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(petDoc.map { it.type }).containsOnly(ARRAY)
                assertThat(petDoc.map { it.path }).containsExactly(
                    Pet::nicknamesList.name,
                    Pet::nicknamesSet.name,
                    Pet::nicknamesCollection.name
                )
            }
            it("should create optional simple array fields") {
                data class Pet(
                    val nicknamesList: List<String>?,
                    val nicknamesSet: Set<String>?,
                    val nicknamesCollection: Collection<String>?
                )

                val petDoc by obj {
                    field(Pet::nicknamesList, "")
                    field(Pet::nicknamesSet, "")
                    field(Pet::nicknamesCollection, "")
                }
                assertThat(petDoc.map { it.isOptional }).containsOnly(true)
            }
            it("should create array fields") {
                data class Store(val location: String)
                data class Pet(
                    val storesList: List<Store>,
                    val storesSet: Set<Store>,
                    val storesCollection: Collection<Store>
                )

                val storesDesc = "Pet's stores"
                val locationDesc = "Store GPS coordinates"
                val storeDoc by obj {
                    field(Store::location, locationDesc)
                }
                val petDoc by obj {
                    field(Pet::storesList, storesDesc, storeDoc)
                    field(Pet::storesSet, storesDesc, storeDoc)
                    field(Pet::storesCollection, storesDesc, storeDoc)
                }
                assertThat(petDoc.map { it.isOptional }).containsOnly(false)
                assertThat(petDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(petDoc.map { it.type }).containsExactly(
                    ARRAY, STRING,
                    ARRAY, STRING,
                    ARRAY, STRING
                )
                assertThat(petDoc.map { it.path }).containsExactly(
                    Pet::storesList.name,
                    "${Pet::storesList.name}[].${Store::location.name}",
                    Pet::storesSet.name,
                    "${Pet::storesSet.name}[].${Store::location.name}",
                    Pet::storesCollection.name,
                    "${Pet::storesCollection.name}[].${Store::location.name}"
                )
                assertThat(petDoc.map { it.description }).containsExactly(
                    storesDesc, locationDesc,
                    storesDesc, locationDesc,
                    storesDesc, locationDesc
                )
            }
            it("should create object fields") {
                data class Location(val lat: String, val lon: String)
                data class Store(val location: Location)
                data class Pet(val store: Store)

                val storeDesc = "Pet's store"
                val locationDesc = "Store GPS coordinates"
                val latDesc = "Latitude"
                val lonDesc = "Longitude"
                val locationDoc by obj {
                    field(Location::lat, latDesc)
                    field(Location::lon, lonDesc)
                }
                val storeDoc by obj {
                    field(Store::location, locationDesc, locationDoc)
                }
                val petDoc by obj {
                    field(Pet::store, storeDesc, storeDoc)
                }
                assertThat(petDoc.map { it.isOptional }).containsOnly(false)
                assertThat(petDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(petDoc.map { it.type }).containsExactly(
                    OBJECT,
                    OBJECT,
                    STRING, STRING
                )
                assertThat(petDoc.map { it.path }).containsExactly(
                    Pet::store.name,
                    "${Pet::store.name}.${Store::location.name}",
                    "${Pet::store.name}.${Store::location.name}.${Location::lat.name}",
                    "${Pet::store.name}.${Store::location.name}.${Location::lon.name}"
                )
                assertThat(petDoc.map { it.description }).containsExactly(
                    storeDesc,
                    locationDesc,
                    latDesc,
                    lonDesc
                )
            }
            it("should create array of objects") {
                data class Pet(val name: String)

                val nameDesc = "Pet's name"
                val petDoc by obj { field(Pet::name, nameDesc) }
                val petsDoc by arr<Pet>(petDoc)
                val petsDescDoc by arrDesc(petDoc, "Array of cute pets")
                assertThat(petsDoc.map { it.isOptional }).containsOnly(false)
                assertThat(petsDoc.map { it.viewAttr() }).containsOnlyNulls()
                assertThat(petsDoc.map { it.path }).containsExactly("[]", "[].name")
                assertThat(petsDoc.map { it.type }).containsExactly(ARRAY, STRING)
                assertThat(petsDoc.map { it.description }).containsExactly("Array of pets", nameDesc)
                assertThat(petsDescDoc.map { it.description }).containsExactly("Array of cute pets", nameDesc)
            }
        }
    }
})

fun FieldDescriptor.viewAttr() = attributes[VIEWS_ATTR] as? Views
