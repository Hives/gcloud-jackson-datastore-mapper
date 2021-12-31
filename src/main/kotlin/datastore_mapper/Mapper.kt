package datastore_mapper

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.withNullability

fun Any.toEntity(key: Key): Entity {
    val entity = Entity.newBuilder(key)

    this::class.memberProperties.forEach {
        @Suppress("UNCHECKED_CAST") val property = it as KProperty1<Any, *>
        when(property.returnType.withNullability(false)) {
            String::class.createType() -> {
                property.get(this)?.also { value ->
                    entity.set(property.name, value as String)
                }
            }
            Int::class.createType() -> {
                property.get(this)?.also { value ->
                    entity.set(property.name, (value as Int).toLong())
                }
            }
            else -> {
                throw RuntimeException("Unknown returnType: ${property.returnType}")
            }
        }
    }

    return entity.build()
}