package datastore_mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.google.cloud.datastore.Key
import datastore_mapper.gcloud.EmulatedDatastore
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.time.Instant

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class EndToEndTest {
    private val datastore = EmulatedDatastore.instance
    private val keyFactory = datastore.client.newKeyFactory()
    private val testKind = "TestKind"
    private fun createKey(name: String): Key = keyFactory.setKind(testKind).newKey(name)

    @BeforeAll
    fun setup() {
        datastore.clean()
    }

    @AfterEach
    fun cleanup() {
        datastore.clean()
    }

    @Test
    fun `Can convert a data object to an entity, persist it, retrieve it, and convert it back`() {
        val entity = initialObject.toEntity(
            keyProperty = TestClass::id,
            kind = testKind,
            datastore = datastore.client
        )

        datastore.client.put(entity)

        val retrievedEntity = datastore.client.get(createKey(initialObject.id))

        val finalObject = retrievedEntity.toDomain(TestClass::class, TestClass::id)

        assertThat(finalObject).isEqualTo(initialObject)
    }

    private val initialObject = TestClass(
        id = "my-id",
        string = "string-value",
        optional = null,
        int = 42,
        boolean = false,
        bigDecimal = BigDecimal.valueOf(12.34),
        instant = Instant.now(),
        listOfStrings = listOf("one", "two"),
        listOfInts = listOf(1, 2),
        listOfObjects = listOf(
            TestClass.NestedClass("one", 1, 1L, true, Instant.now()),
            TestClass.NestedClass("two", 2, 2L, false, Instant.now()),
        ),
        nestedObject = TestClass.NestedClass(
            string = "nested-string",
            int = 1234,
            long = 98765L,
            boolean = false,
            instant = Instant.now()
        )
    )
}