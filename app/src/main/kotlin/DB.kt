import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.Duration
import java.time.LocalDateTime

/** A class that acts like a database */
object DB {
    /** Map of secured ips to clients */
    val clientsIPsAndPorts = hashMapOf<String, User>()

    /** Map of secured ips to servers */
    val serversIPsAndPorts = hashMapOf<String, User>()

    /** Segments of allowed ips for clients */
    val clientSegments = listOf(Segment("192.0.0.0", "192.255.0.0"))

    /** Segments of allowed ips for servers*/
    val serverSegments = listOf(Segment("127.0.0.0", "127.0.255.0"))

    /** Duration for validating expiration of an ip address (in seconds) */
    private const val LIFE_TIME_SECONDS = 2

    private val logger = KotlinLogging.logger {}

    /** Free the expired ips based on [LIFE_TIME_SECONDS] */
    fun freeExpiredIps() {
        logger.info { "Freeing expired IPs" }
        serversIPsAndPorts.entries.removeIf { (_, user) ->
            Duration.between(user.date, LocalDateTime.now()).toSeconds() > LIFE_TIME_SECONDS
        }
        clientsIPsAndPorts.entries.removeIf { (_, user) ->
            Duration.between(user.date, LocalDateTime.now()).toSeconds() > LIFE_TIME_SECONDS
        }
    }
}
