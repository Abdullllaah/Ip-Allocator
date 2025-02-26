/** Holds the ip range for allocation */
class Segment(
    ipFrom: String,
    ipTo: String,
) {
    private val fieldsRange = 0..255
    private val fieldsNumber = 4

    init {
        require(validate(ipFrom)) { "Invalid IP from: $ipFrom" }
        require(validate(ipTo)) { "Invalid IP to: $ipTo" }
    }

    private fun validate(ip: String): Boolean {
        val fields = ip.split(".")
        if (fields.size != fieldsNumber)
            return false
        return fields.all { field ->
            field.toIntOrNull()?.let { it in fieldsRange } ?: false
        }
    }

    /** Split the ip on "." to work with each field independently */
    val fromFields = ipFrom.split(".").map { it.toInt() }.toList()

    /** Split the ip on "." to work with each field independently */
    val toFields = ipTo.split(".").map { it.toInt() }.toList()
}
