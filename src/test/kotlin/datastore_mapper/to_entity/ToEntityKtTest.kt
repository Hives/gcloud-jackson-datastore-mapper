package datastore_mapper.to_entity

import com.google.cloud.Timestamp
import com.google.cloud.datastore.FullEntity
import com.google.cloud.datastore.IncompleteKey
import com.google.cloud.datastore.Value
import datastore_mapper.gcloud.EmulatedDatastore
import datastore_mapper.using_jackson.createEntity
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.Instant

class ToEntityKtTest : FreeSpec({

    val datastore = EmulatedDatastore.instance

    "Keys" - {
        "can create a key from given String property" {
            data class TestClass(val id: String)

            val testObject = TestClass(id = "my-id")

            val entity = createEntity(
                fromValue = testObject,
                keyProperty = TestClass::id,
                kind = "TestClassKind",
                datastore = datastore.client
            )

            entity.key.name shouldBe "my-id"
            entity.key.kind shouldBe "TestClassKind"
            entity.key.projectId shouldBe datastore.project
            entity.contains("id") shouldBe false
        }

        "can create a key from given Int property" {
            data class TestClass(val id: Int)

            val testObject = TestClass(id = 12345)

            val entity = createEntity(
                fromValue = testObject,
                keyProperty = TestClass::id,
                kind = "TestClassKind",
                datastore = datastore.client
            )

            entity.key.id shouldBe 12345
            entity.key.kind shouldBe "TestClassKind"
            entity.key.projectId shouldBe datastore.project
            entity.contains("id") shouldBe false
        }

        "can create a key from given Long property" {
            data class TestClass(val id: Long)

            val testObject = TestClass(id = 12345)

            val entity = createEntity(
                fromValue = testObject,
                keyProperty = TestClass::id,
                kind = "TestClassKind",
                datastore = datastore.client
            )

            entity.key.id shouldBe 12345
            entity.key.kind shouldBe "TestClassKind"
            entity.key.projectId shouldBe datastore.project
            entity.contains("id") shouldBe false
        }
    }

    "Setting properties on root entity" - {
        "Simple property types" - {
            "a String gets set as a String" {
                data class TestClass(val id: String, val stringProperty: String)

                val testObject = TestClass(id = "my-id", stringProperty = "Foo")

                val entity = createEntity(
                    fromValue = testObject,
                    keyProperty = TestClass::id,
                    kind = "TestClassKind",
                    datastore = datastore.client
                )

                entity.contains("stringProperty") shouldBe true
                entity.getString("stringProperty") shouldBe "Foo"
            }

            "an optional String gets set as a String" {
                data class TestClass(val id: String, val optionalStringProperty: String?)

                val testObject = TestClass(id = "my-id", optionalStringProperty = "Foo")

                val entity = createEntity(
                    fromValue = testObject,
                    keyProperty = TestClass::id,
                    kind = "TestClassKind",
                    datastore = datastore.client
                )

                entity.contains("optionalStringProperty") shouldBe true
                entity.getString("optionalStringProperty") shouldBe "Foo"
            }

            "an Int gets set as a Long" {
                data class TestClass(val id: String, val intProperty: Int)

                val testObject = TestClass(id = "my-id", intProperty = 42)

                val entity = createEntity(
                    fromValue = testObject,
                    keyProperty = TestClass::id,
                    kind = "TestClassKind",
                    datastore = datastore.client
                )

                entity.contains("intProperty") shouldBe true
                entity.getLong("intProperty") shouldBe 42
            }

            "a Boolean gets set as a Boolean" {
                data class TestClass(val id: String, val booleanProperty: Boolean)

                val testObject = TestClass(id = "my-id", booleanProperty = false)

                val entity = createEntity(
                    fromValue = testObject,
                    keyProperty = TestClass::id,
                    kind = "TestClassKind",
                    datastore = datastore.client
                )

                entity.contains("booleanProperty") shouldBe true
                entity.getBoolean("booleanProperty") shouldBe false
            }

            "a BigDecimal gets set as a String" {
                data class TestClass(val id: String, val bigDecimalProperty: BigDecimal)

                val testObject = TestClass(id = "my-id", bigDecimalProperty = BigDecimal(12.34))

                val entity = createEntity(
                    fromValue = testObject,
                    keyProperty = TestClass::id,
                    kind = "TestClassKind",
                    datastore = datastore.client
                )

                entity.contains("bigDecimalProperty") shouldBe true
                entity.getString("bigDecimalProperty") shouldBe BigDecimal(12.34).toString()
            }

            "an Instant gets set as a Timestamp" {
                data class TestClass(val id: String, val instantProperty: Instant)

                val timestamp = "2020-01-01T10:00:00.00Z"
                val instant = Instant.parse(timestamp)
                val testObject = TestClass(id = "my-id", instantProperty = instant)

                val entity = createEntity(
                    fromValue = testObject,
                    keyProperty = TestClass::id,
                    kind = "TestClassKind",
                    datastore = datastore.client
                )

                entity.contains("instantProperty") shouldBe true
                entity.getTimestamp("instantProperty") shouldBe Timestamp.parseTimestamp(timestamp)
            }

            "a String that looks like a timestamp gets set as a Timestamp (!!)" {
                data class TestClass(val id: String, val stringProperty: String)

                val timestamp = "2020-01-01T10:00:00.00Z"
                val testObject = TestClass(id = "my-id", stringProperty = timestamp)

                val entity = createEntity(
                    fromValue = testObject,
                    keyProperty = TestClass::id,
                    kind = "TestClassKind",
                    datastore = datastore.client
                )

                entity.contains("stringProperty") shouldBe true
                entity.getTimestamp("stringProperty") shouldBe Timestamp.parseTimestamp(timestamp)
            }

            "an Null property does not get set" {
                data class TestClass(val id: String, val optionalStringProperty: String?)

                val testObject = TestClass(id = "my-id", optionalStringProperty = null)

                val entity = createEntity(
                    fromValue = testObject,
                    keyProperty = TestClass::id,
                    kind = "TestClassKind",
                    datastore = datastore.client
                )

                entity.contains("optionalStringProperty") shouldBe false
            }
        }

        "Lists" - {
            "a list of Strings gets set as a list of Strings" {
                data class TestClass(val id: String, val list: List<String>)

                val testObject = TestClass(id = "my-id", list = listOf("one", "two", "three"))

                val entity = createEntity(
                    fromValue = testObject,
                    keyProperty = TestClass::id,
                    kind = "TestClassKind",
                    datastore = datastore.client
                )

                entity.contains("list") shouldBe true
                val persistedValues = entity.getList<Value<String>>("list").map { it.get() }
                persistedValues shouldBe listOf("one", "two", "three")
            }

            "a list of Ints gets set as a list of Longs" {
                data class TestClass(val id: String, val list: List<Int>)

                val testObject = TestClass(id = "my-id", list = listOf(1, 2, 3))

                val entity = createEntity(
                    fromValue = testObject,
                    keyProperty = TestClass::id,
                    kind = "TestClassKind",
                    datastore = datastore.client
                )

                entity.contains("list") shouldBe true
                val persistedValues = entity.getList<Value<Long>>("list").map { it.get() }
                persistedValues shouldBe listOf(1L, 2L, 3L)
            }

            "a list of data class instances gets set as a list of entities" {
                data class NestedClass(val number: Int, val string: String)
                data class TestClass(val id: String, val list: List<NestedClass>)

                val testObject = TestClass(
                    id = "my-id",
                    list = listOf(
                        NestedClass(number = 1, string = "one"),
                        NestedClass(number = 2, string = "two")
                    )
                )

                val entity = createEntity(
                    fromValue = testObject,
                    keyProperty = TestClass::id,
                    kind = "TestClassKind",
                    datastore = datastore.client
                )

                entity.contains("list") shouldBe true

                val nestedEntities = entity.getList<Value<FullEntity<IncompleteKey>>>("list").map { it.get() }

                with(nestedEntities[0]) {
                    getLong("number") shouldBe 1
                    getString("string") shouldBe "one"
                }

                with(nestedEntities[1]) {
                    getLong("number") shouldBe 2
                    getString("string") shouldBe "two"
                }
            }

            // TODO - what about other types of collections? sets? etc?
        }
    }

    "Setting properties on a nested entity" - {
        "sets an Int on a nested entity as a Long" {
            data class NestedClass(val int: Int)
            data class TestClass(val id: String, val nested: NestedClass)

            val testObject = TestClass(id = "my-id", nested = NestedClass(int = 42))

            val entity = createEntity(
                fromValue = testObject,
                keyProperty = TestClass::id,
                kind = "TestClassKind",
                datastore = datastore.client
            )

            entity.getEntity<IncompleteKey>("nested").getLong("int") shouldBe 42
        }

        "sets a Long on a nested entity as a Long" {
            data class NestedClass(val long: Long)
            data class TestClass(val id: String, val nested: NestedClass)

            val testObject = TestClass(id = "my-id", nested = NestedClass(long = 42L))

            val entity = createEntity(
                fromValue = testObject,
                keyProperty = TestClass::id,
                kind = "TestClassKind",
                datastore = datastore.client
            )

            entity.getEntity<IncompleteKey>("nested").getLong("long") shouldBe 42L
        }

        "sets a String on a nested entity as a String" {
            data class NestedClass(val string: String)
            data class TestClass(val id: String, val nested: NestedClass)

            val testObject = TestClass(id = "my-id", nested = NestedClass(string = "Hello"))

            val entity = createEntity(
                fromValue = testObject,
                keyProperty = TestClass::id,
                kind = "TestClassKind",
                datastore = datastore.client
            )

            entity.getEntity<IncompleteKey>("nested").getString("string") shouldBe "Hello"
        }

        "sets a Boolean on a nested entity as a Boolean" {
            data class NestedClass(val bool: Boolean)
            data class TestClass(val id: String, val nested: NestedClass)

            val testObject = TestClass(id = "my-id", nested = NestedClass(bool = false))

            val entity = createEntity(
                fromValue = testObject,
                keyProperty = TestClass::id,
                kind = "TestClassKind",
                datastore = datastore.client
            )

            entity.getEntity<IncompleteKey>("nested").getBoolean("bool") shouldBe false
        }

        "sets an Instant on a nested entity as a Timestamp" {
            data class NestedClass(val date: Instant)
            data class TestClass(val id: String, val nested: NestedClass)

            val timestamp = "2020-01-01T10:00:00.00Z"
            val instant = Instant.parse(timestamp)
            val testObject = TestClass(id = "my-id", nested = NestedClass(date = instant))

            val entity = createEntity(
                fromValue = testObject,
                keyProperty = TestClass::id,
                kind = "TestClassKind",
                datastore = datastore.client
            )

            entity.getEntity<IncompleteKey>("nested").getTimestamp("date") shouldBe Timestamp.parseTimestamp(timestamp)
        }

        // TODO - other properties? lists?
    }
})
