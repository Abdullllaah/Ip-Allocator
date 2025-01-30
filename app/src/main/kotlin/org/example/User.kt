import java.time.LocalDateTime

class User(val ip: String) {
    val date: LocalDateTime = LocalDateTime.now().withNano(0)
    val ports = mutableListOf<UInt>()

    override fun toString(): String {
        return "(IP:${this.ip}, Date: ${this.date}, Ports: ${this.ports})"
    }
}