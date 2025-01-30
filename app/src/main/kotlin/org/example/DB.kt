import java.time.Duration
import java.time.LocalDateTime

object DB {
    val serversIPsAndPorts = hashMapOf<String, User>()
    val clientsIPsAndPorts = hashMapOf<String, User>()
    val clientSegment = Segment("192.0.0.0", "192.255.0.0")
    val serverSegment = Segment("127.0.0.0", "127.0.255.1")
    val LIFE_TIME_SECONDS = 2

    fun freeExpiredIps(){
        serversIPsAndPorts.entries.removeIf { (_, user) -> Duration.between(user.date, LocalDateTime.now()).toSeconds() > LIFE_TIME_SECONDS}
        clientsIPsAndPorts.entries.removeIf { (_, user) -> Duration.between(user.date, LocalDateTime.now()).toSeconds() > LIFE_TIME_SECONDS}
    }
}