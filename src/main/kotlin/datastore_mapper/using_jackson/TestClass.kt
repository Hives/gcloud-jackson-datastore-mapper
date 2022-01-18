import java.math.BigDecimal
import java.time.Instant

data class TestClass(
    val id: String,
    val integer: Int,
    val long: Long,
    val bigDecimal: BigDecimal,
    val string: String,
    val bool: Boolean,
    val nested: NestedClass,
    val date: Instant,
    val listOfStrings: List<String>,
    val listOfNumbers: List<Int>,
    val listOfObjects: List<NestedClass>,
) {
    data class NestedClass(
        val number: Int,
        val string: String,
        val bool: Boolean,
        val date: Instant,
    )
}
