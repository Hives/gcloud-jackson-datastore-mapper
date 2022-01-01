package datastore_mapper.to_domain

import com.google.cloud.datastore.Entity
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

fun <T : Any> Entity.toDomain(targetClass: KClass<T>, keyProperty: KProperty1<T, Any>): T {
    val primaryConstructor = targetClass.primaryConstructor!!

    val constructorArguments = primaryConstructor
        .parameters
        .associateWith {
            if (it.name == keyProperty.name) getKeyValue(it)
            else getValue(it)
        }

    return primaryConstructor.callBy(constructorArguments)
}

private fun Entity.getKeyValue(parameter: KParameter) =
    // TODO this will return null if key identifier is of the wrong type, and then calling the constructor fails. make a nice error message?
    when (parameter.type.withNullability(false)) {
        String::class.createType() -> key.name
        Int::class.createType() -> key.id.toInt()
        Long::class.createType() -> key.id
        else -> TODO("Support for key of type ${parameter.type} not implemented")
    }

@Suppress("IMPLICIT_CAST_TO_ANY")
private fun Entity.getValue(parameter: KParameter) =
    when (parameter.type.withNullability(false)) {
        typeOf<String>() -> this.getOptionalString(parameter.name)
        typeOf<Int>() -> this.getOptionalInt(parameter.name)
        typeOf<Boolean>() -> this.getOptionalBoolean(parameter.name)
        typeOf<List<String>>() -> this.getListOfStrings(parameter.name!!)
        else -> TODO("Support for property of type ${parameter.type} not implemented")
    }
