import io.github.oshai.kotlinlogging.KotlinLogging
import jooq.generated.tables.references.*
import org.jooq.DatePart
import org.jooq.impl.DSL.currentLocalDateTime
import org.jooq.impl.DSL.localDateTimeSub
import java.time.LocalDateTime

/** An object that handles the database through the admin registry*/
object Admin {
    private val logger = KotlinLogging.logger {}

    fun allocateIps(segment: Segment, type: ClientTargetType) {
        val serverIps = mutableListOf<String>()
        val clientIps = mutableListOf<String>()
        for (i in segment.fromFields[0]..segment.toFields[0]) {
            for (j in segment.fromFields[1]..segment.toFields[1]) {
                for (k in segment.fromFields[2]..segment.toFields[2]) {
                    for (m in segment.fromFields[3]..segment.toFields[3]) {
                        val ip = "$i.$j.$k.$m"
                        when (type) {
                            ClientTargetType.SERVER -> serverIps += ip
                            ClientTargetType.CLIENT -> clientIps += ip
                        }
                    }
                }
            }
        }

        if (serverIps.isNotEmpty()) {
            val serversBatch = Connect.dsl.batch(
                Connect.dsl.insertInto(SERVERS_IPS, SERVERS_IPS.IP).values("")
            )
            serverIps.forEach { ip ->
                serversBatch.bind(ip)
            }
            serversBatch.execute()
        } else {
            val clientsBatch = Connect.dsl.batch(
                Connect.dsl.insertInto(CLIENTS_IPS, CLIENTS_IPS.IP).values("")
            )
            clientIps.forEach { ip ->
                clientsBatch.bind(ip)
            }
            clientsBatch.execute()
        }
    }

    fun freeExpiredIps() {
        logger.info { "Freeing expired IPs" }

        val lifeTimeSec = Connect.dsl
            .select(CONFIG.LIFE_TIME_SEC)
            .from(CONFIG)
            .fetchOneInto(Int::class.java)

        Connect.dsl.transaction { ctx ->
            val expiredClientIps = ctx.dsl().update(CLIENTS_IPS)
                .set(CLIENTS_IPS.PORT, 0)
                .set(CLIENTS_IPS.ALLOCATED, false)
                .set(CLIENTS_IPS.DATE, null as LocalDateTime?)
                .where(CLIENTS_IPS.DATE.lt(localDateTimeSub(currentLocalDateTime(), lifeTimeSec, DatePart.SECOND)))
                .returningResult()
                .fetch(CLIENTS_IPS.IP)

            ctx.dsl().deleteFrom(CLIENTS_INFO)
                .where(CLIENTS_INFO.IP.`in`(expiredClientIps))
                .execute()

            ctx.dsl().deleteFrom(CLIENTS)
                .where(CLIENTS.IP.`in`(expiredClientIps))
                .execute()
        }

        Connect.dsl.transaction { ctx ->
            val expiredServersIps = ctx.dsl().update(SERVERS_IPS)
                .set(SERVERS_IPS.ALLOCATED, false)
                .set(SERVERS_IPS.DATE, null as LocalDateTime?)
                .where(CLIENTS_IPS.DATE.lt(localDateTimeSub(currentLocalDateTime(), lifeTimeSec, DatePart.SECOND)))
                .returningResult()
                .fetch(SERVERS_IPS.IP)

            ctx.dsl().deleteFrom(SERVERS)
                .where(SERVERS.IP.`in`(expiredServersIps))
                .execute()

            ctx.dsl().deleteFrom(SERVICES)
                .where(SERVICES.IP.`in`(expiredServersIps))
                .execute()
        }
    }
}