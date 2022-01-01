package datastore_mapper.to_entity

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

fun <T: Any> T.toEntity(
    keyProperty: KProperty1<T, Any>,
    projectId: String,
    kindName: String,
): Entity {
    val key = createKey(keyProperty.get(this), kindName, projectId)

    val entity = Entity.newBuilder(key)

    this::class.memberProperties
        .filterNot { it == keyProperty }
        .forEach {
            @Suppress("UNCHECKED_CAST")
            val property = it as KProperty1<T, *>

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

private fun createKey(keyValue: Any, kindName: String, projectId: String) =
    when (keyValue) {
        is String -> Key.newBuilder(projectId, kindName, keyValue).build()
        is Int -> Key.newBuilder(projectId, kindName, keyValue.toLong()).build()
        is Long -> Key.newBuilder(projectId, kindName, keyValue).build()
        else -> throw RuntimeException("Could not create key from $keyValue, unrecognised type: ${keyValue::class}")
    }
