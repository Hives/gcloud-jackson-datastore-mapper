package datastore_mapper.to_domain

import com.google.cloud.datastore.Entity
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability

fun <T : Any> Entity.toDomain(targetClass: KClass<T>): T {
    val primaryConstructor = targetClass.primaryConstructor!!

    val constructorArguments = primaryConstructor
        .parameters
        .associateWith(this::getValue)

    return primaryConstructor.callBy(constructorArguments)
}

private fun Entity.getValue(parameter: KParameter) =
    when (parameter.type.withNullability(false)) {
        String::class.createType() -> this.getOptionalString(parameter.name)
        Int::class.createType() -> this.getOptionalInt(parameter.name)
        Boolean::class.createType() -> this.getOptionalBoolean(parameter.name)
        else -> TODO("Support for ${parameter.type} not implemented")
    }
