class Segment (
    ipFrom:String,
    ipTo:String,
){
    private val fromFields = ipFrom.split(".")
    private val toFields = ipTo.split(".")

    val firstFieldFrom: Int = fromFields[0].toInt()
    val secondFieldFrom: Int = fromFields[1].toInt()
    val thirdFieldFrom: Int = fromFields[2].toInt()
    val fourthFieldFrom: Int = fromFields[3].toInt()

    val firstFieldTo: Int = toFields[0].toInt()
    val secondFieldTo: Int = toFields[1].toInt()
    val thirdFieldTo: Int = toFields[2].toInt()
    val fourthFieldTo: Int = toFields[3].toInt()
}
