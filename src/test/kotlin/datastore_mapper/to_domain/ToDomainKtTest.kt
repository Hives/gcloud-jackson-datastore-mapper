package datastore_mapper.to_domain

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.ListValue
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

    "can convert a String property" {
        data class TestClass(val id: String, val stringProperty: String)

        val entity = Entity.newBuilder(createKey("id")).set("stringProperty", "Foo").build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", stringProperty = "Foo")
    }

    "can convert an optional String property" {
        data class TestClass(val id: String, val stringProperty: String?)

        val entity = Entity.newBuilder(createKey("id")).set("stringProperty", "Foo").build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", stringProperty = "Foo")
    }

    "sets an optional String property to null if not present on the entity" {
        data class TestClass(val id: String, val stringProperty: String?)

        val entity = Entity.newBuilder(createKey("id")).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", stringProperty = null)
    }

    "can convert an Int property" {
        data class TestClass(val id: String, val intProperty: Int)

        val entity = Entity.newBuilder(createKey("id")).set("intProperty", 42L).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", intProperty = 42)
    }

    "can convert an optional Int property" {
        data class TestClass(val id: String, val intProperty: Int?)

        val entity = Entity.newBuilder(createKey("id")).set("intProperty", 42L).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", intProperty = 42)
    }

    "sets an optional Int property to null if not present on the entity" {
        data class TestClass(val id: String, val intProperty: Int?)

        val entity = Entity.newBuilder(createKey("id")).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", intProperty = null)
    }

    "can convert a Boolean property" {
        data class TestClass(val id: String, val booleanProperty: Boolean)

        val entity = Entity.newBuilder(createKey("id")).set("booleanProperty", false).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", booleanProperty = false)
    }

    "can convert an optional Boolean property" {
        data class TestClass(val id: String, val booleanProperty: Boolean?)

        val entity = Entity.newBuilder(createKey("id")).set("booleanProperty", false).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", booleanProperty = false)
    }

    "sets an optional Boolean property to null if not present on the entity" {
        data class TestClass(val id: String, val booleanProperty: Boolean?)

        val entity = Entity.newBuilder(createKey("id")).build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", booleanProperty = null)
    }

    "can convert a list of string values" {
        data class TestClass(val id: String, val listProperty: List<String>)

        val listValue = ListValue.newBuilder()
        listValue.addValue("one")
        listValue.addValue("two")

        val entity = Entity.newBuilder(createKey("id"))
            .set("listProperty", listValue.build())
            .build()

        val result = entity.toDomain(TestClass::class, TestClass::id)

        result shouldBe TestClass(id = "id", listProperty = listOf("one", "two"))
    }
})

private fun createKey(name: String) = Key.newBuilder("project-id", "KindName", name).build()
private fun createKey(id: Long) = Key.newBuilder("project-id", "KindName", id).build()

