package datastore_mapper

import java.math.BigDecimal
import java.time.Instant

data class TestClass(
    val id: String,
    val string: String,
    val optional: String?,
    val int: Int,
    val boolean: Boolean,
    val bigDecimal: BigDecimal,
    val instant: Instant,
    val listOfStrings: List<String>,
    val listOfInts: List<Int>,
    val listOfObjects: List<NestedClass>,
    val nestedObject: NestedClass,
) {
    data class NestedClass(
        val string: String,
        val int: Int,
        val long: Long,
        val boolean: Boolean,
        val instant: Instant,
    )
}
