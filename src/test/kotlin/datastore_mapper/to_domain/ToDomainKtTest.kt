package datastore_mapper.to_domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.google.cloud.Timestamp
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.FullEntity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.ListValue
import datastore_mapper.gcloud.EmulatedDatastore
import datastore_mapper.toDomain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

internal class ToDomainKtTest {
    private val datastore = EmulatedDatastore.instance
    private val keyFactory = datastore.client.newKeyFactory()
    private val testKind = "TestKind"
    private fun createKey(name: String): Key = keyFactory.setKind(testKind).newKey(name)
    private fun createKey(id: Long): Key = keyFactory.setKind(testKind).newKey(id)

    @Nested
    inner class Keys {
        @Test
        fun `can read a string id into a given property`() {
            data class TestClass(val id: String)

            val entity = Entity.newBuilder(createKey("my-id")).build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            assertThat(result).isEqualTo(TestClass("my-id"))
        }

        @Test
        fun `can read a numerical id into a property of type Int`() {
            data class TestClass(val id: Int)

            val entity = Entity.newBuilder(createKey(123456L)).build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            assertThat(result).isEqualTo(TestClass(123456))
        }

        @Test
        fun `can read a numerical id into a property of type Long`() {
            data class TestClass(val id: Long)

            val entity = Entity.newBuilder(createKey(123456L)).build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            assertThat(result).isEqualTo(TestClass(123456))
        }
    }

    @Nested
    inner class Properties {
        @Test
        fun `can convert a String`() {
            data class TestClass(val id: String, val string: String)

            val entity = Entity.newBuilder(createKey("id"))
                .set("string", "Foo")
                .build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            assertThat(result).isEqualTo(TestClass(id = "id", string = "Foo"))
        }

        @Test
        fun `converts an unset optional property to null`() {
            data class TestClass(val id: String, val optional: String?)

            val entity = Entity.newBuilder(createKey("id")).build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            assertThat(result).isEqualTo(TestClass(id = "id", optional = null))
        }

        @Test
        fun `can convert a Long to an Int`() {
            data class TestClass(val id: String, val int: Int)

            val entity = Entity.newBuilder(createKey("id"))
                .set("int", 1234L)
                .build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            assertThat(result).isEqualTo(TestClass(id = "id", int = 1234))
        }

        @Test
        fun `can convert a Long to a Long`() {
            data class TestClass(val id: String, val long: Long)

            val entity = Entity.newBuilder(createKey("id"))
                .set("long", 1234L)
                .build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            assertThat(result).isEqualTo(TestClass(id = "id", long = 1234L))
        }

        @Test
        fun `can convert a Boolean property`() {
            data class TestClass(val id: String, val boolean: Boolean)

            val entity = Entity.newBuilder(createKey("id"))
                .set("boolean", false)
                .build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            assertThat(result).isEqualTo(TestClass(id = "id", boolean = false))
        }

        @Test
        fun `can convert a String property to a BigDecimal`() {
            data class TestClass(val id: String, val bigDecimal: BigDecimal)

            val entity = Entity.newBuilder(createKey("id"))
                .set("bigDecimal", "12.34")
                .build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            assertThat(result).isEqualTo(TestClass(id = "id", bigDecimal = BigDecimal.valueOf(12.34)))
        }

        @Test
        fun `can convert a Timestamp property to an Instant`() {
            data class TestClass(val id: String, val instant: Instant)

            val timestamp = "2020-01-01T10:00:00.00Z"

            val entity = Entity.newBuilder(createKey("id"))
                .set("instant", Timestamp.parseTimestamp(timestamp))
                .build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            assertThat(result).isEqualTo(TestClass(id = "id", instant = Instant.parse(timestamp)))
        }

        @Test
        fun `can convert a list of Strings`() {
            data class TestClass(val id: String, val listOfStrings: List<String>)

            val entity = Entity.newBuilder(createKey("id"))
                .set("listOfStrings", ListValue.newBuilder().addValue("one").addValue("two").build())
                .build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            assertThat(result).isEqualTo(TestClass(id = "id", listOfStrings = listOf("one", "two")))
        }

        @Test
        fun `can convert a list of Ints`() {
            data class TestClass(val id: String, val listOfInts: List<Int>)

            val entity = Entity.newBuilder(createKey("id"))
                .set("listOfInts", ListValue.newBuilder().addValue(1L).addValue(2L).build())
                .build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            assertThat(result).isEqualTo(TestClass(id = "id", listOfInts = listOf(1, 2)))
        }

        @Test
        fun canConvertAListOfEntities() {
            // this test doesn't like having spaces in the test name
            // the mapper seems not to like the nested class being a child of a function
            // with spaces in the name
            data class NestedClass(val string: String, val int: Int)
            data class TestClass(val id: String, val listOfObjects: List<NestedClass>)

            val entity = Entity.newBuilder(createKey("id"))
                .set(
                    "listOfObjects",
                    ListValue.newBuilder()
                        .addValue(
                            FullEntity.newBuilder()
                                .set("string", "one")
                                .set("int", 1L)
                                .build()
                        )
                        .addValue(
                            FullEntity.newBuilder()
                                .set("string", "two")
                                .set("int", 2L)
                                .build()
                        )
                        .build()
                ).build()

            val result = entity.toDomain(TestClass::class, TestClass::id)

            assertThat(result).isEqualTo(
                TestClass(
                    id = "id",
                    listOfObjects = listOf(
                        NestedClass("one", 1),
                        NestedClass("two", 2),
                    )
                )
            )
        }
    }

    @Test
    fun `can convert a nested entity`() {
        data class NestedClass(val string: String, val int: Int, val long: Long, val instant: Instant, val boolean: Boolean)
        data class TestClass(val id: String, val nestedObject: NestedClass)

        val timestamp = "2020-01-01T10:00:00.00Z"

        val entity = Entity.newBuilder(createKey("id"))
            .set(
                "nestedObject",
                Entity.newBuilder()
                    .set("string", "string-value")
                    .set("long", 100L)
                    .set("int", 1L)
                    .set("instant", Timestamp.parseTimestamp(timestamp))
                    .set("boolean", "false")
                    .build()
            ).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        assertThat(result).isEqualTo(
            TestClass(
                id = "id",
                nestedObject = NestedClass(
                    string = "string-value",
                    long = 100L,
                    int = 1,
                    instant = Instant.parse(timestamp),
                    boolean = false,
                )
            )
        )
    }

}
