package datastore_mapper.to_entity

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import com.google.cloud.Timestamp
import com.google.cloud.datastore.FullEntity
import com.google.cloud.datastore.IncompleteKey
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.Value
import datastore_mapper.TestClass
import datastore_mapper.TestClass.NestedClass
import datastore_mapper.gcloud.EmulatedDatastore
import datastore_mapper.toEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.time.Instant

@Suppress("SameParameterValue")
internal class ToEntityKtTest {

    private val datastore = EmulatedDatastore.instance
    private val keyFactory = datastore.client.newKeyFactory()
    private val testKind = "TestKind"
    private fun createKey(name: String): Key = keyFactory.setKind(testKind).newKey(name)
    private fun createKey(id: Long): Key = keyFactory.setKind(testKind).newKey(id)

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Keys {
        @BeforeAll
        fun setup() {
            datastore.clean()
        }

        @AfterEach
        fun cleanup() {
            datastore.clean()
        }

        @Test
        fun `can create a key from given String property`() {
            data class TestClass(val id: String, val string: String)

            val testObject = TestClass(id = "my-string-id", string = "ive-got-a-string-id")

            val entity = testObject.toEntity(
                keyProperty = TestClass::id,
                kind = testKind,
                datastore = datastore.client
            )

            datastore.client.put(entity)

            val retrieved = datastore.client.get(createKey("my-string-id"))

            assertThat(retrieved.getString("string")).isEqualTo("ive-got-a-string-id")
        }

        @Test
        fun `can create a key from given Int property`() {
            data class TestClass(val id: Int, val string: String)

            val testObject = TestClass(id = 1234, string = "ive-got-an-int-id")

            val entity = testObject.toEntity(
                keyProperty = TestClass::id,
                kind = testKind,
                datastore = datastore.client
            )

            datastore.client.put(entity)

            val retrieved = datastore.client.get(createKey(1234))

            assertThat(retrieved.getString("string")).isEqualTo("ive-got-an-int-id")
        }

        @Test
        fun `can create a key from given Long property`() {
            data class TestClass(val id: Long, val string: String)

            val testObject = TestClass(id = 5678L, string = "ive-got-a-Long-id")

            val entity = testObject.toEntity(
                keyProperty = TestClass::id,
                kind = testKind,
                datastore = datastore.client
            )

            datastore.client.put(entity)

            val retrieved = datastore.client.get(createKey(5678L))

            assertThat(retrieved.getString("string")).isEqualTo("ive-got-a-Long-id")
        }
    }

    @Nested
    inner class Properties {
        private val timestamp = "2020-01-01T10:00:00.00Z"

        private val testObject = TestClass(
            id = "my-id",
            string = "string-value",
            optional = null,
            int = 42,
            boolean = false,
            bigDecimal = BigDecimal.valueOf(12.34),
            instant = Instant.parse(timestamp),
            listOfStrings = listOf("one", "two"),
            listOfInts = listOf(1, 2),
            listOfObjects = listOf(
                NestedClass("one", 1, 1L, true, Instant.parse(timestamp)),
                NestedClass("two", 2, 2L, false, Instant.parse(timestamp)),
            ),
            nestedObject = NestedClass(
                string = "nested-string",
                int = 1234,
                long = 98765L,
                boolean = false,
                instant = Instant.parse(timestamp)
            )
        )

        private val entity = testObject.toEntity(
            keyProperty = TestClass::id,
            kind = testKind,
            datastore = datastore.client
        )

        @Test
        fun `String value is set as a String`() {
            assertThat(entity.getString("string")).isEqualTo("string-value")
        }

        @Test
        fun `optional value is not set`() {
            assertThat(entity.contains("optional")).isFalse()
        }

        @Test
        fun `Int value is set as a Long`() {
            assertThat(entity.getLong("int")).isEqualTo(42L)
        }

        @Test
        fun `Boolean value is set as a Boolean`() {
            assertThat(entity.getBoolean("boolean")).isEqualTo(false)
        }

        @Test
        fun `BigDecimal value is set as a String`() {
            assertThat(entity.getString("bigDecimal")).isEqualTo("12.34")
        }

        @Test
        fun `Instant value is set as a Timestamp`() {
            assertThat(entity.getTimestamp("instant")).isEqualTo(Timestamp.parseTimestamp(timestamp))
        }

        @Test
        fun `List of Strings is set as a list of Strings`() {
            val listValue = entity.getList<Value<String>>("listOfStrings")
            val values = listValue.map(Value<String>::get)
            assertThat(values).containsExactly("one", "two")
        }

        @Test
        fun `List of Ints is set as a list of Longs`() {
            val listValue = entity.getList<Value<Long>>("listOfInts")
            val values = listValue.map(Value<Long>::get)
            assertThat(values).containsExactly(1L, 2L)
        }

        @Test
        fun `List of objects is set as a list of entities`() {
            val listValue = entity.getList<Value<FullEntity<IncompleteKey>>>("listOfObjects")
            val values = listValue.map(Value<FullEntity<IncompleteKey>>::get)
            with(values[0]) {
                assertThat(getString("string")).isEqualTo("one")
                assertThat(getLong("int")).isEqualTo(1L)
                assertThat(getLong("long")).isEqualTo(1L)
                assertThat(getBoolean("boolean")).isEqualTo(true)
                assertThat(getTimestamp("instant")).isEqualTo(Timestamp.parseTimestamp(timestamp))
            }
            with(values[1]) {
                assertThat(getString("string")).isEqualTo("two")
                assertThat(getLong("int")).isEqualTo(2L)
                assertThat(getLong("long")).isEqualTo(2L)
                assertThat(getBoolean("boolean")).isEqualTo(false)
                assertThat(getTimestamp("instant")).isEqualTo(Timestamp.parseTimestamp(timestamp))
            }
        }

        @Test
        fun `can set properties on a nested entity`() {
            val nestedEntity = entity.getEntity<IncompleteKey>("nestedObject")

            with(nestedEntity) {
                assertThat(getString("string")).isEqualTo("nested-string")
                assertThat(getLong("int")).isEqualTo(1234L)
                assertThat(getLong("long")).isEqualTo(98765L)
                assertThat(getBoolean("boolean")).isEqualTo(false)
                assertThat(getTimestamp("instant")).isEqualTo(Timestamp.parseTimestamp(timestamp))
            }
        }
    }
}
