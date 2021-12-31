package datastore_mapper.to_domain

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class ToDomainKtTest : StringSpec({
    val mockKey = mockk<Key>()

    "can convert a String parameter" {
        data class TestClass(val stringProperty: String)

        val entity = Entity.newBuilder(mockKey).set("stringProperty", "Foo").build()

        entity.toDomain(TestClass::class) shouldBe TestClass(stringProperty = "Foo")
    }

    "can convert an optional String parameter" {
        data class TestClass(val stringProperty: String?)

        val entity = Entity.newBuilder(mockKey).set("stringProperty", "Foo").build()

        entity.toDomain(TestClass::class) shouldBe TestClass(stringProperty = "Foo")
    }

    "sets an optional String parameter to null if not present on the entity" {
        data class TestClass(val stringProperty: String?)

        val entity = Entity.newBuilder(mockKey).build()

        entity.toDomain(TestClass::class) shouldBe TestClass(stringProperty = null)
    }

    "can convert an Int parameter" {
        data class TestClass(val intParameter: Int)

        val entity = Entity.newBuilder(mockKey).set("intParameter", 42L).build()

        entity.toDomain(TestClass::class) shouldBe TestClass(intParameter = 42)
    }

    "can convert an optional Int parameter" {
        data class TestClass(val intParameter: Int?)

        val entity = Entity.newBuilder(mockKey).set("intParameter", 42L).build()

        entity.toDomain(TestClass::class) shouldBe TestClass(intParameter = 42)
    }

    "sets an optional Int parameter to null if not present on the entity" {
        data class TestClass(val intParameter: Int?)

        val entity = Entity.newBuilder(mockKey).build()

        entity.toDomain(TestClass::class) shouldBe TestClass(intParameter = null)
    }

    "can convert a Boolean parameter" {
        data class TestClass(val booleanParameter: Boolean)

        val entity = Entity.newBuilder(mockKey).set("booleanParameter", false).build()

        entity.toDomain(TestClass::class) shouldBe TestClass(booleanParameter = false)
    }

    "can convert an optional Boolean parameter" {
        data class TestClass(val booleanParameter: Boolean?)

        val entity = Entity.newBuilder(mockKey).set("booleanParameter", false).build()

        entity.toDomain(TestClass::class) shouldBe TestClass(booleanParameter = false)
    }

    "sets an optional Boolean parameter to null if not present on the entity" {
        data class TestClass(val booleanParameter: Boolean?)

        val entity = Entity.newBuilder(mockKey).build()

        entity.toDomain(TestClass::class) shouldBe TestClass(booleanParameter = null)
    }
})
