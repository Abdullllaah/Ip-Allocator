/** Holds the ip range for allocation */
class Segment(
    ipFrom: String,
    ipTo: String,
) {
    init {
        require(validate(ipFrom)) { "Invalid IP from: $ipFrom" }
        require(validate(ipTo)) { "Invalid IP to: $ipTo" }
    }

    private val portsRange = 0..255
    private val fieldsNumber = 4

    private fun validate(ip: String): Boolean {
        val fields = ip.split(".")
        if (fields.size != fieldsNumber)
            return false
        return fields.all { field ->
            field.toIntOrNull()?.let { it in portsRange } ?: false
        }
    }

    /** Split the ip on "." to work with each field independently */
    val fromFields = ipFrom.split(".").map { it.toInt() }.toList()

    /** Split the ip on "." to work with each field independently */
    val toFields = ipTo.split(".").map { it.toInt() }.toList()
}
