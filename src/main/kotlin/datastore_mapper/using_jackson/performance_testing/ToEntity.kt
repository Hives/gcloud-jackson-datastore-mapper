package datastore_mapper.using_jackson.performance_testing

import TestClass
import TestClass.NestedClass
import com.google.cloud.Timestamp
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.FullEntity
import com.google.cloud.datastore.IncompleteKey
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.ListValue
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.Value
import datastore_mapper.to_entity_jackson.createDatastoreClient
import datastore_mapper.using_jackson.createEntity
import datastore_mapper.using_jackson.createKey
import datastore_mapper.using_jackson.toDomain
import java.math.BigDecimal
import java.time.Instant
import kotlin.system.measureTimeMillis

fun main() {
    val datastore = createDatastoreClient("jl-digital-merch-flex", "paul-test")

    val testClass = TestClass(
        id = "my-test-id",
        integer = 42,
        long = 99L,
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
            number = 42,
            string = "Hello",
            bool = false,
            date = Instant.now(),
        )
    )

    val manyTestClasses = List(500) { it }.map { testClass.copy(id = "$it") }

    persistAndRetrieveManual(manyTestClasses, datastore)
    measureTimeMillis {
        persistAndRetrieveManual(manyTestClasses, datastore)
    }.also {
        println("did it the manual way in $it ms")
        // 1223 ms
    }

    persistAndRetrieveAutomatic(manyTestClasses, datastore)
    measureTimeMillis {
        persistAndRetrieveAutomatic(manyTestClasses, datastore)
    }.also {
        println("did it the automatic way in $it ms")
        // 1306 ms
    }
}

private fun persistAndRetrieveAutomatic(
    manyTestClasses: List<TestClass>,
    datastore: Datastore,
) {
    val entities = manyTestClasses.map {
        createEntity(
            fromValue = it,
            keyProperty = TestClass::id,
            kind = "PerformanceAutomatic",
            datastore = datastore
        )
    }

    datastore.put(*entities.toTypedArray())

    val retrievedEntities =
        datastore.run(
            Query.newEntityQueryBuilder()
                .setKind("PerformanceAutomatic")
                .build()
        )
            .asSequence()
            .toList()

    retrievedEntities.map { it.toDomain(TestClass::class, TestClass::id) }

//    check(entities.toSet() == retrievedEntities.toSet())
}

private fun persistAndRetrieveManual(
    manyTestClasses: List<TestClass>,
    datastore: Datastore,
) {
    val entities = manyTestClasses.map {
        val key = datastore.createKey("PerformanceManual", it.id)
        it.toEntityManual(key)
    }

    datastore.put(*entities.toTypedArray())

    val retrievedEntities =
        datastore.run(
            Query.newEntityQueryBuilder()
                .setKind("PerformanceManual")
                .build()
        )
            .asSequence()
            .toList()

    retrievedEntities.map { it.toDomainManual() }

//    check(entities.toSet() == retrievedEntities.toSet())
}

fun Entity.toDomainManual(): TestClass =
    TestClass(
        id = key.name,
        integer = getLong("integer").toInt(),
        long = getLong("long"),
        bigDecimal = getString("bigDecimal").toBigDecimal(),
        string = getString("string"),
        bool = getBoolean("bool"),
        nested = getEntity<IncompleteKey>("nested").let {
            NestedClass(
                number = it.getLong("number").toInt(),
                string = it.getString("string"),
                bool = it.getBoolean("bool"),
                date = it.getInstant("date")
            )
        },
        date = getInstant("date"),
        listOfStrings = getList<Value<String>>("listOfStrings").map { it.get() },
        listOfNumbers = getList<Value<Long>>("listOfNumbers").map { it.get().toInt() },
        listOfObjects = getList<Value<FullEntity<IncompleteKey>>>("listOfObjects").map {
            it.get().let { entity ->
                NestedClass(
                    number = entity.getLong("number").toInt(),
                    string = entity.getString("string"),
                    bool = entity.getBoolean("bool"),
                    date = entity.getInstant("date")
                )
            }
        }
    )

fun Entity.getInstant(name: String) =
    getTimestamp(name).toString().let(Instant::parse)

fun FullEntity<IncompleteKey>.getInstant(name: String) =
    getTimestamp(name).toString().let(Instant::parse)

fun TestClass.toEntityManual(key: Key): FullEntity<IncompleteKey> =
    Entity.newBuilder()
        .setKey(key)
        .set("integer", integer.toLong())
        .set("long", long)
        .set("bigDecimal", bigDecimal.toPlainString())
        .set("string", string)
        .set("bool", bool)
        .set("nested", nested.toEntityManual())
        .set("date", Timestamp.parseTimestamp(date.toString()))
        .set(
            "listOfStrings",
            listOfStrings
                .fold(ListValue.newBuilder()) { acc, next -> acc.addValue(next) }
                .build()
        )
        .set(
            "listOfNumbers",
            listOfNumbers
                .fold(ListValue.newBuilder()) { acc, next -> acc.addValue(next.toLong()) }
                .build()
        )
        .set(
            "listOfObjects",
            listOfObjects
                .fold(ListValue.newBuilder()) { acc, next -> acc.addValue(next.toEntityManual()) }
                .build()
        )
        .build()

fun NestedClass.toEntityManual(): FullEntity<IncompleteKey>? =
    Entity.newBuilder()
        .set("number", number.toLong())
        .set("string", string)
        .set("bool", bool)
        .set("date", Timestamp.parseTimestamp(date.toString()))
        .build()
