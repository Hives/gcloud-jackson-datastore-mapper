package datastore_mapper.using_jackson

import TestClass
import TestClass.NestedClass
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.cloud.Timestamp
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.FullEntity
import com.google.cloud.datastore.IncompleteKey
import com.google.cloud.datastore.Value
import com.google.cloud.datastore.ValueType
import datastore_mapper.to_entity_jackson.createDatastoreClient
import java.math.BigDecimal
import java.time.Instant
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

private val mapper = jacksonObjectMapper()
    .registerModule(JavaTimeModule())

fun main() {
    val datastore = createDatastoreClient("jl-digital-merch-flex", "paul-test")

    val timestampString = Timestamp.parseTimestamp(Instant.now().toString()).toString()
    println(timestampString)

    val input = TestClass(
        id = "my-test-id",
        integer = 42,
        long = 99L,
        bigDecimal = BigDecimal.valueOf(12.34),
        string = timestampString,
        bool = false,
        date = Instant.now(),
        listOfStrings = listOf("one", "two", "three"),
        listOfNumbers = listOf(1, 2, 3),
        listOfObjects = listOf(
            NestedClass(1, "one", true, Instant.now()),
            NestedClass(2, "two", false, Instant.now()),
        ),
        nested = NestedClass(
            number = 42,
            string = "Hello",
            bool = false,
            date = Instant.now(),
        )
    )

    val entity = createEntity(
        fromValue = input,
        keyProperty = TestClass::id,
        kind = "TestKind2",
        datastore = datastore
    )

    datastore.put(entity)

    val retrieved = datastore.get(datastore.createKey("TestKind2", "my-test-id"))

    val output = retrieved.toDomain(TestClass::class, TestClass::id)

    println(output)

    check(output == input)
}

fun <T : Any> Entity.toDomain(targetClass: KClass<T>, keyProperty: KProperty1<T, Any>): T {
    val node = this.toNode(keyProperty.name)
    return mapper.treeToValue(node, targetClass.java)
}

fun Entity.toNode(keyPropertyName: String): JsonNode {
    val node = mapper.createObjectNode()

    when {
        key.hasName() -> node.put(keyPropertyName, key.name)
        key.hasId() -> node.put(keyPropertyName, key.id)
    }

    properties.forEach { (name, value) ->
        when (value.type) {
            ValueType.STRING -> node.put(name, value.get() as String)
            ValueType.LONG -> node.put(name, value.get() as Long)
            ValueType.BOOLEAN -> node.put(name, value.get() as Boolean)
            ValueType.TIMESTAMP -> node.put(name, (value.get() as Timestamp).toString())
            ValueType.ENTITY -> node.set(name, (value.get() as FullEntity<IncompleteKey>).toNode())
            ValueType.LIST -> node.set(name, (value.get() as List<Value<*>>).toNode())
            ValueType.DOUBLE -> TODO()
            ValueType.NULL -> TODO()
            ValueType.KEY -> TODO()
            ValueType.BLOB -> TODO()
            ValueType.RAW_VALUE -> TODO()
            ValueType.LAT_LNG -> TODO()
        }
    }

    return node
}

fun <T : Value<*>> List<T>.toNode(): JsonNode {
    val node = mapper.createArrayNode()

    forEach { value ->
        when (value.type) {
            ValueType.STRING -> node.add(value.get() as String)
            ValueType.LONG -> node.add(value.get() as Long)
            ValueType.ENTITY -> node.add((value.get() as FullEntity<IncompleteKey>).toNode())
            ValueType.NULL -> TODO()
            ValueType.LIST -> TODO()
            ValueType.KEY -> TODO()
            ValueType.DOUBLE -> TODO()
            ValueType.BOOLEAN -> TODO()
            ValueType.TIMESTAMP -> TODO()
            ValueType.BLOB -> TODO()
            ValueType.RAW_VALUE -> TODO()
            ValueType.LAT_LNG -> TODO()
        }
    }

    return node
}

fun FullEntity<IncompleteKey>.toNode(): JsonNode {
    val node = mapper.createObjectNode()

    properties.forEach { (name, value) ->
        when (value.type) {
            ValueType.STRING -> node.put(name, value.get() as String)
            ValueType.LONG -> node.put(name, value.get() as Long)
            ValueType.BOOLEAN -> node.put(name, value.get() as Boolean)
            ValueType.TIMESTAMP -> node.put(name, (value.get() as Timestamp).toString())
            ValueType.ENTITY -> node.set(name, (value.get() as FullEntity<IncompleteKey>).toNode())
            ValueType.LIST -> TODO()
            ValueType.DOUBLE -> TODO()
            ValueType.NULL -> TODO()
            ValueType.KEY -> TODO()
            ValueType.BLOB -> TODO()
            ValueType.RAW_VALUE -> TODO()
            ValueType.LAT_LNG -> TODO()
        }
    }

    return node
}
