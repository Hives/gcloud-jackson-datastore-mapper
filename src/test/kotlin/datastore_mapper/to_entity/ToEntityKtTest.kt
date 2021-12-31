package datastore_mapper.to_entity

import com.google.cloud.datastore.Key
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class ToEntityKtTest : StringSpec({
    val mockKey = mockk<Key>()

    "returns an entity with the key set to the provided value" {
        data class TestClass(val property: String)
        val testObject = TestClass("Foo")
        val entity = testObject.toEntity(mockKey)

        entity.key shouldBe mockKey
    }

    "a string property gets set as a string" {
        data class TestClass(val stringProperty: String)
        val testObject = TestClass(stringProperty = "Foo")
        val entity = testObject.toEntity(mockKey)

        entity.contains("stringProperty") shouldBe true
        entity.getString("stringProperty") shouldBe "Foo"
    }

    "an optional string property gets set as a string" {
        data class TestClass(val optionalStringProperty: String?)
        val testObject = TestClass(optionalStringProperty = "Foo")
        val entity = testObject.toEntity(mockKey)

        entity.contains("optionalStringProperty") shouldBe true
        entity.getString("optionalStringProperty") shouldBe "Foo"
    }

    "an optional string property with value null does not get set" {
        data class TestClass(val optionalStringProperty: String?)
        val testObject = TestClass(optionalStringProperty = null)
        val entity = testObject.toEntity(mockKey)

        entity.contains("optionalStringProperty") shouldBe false
    }

    "an Int property gets set as a Long" {
        data class TestClass(val intProperty: Int)
        val testObject = TestClass(intProperty = 42)
        val entity = testObject.toEntity(mockKey)

        entity.contains("intProperty") shouldBe true
        entity.getLong("intProperty") shouldBe 42
    }

    "an optional Int property gets set as a Long" {
        data class TestClass(val optionalIntProperty: Int?)
        val testObject = TestClass(optionalIntProperty = 42)
        val entity = testObject.toEntity(mockKey)

        entity.contains("optionalIntProperty") shouldBe true
        entity.getLong("optionalIntProperty") shouldBe 42
    }

    "an optional Int property with value null does not get set" {
        data class TestClass(val optionalIntProperty: Int?)
        val testObject = TestClass(optionalIntProperty = null)
        val entity = testObject.toEntity(mockKey)

        entity.contains("optionalIntProperty") shouldBe false
    }

    "a Boolean property gets set as a Boolean" {
        data class TestClass(val booleanProperty: Boolean)
        val testObject = TestClass(booleanProperty = false)
        val entity = testObject.toEntity(mockKey)

        entity.contains("booleanProperty") shouldBe true
        entity.getBoolean("booleanProperty") shouldBe false
    }

    "an optional Boolean property gets set as a Boolean" {
        data class TestClass(val optionalBooleanProperty: Boolean?)
        val testObject = TestClass(optionalBooleanProperty = false)
        val entity = testObject.toEntity(mockKey)

        entity.contains("optionalBooleanProperty") shouldBe true
        entity.getBoolean("optionalBooleanProperty") shouldBe false
    }

    "an optional Boolean property with value null does not get set" {
        data class TestClass(val optionalBooleanProperty: Boolean?)
        val testObject = TestClass(optionalBooleanProperty = null)
        val entity = testObject.toEntity(mockKey)

        entity.contains("optionalBooleanProperty") shouldBe false
    }
})
