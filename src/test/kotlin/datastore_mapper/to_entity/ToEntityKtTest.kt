package datastore_mapper.to_entity

import com.google.cloud.datastore.Value
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class ToEntityKtTest : StringSpec({

    "can create a key from given String property" {
        data class TestClass(val id: String)
        val testObject = TestClass(id = "my-id")
        val entity = testObject.toEntity(TestClass::id, "my-test-project", "TestClassKind")

        entity.key.name shouldBe "my-id"
        entity.key.kind shouldBe "TestClassKind"
        entity.key.projectId shouldBe "my-test-project"
        entity.contains("id") shouldBe false
    }

    "can create a key from given Int property" {
        data class TestClass(val id: Int)
        val testObject = TestClass(id = 12345)
        val entity = testObject.toEntity(TestClass::id, "my-test-project", "TestClassKind")

        entity.key.id shouldBe 12345
        entity.key.kind shouldBe "TestClassKind"
        entity.key.projectId shouldBe "my-test-project"
        entity.contains("id") shouldBe false
    }

    "can create a key from given Long property" {
        data class TestClass(val id: Long)
        val testObject = TestClass(id = 12345)
        val entity = testObject.toEntity(TestClass::id, "my-test-project", "TestClassKind")

        entity.key.id shouldBe 12345
        entity.key.kind shouldBe "TestClassKind"
        entity.key.projectId shouldBe "my-test-project"
        entity.contains("id") shouldBe false
    }

    "a string property gets set as a string" {
        data class TestClass(val id: String, val stringProperty: String)
        val testObject = TestClass(id = "my-id", stringProperty = "Foo")
        val entity = testObject.toEntity(TestClass::id, "my-project", "MyKind")

        entity.contains("stringProperty") shouldBe true
        entity.getString("stringProperty") shouldBe "Foo"
    }

    "an optional string property gets set as a string" {
        data class TestClass(val id: String, val optionalStringProperty: String?)
        val testObject = TestClass(id = "my-id", optionalStringProperty = "Foo")
        val entity = testObject.toEntity(TestClass::id, "my-project", "MyKind")

        entity.contains("optionalStringProperty") shouldBe true
        entity.getString("optionalStringProperty") shouldBe "Foo"
    }

    "an optional string property with value null does not get set" {
        data class TestClass(val id: String, val optionalStringProperty: String?)
        val testObject = TestClass(id = "my-id", optionalStringProperty = null)
        val entity = testObject.toEntity(TestClass::id, "my-project", "MyKind")

        entity.contains("optionalStringProperty") shouldBe false
    }

    "an Int property gets set as a Long" {
        data class TestClass(val id: String, val intProperty: Int)
        val testObject = TestClass(id = "my-id", intProperty = 42)
        val entity = testObject.toEntity(TestClass::id, "my-project", "MyKind")

        entity.contains("intProperty") shouldBe true
        entity.getLong("intProperty") shouldBe 42
    }

    "an optional Int property gets set as a Long" {
        data class TestClass(val id: String, val optionalIntProperty: Int?)
        val testObject = TestClass(id = "my-id", optionalIntProperty = 42)
        val entity = testObject.toEntity(TestClass::id, "my-project", "MyKind")

        entity.contains("optionalIntProperty") shouldBe true
        entity.getLong("optionalIntProperty") shouldBe 42
    }

    "an optional Int property with value null does not get set" {
        data class TestClass(val id: String, val optionalIntProperty: Int?)
        val testObject = TestClass(id = "my-id", optionalIntProperty = null)
        val entity = testObject.toEntity(TestClass::id, "my-project", "MyKind")

        entity.contains("optionalIntProperty") shouldBe false
    }

    "a Boolean property gets set as a Boolean" {
        data class TestClass(val id: String, val booleanProperty: Boolean)
        val testObject = TestClass(id = "my-id", booleanProperty = false)
        val entity = testObject.toEntity(TestClass::id, "my-project", "MyKind")

        entity.contains("booleanProperty") shouldBe true
        entity.getBoolean("booleanProperty") shouldBe false
    }

    "an optional Boolean property gets set as a Boolean" {
        data class TestClass(val id: String, val optionalBooleanProperty: Boolean?)
        val testObject = TestClass(id = "my-id", optionalBooleanProperty = false)
        val entity = testObject.toEntity(TestClass::id, "my-project", "MyKind")

        entity.contains("optionalBooleanProperty") shouldBe true
        entity.getBoolean("optionalBooleanProperty") shouldBe false
    }

    "an optional Boolean property with value null does not get set" {
        data class TestClass(val id: String, val optionalBooleanProperty: Boolean?)
        val testObject = TestClass(id = "my-id", optionalBooleanProperty = null)
        val entity = testObject.toEntity(TestClass::id, "my-project", "MyKind")

        entity.contains("optionalBooleanProperty") shouldBe false
    }

    "a list of strings gets set as a list of strings" {
        data class TestClass(val id: String, val listProperty: List<String>)
        val testObject = TestClass(id = "my-id", listProperty = listOf("one", "two"))
        
        val entity = testObject.toEntity(TestClass::id, "my-project", "MyKind")

        entity.contains("listProperty") shouldBe true

        val listValues = entity.getList<Value<String>>("listProperty").map { it.get() }
        listValues shouldContainExactly listOf("one", "two")
    }
})
