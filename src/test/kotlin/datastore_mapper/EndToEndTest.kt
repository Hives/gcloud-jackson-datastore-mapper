package datastore_mapper

import com.google.cloud.datastore.Key
import datastore_mapper.gcloud.EmulatedDatastore
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

//class EndToEndTest : StringSpec({
//    val datastore = EmulatedDatastore.instance
//
//    data class TestClass(
//        val id: String,
//        val stringProperty: String,
//        val intProperty: Int,
//        val booleanProperty: Boolean,
//        val listProperty: List<String>
//    )
//
//    beforeEach {
//        datastore.clean()
//    }
//
//    "can convert an object to an entity, persist it, retrieve it and convert it back" {
//        val testObject = TestClass(
//            id = "my-test-id",
//            stringProperty = "FooBar",
//            intProperty = 42,
//            booleanProperty = false,
//            listProperty = listOf("one", "two", "three")
//        )
//
//        val entity = testObject.toEntity(TestClass::id, datastore.project, "TestKind")
//
//        datastore.client.put(entity)
//
//        val key = Key.newBuilder(datastore.project, "TestKind", testObject.id).build()
//
//        val retrievedEntity = datastore.client.get(key)
//
//        val result = retrievedEntity.toDomain(TestClass::class, TestClass::id)
//
//        result shouldBe testObject
//    }
//
//})