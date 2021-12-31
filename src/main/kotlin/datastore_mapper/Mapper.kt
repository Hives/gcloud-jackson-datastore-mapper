package datastore_mapper

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

fun Any.toEntity(key: Key): Entity {
    val entity = Entity.newBuilder(key)

    this::class.memberProperties.forEach {
        @Suppress("UNCHECKED_CAST")
        val property = it as KProperty1<Any, *>

        property.get(this)?.also { value ->
            when (value) {
                is String -> entity.set(property.name, value)
                is Int -> entity.set(property.name, value.toLong())
                is Boolean -> entity.set(property.name, value)
            }
        }
    }

    return entity.build()
}