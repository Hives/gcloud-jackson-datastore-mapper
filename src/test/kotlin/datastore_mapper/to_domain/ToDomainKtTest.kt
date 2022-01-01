package datastore_mapper.to_domain

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ToDomainKtTest : StringSpec({
    "can read a string id into a given property" {
        data class TestClass(val id: String)

        val entity = Entity.newBuilder(createKey("my-id")).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "my-id")
    }

    "can read a numerical id into a property of type Int" {
        data class TestClass(val id: Int)

        val entity = Entity.newBuilder(createKey(123456)).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = 123456)
    }

    "can read a numerical id into a property of type Long" {
        data class TestClass(val id: Long)

        val entity = Entity.newBuilder(createKey(123456)).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = 123456)
    }

    "can convert a String parameter" {
        data class TestClass(val id: String, val stringProperty: String)

        val entity = Entity.newBuilder(createKey("id")).set("stringProperty", "Foo").build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", stringProperty = "Foo")
    }

    "can convert an optional String parameter" {
        data class TestClass(val id: String, val stringProperty: String?)

        val entity = Entity.newBuilder(createKey("id")).set("stringProperty", "Foo").build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", stringProperty = "Foo")
    }

    "sets an optional String parameter to null if not present on the entity" {
        data class TestClass(val id: String, val stringProperty: String?)

        val entity = Entity.newBuilder(createKey("id")).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", stringProperty = null)
    }

    "can convert an Int parameter" {
        data class TestClass(val id: String, val intParameter: Int)

        val entity = Entity.newBuilder(createKey("id")).set("intParameter", 42L).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", intParameter = 42)
    }

    "can convert an optional Int parameter" {
        data class TestClass(val id: String, val intParameter: Int?)

        val entity = Entity.newBuilder(createKey("id")).set("intParameter", 42L).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", intParameter = 42)
    }

    "sets an optional Int parameter to null if not present on the entity" {
        data class TestClass(val id: String, val intParameter: Int?)

        val entity = Entity.newBuilder(createKey("id")).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", intParameter = null)
    }

    "can convert a Boolean parameter" {
        data class TestClass(val id: String, val booleanParameter: Boolean)

        val entity = Entity.newBuilder(createKey("id")).set("booleanParameter", false).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", booleanParameter = false)
    }

    "can convert an optional Boolean parameter" {
        data class TestClass(val id: String, val booleanParameter: Boolean?)

        val entity = Entity.newBuilder(createKey("id")).set("booleanParameter", false).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", booleanParameter = false)
    }

    "sets an optional Boolean parameter to null if not present on the entity" {
        data class TestClass(val id: String, val booleanParameter: Boolean?)

        val entity = Entity.newBuilder(createKey("id")).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", booleanParameter = null)
    }
})

private fun createKey(name: String) = Key.newBuilder("project-id", "KindName", name).build()
private fun createKey(id: Long) = Key.newBuilder("project-id", "KindName", id).build()

