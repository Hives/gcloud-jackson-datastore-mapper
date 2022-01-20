package datastore_mapper.to_domain

import com.google.cloud.Timestamp
import com.google.cloud.datastore.Entity
import datastore_mapper.gcloud.EmulatedDatastore
import datastore_mapper.using_jackson.toDomain
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.Instant

class ToDomainKtTest : FreeSpec({

    val datastore = EmulatedDatastore.instance
    val keyFactory = datastore.client.newKeyFactory()
    fun createKey(name: String) = keyFactory.setKind("TestKind").newKey(name)
    fun createKey(id: Long) = keyFactory.setKind("TestKind").newKey(id)

    "Keys" - {
        "can read a string id into a given property" {
            data class TestClass(val id: String)

            val entity = Entity.newBuilder(createKey("my-id")).build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            result shouldBe TestClass(id = "my-id")
        }

        "can read a numerical id into a property of type Int" {
            data class TestClass(val id: Int)

            val entity = Entity.newBuilder(createKey(123456L)).build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            result shouldBe TestClass(id = 123456)
        }

        "can read a numerical id into a property of type Long" {
            data class TestClass(val id: Long)

            val entity = Entity.newBuilder(createKey(123456L)).build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            result shouldBe TestClass(id = 123456)
        }
    }

    "Reading properties on root entity" - {
        "Simple property types" - {
            "can convert a String property" {
                data class TestClass(val id: String, val stringProperty: String)

                val entity = Entity.newBuilder(createKey("id"))
                    .set("stringProperty", "Foo")
                    .build()

                val result = entity.toDomain(TestClass::class, TestClass::id)

                result shouldBe TestClass(id = "id", stringProperty = "Foo")
            }

            "can convert an optional property" {
                data class TestClass(val id: String, val optionalProperty: String?)

                val entity = Entity.newBuilder(createKey("id"))
                    .set("optionalProperty", "Foo")
                    .build()

                val result = entity.toDomain(TestClass::class, TestClass::id)

                result shouldBe TestClass(id = "id", optionalProperty = "Foo")
            }

            "can convert a null property" {
                data class TestClass(val id: String, val optionalProperty: String?)

                val entity = Entity.newBuilder(createKey("id")).build()

                val result = entity.toDomain(TestClass::class, TestClass::id)

                result shouldBe TestClass(id = "id", optionalProperty = null)
            }

            "can convert an Long property to an Int" {
                data class TestClass(val id: String, val intProperty: Int)

                val entity = Entity.newBuilder(createKey("id")).set("intProperty", 42L).build()

                val result = entity.toDomain(TestClass::class, TestClass::id)

                result shouldBe TestClass(id = "id", intProperty = 42)
            }

            "can convert a Long property" {
                data class TestClass(val id: String, val longProperty: Long)

                val entity = Entity.newBuilder(createKey("id")).set("longProperty", 42L).build()

                val result = entity.toDomain(TestClass::class, TestClass::id)

                result shouldBe TestClass(id = "id", longProperty = 42L)
            }

            "can convert a Boolean property" {
                data class TestClass(val id: String, val booleanProperty: Boolean)

                val entity = Entity.newBuilder(createKey("id")).set("booleanProperty", false).build()

                val result = entity.toDomain(TestClass::class, TestClass::id)

                result shouldBe TestClass(id = "id", booleanProperty = false)
            }

            "can convert a String property to a BigDecimal" {
                data class TestClass(val id: String, val bigDecimal: BigDecimal)

                val bigDecimal = BigDecimal(12.34)

                val entity = Entity.newBuilder(createKey("id")).set("bigDecimal", bigDecimal.toString()).build()

                val result = entity.toDomain(TestClass::class, TestClass::id)

                result shouldBe TestClass(id = "id", bigDecimal = bigDecimal)
            }

            "can convert a Timestamp property to an Instant" {
                data class TestClass(val id: String, val date: Instant)

                val timestamp = "2020-01-01T10:00:00.00Z"

                val entity = Entity.newBuilder(createKey("id"))
                    .set("date", Timestamp.parseTimestamp(timestamp))
                    .build()

                val result = entity.toDomain(TestClass::class, TestClass::id)

                result shouldBe TestClass(id = "id", date = Instant.parse(timestamp))
            }

            "can convert a Timestamp property to a String (if necessary?!)" {
                data class TestClass(val id: String, val string: String)

                val timestamp = "2020-01-01T10:00:00.00Z"

                val entity = Entity.newBuilder(createKey("id"))
                    .set("string", Timestamp.parseTimestamp(timestamp))
                    .build()

                val result = entity.toDomain(TestClass::class, TestClass::id)

                result shouldBe TestClass(id = "id", string = timestamp)
            }
        }
    }
})

