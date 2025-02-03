/** Holds the ip range for allocation */
class Segment(
    ipFrom: String,
    ipTo: String,
) {
  /** Split the ip on "." to work with each field independently */
  val fromFields = ipFrom.split(".").map { it.toInt() }.toList()

  /** Split the ip on "." to work with each field independently */
  val toFields = ipTo.split(".").map { it.toInt() }.toList()
}
