import java.time.LocalDateTime
import java.time.ZoneOffset

/** A class that holds user information (ip, date, ports)*/
class User(val ip: String) {
  /** Date of assigning the ips to use it for checking expiration */
  val date: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
  val ports = mutableListOf<UInt>()

  override fun toString(): String {
    return "(IP:${this.ip}, Date: ${this.date}, Ports: ${this.ports})"
  }
}
