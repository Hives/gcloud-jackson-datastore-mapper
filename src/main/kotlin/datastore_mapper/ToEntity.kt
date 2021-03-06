package datastore_mapper

import TestClass
import TestClass.NestedClass
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.google.cloud.Timestamp
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.FullEntity
import com.google.cloud.datastore.IncompleteKey
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.ListValue
import datastore_mapper.to_entity_jackson.createDatastoreClient
import java.math.BigDecimal
import java.time.Instant
import kotlin.reflect.KProperty1

private val mapper = ObjectMapper()
    .registerModule(JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

fun main() {

    val datastore = createDatastoreClient("jl-digital-merch-flex", "paul-test")

    val input = TestClass(
        id = "test-class-id",
        integer = 42,
        long = 100L,
        bigDecimal = BigDecimal.valueOf(12.34),
        string = "Hello",
        bool = false,
        date = Instant.now(),
        listOfStrings = listOf("one", "two", "three"),
        listOfNumbers = listOf(1, 2, 3),
        listOfObjects = listOf(
            NestedClass(1, "one", true, Instant.now()),
            NestedClass(2, "two", false, Instant.now()),
        ),
        nested = NestedClass(
            number = 43,
            string = "Goodbye",
            bool = true,
            date = Instant.now()
        )
    )

    val entity = input.toEntity(
        keyProperty = TestClass::id,
        kind = "TestKind",
        datastore = datastore
    )

    datastore.put(entity)
}

fun <T> T.toEntity(
    keyProperty: KProperty1<T, Any>,
    kind: String,
    datastore: Datastore,
): FullEntity<IncompleteKey> {
    val node: JsonNode = mapper.valueToTree(this)

    // would be better to specify the permissible types of key property in the signature of this function somehow?
    val key = when (val keyValue = keyProperty.get(this)) {
        is String -> datastore.createKey(kind, keyValue)
        is Number -> datastore.createKey(kind, keyValue.toLong())
        else -> throw RuntimeException("Invalid key value: $keyValue. Expected a string or number.")
    }

    return node.toEntity(KeyDetails(key, keyProperty.name))
}

data class KeyDetails(
    val key: Key,
    val propertyName: String
)

private fun JsonNode.toEntity(keyDetails: KeyDetails? = null): FullEntity<IncompleteKey> {
    val entity = Entity.newBuilder()

    keyDetails?.also { entity.setKey(keyDetails.key) }

    fields().forEach { field ->
        if (field.key != keyDetails?.propertyName) {
            when {
                field.value.isBigDecimal -> entity.set(field.key, field.value.asText())
                field.value.isInt -> entity.set(field.key, field.value.asLong())
                field.value.isLong -> entity.set(field.key, field.value.asLong())
                field.value.isTextual -> {
                    try {
                        entity.set(field.key, field.value.asTimestamp())
                    } catch (e: RuntimeException) {
                        entity.set(field.key, field.value.asText())
                    }
                }
                field.value.isBoolean -> entity.set(field.key, field.value.asBoolean())
                field.value.isObject -> entity.set(field.key, field.value.toEntity())
                field.value.isArray -> entity.set(field.key, field.value.toListValue())
                field.value.isNull -> Unit
                else -> throw RuntimeException("Value of unknown type: ${field.value}")
            }
        }
    }

    return entity.build()
}

private fun JsonNode.toListValue(): ListValue {
    val listValue = ListValue.newBuilder()

    elements().forEach { node ->
        when {
            node.isBigDecimal -> listValue.addValue(node.asText())
            node.isInt -> listValue.addValue(node.asLong())
            node.isLong -> listValue.addValue(node.asLong())
            node.isTextual -> {
                try {
                    listValue.addValue(node.asTimestamp())
                } catch (e: RuntimeException) {
                    listValue.addValue(node.asText())
                }
            }
            node.isBoolean -> listValue.addValue(node.asBoolean())
            node.isObject -> listValue.addValue(node.toEntity())
        }
    }

    return listValue.build()
}

private fun JsonNode.asTimestamp(): Timestamp = Timestamp.parseTimestamp(asText())

private fun Datastore.createKey(kind: String, name: String): Key =
    newKeyFactory().setKind(kind).newKey(name)

private fun Datastore.createKey(kind: String, id: Long): Key =
    newKeyFactory().setKind(kind).newKey(id)
