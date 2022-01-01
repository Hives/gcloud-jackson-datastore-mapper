package datastore_mapper.to_domain

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Value

fun Entity.getOptionalString(name: String?): String? =
    if (this.contains(name)) this.getString(name)
    else null

fun Entity.getOptionalInt(name: String?): Int? =
    if (this.contains(name)) this.getLong(name).toInt()
    else null

fun Entity.getOptionalBoolean(name: String?): Boolean? =
    if (this.contains(name)) this.getBoolean(name)
    else null

fun Entity.getListOfStrings(name: String): List<String>? =
    this.getList<Value<String>>(name).map { it.get() }
