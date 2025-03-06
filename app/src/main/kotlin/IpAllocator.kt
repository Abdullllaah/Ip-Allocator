import ClientTargetType.CLIENT
import ClientTargetType.SERVER
import io.github.oshai.kotlinlogging.KotlinLogging
import jooq.generated.tables.references.*
import org.jooq.impl.DSL.count
import org.jooq.impl.DSL.currentTimestamp

/** Handles ip address allocations */
object IpAllocator {

    enum class IpAllocationType {
        SEQUENTIAL,
        RANDOM
    }

    private val logger = KotlinLogging.logger {}

    /** How many times to iterate when assigning an ip randomly */
    private const val NUMBER_OF_RANDOM_ITERATIONS =
        10

    /**
     * Assign Ip based on the user type and allocation type
     *
     * @param targetType The type of the ip
     * @return [IpInformation] Including the secured ip and the port will be 0
     */
    fun assignIp(targetType: TargetType): IpInformation {
        logger.info { "Attempting to assign ip to ${targetType.ipPortTarget} type" }
        return when (targetType.ipPortTarget) {
            SERVER -> assignIpForServer()
            CLIENT -> assignIpForClient()
        }
    }

    private fun assignIpForServer(): IpInformation {
        var ip = ""
        Connect.dsl.transaction { ctx ->
            val record = ctx.dsl().select(SERVERS_IPS.IP)
                .from(SERVERS_IPS)
                .where(SERVERS_IPS.ALLOCATED.eq(false))
                .limit(1)
                .fetchOne()

            if (record == null) {
                throw IllegalStateException("No available IPs")
            }

            ip = record.get(SERVERS_IPS.IP).toString()

            ctx.dsl().update(SERVERS_IPS)
                .set(SERVERS_IPS.ALLOCATED, true)
                .set(SERVERS_IPS.DATE, currentTimestamp().cast(String::class.java))
                .where(SERVERS_IPS.IP.eq(ip))
                .execute()

            ctx.dsl().insertInto(SERVERS, SERVERS.IP)
                .values(ip)
                .execute()
        }

        return IpInformation(ip, 0u)
    }

    private fun assignIpForClient(): IpInformation {
        var ip = ""
        Connect.dsl.transaction { ctx ->
            val record = ctx.dsl().select(CLIENTS_IPS.IP)
                .from(CLIENTS_IPS)
                .where(CLIENTS_IPS.ALLOCATED.eq(false))
                .limit(1)
                .fetchOne()

            if (record == null) {
                throw IllegalStateException("No available IPs")
            }

            ip = record.get(CLIENTS_IPS.IP).toString()

            ctx.dsl().update(CLIENTS_IPS)
                .set(CLIENTS_IPS.ALLOCATED, true)
                .set(CLIENTS_IPS.DATE, currentTimestamp().cast(String::class.java))
                .where(CLIENTS_IPS.IP.eq(ip))
                .execute()

            ctx.dsl().insertInto(CLIENTS_INFO, CLIENTS_INFO.IP)
                .values(ip)
                .returning(CLIENTS_INFO.IP)
                .execute()

            val service = ctx.dsl()
                .select(SERVICES.ID)
                .from(SERVICES)
                .leftJoin(CLIENTS).on(SERVICES.ID.eq(CLIENTS.SERVICE_ID))
                .groupBy(SERVICES.ID)
                .orderBy(count(CLIENTS.IP).asc())
                .limit(1)
                .fetchOne()
                ?.value1()

            if(service == null)
                throw IllegalStateException("There is no service to connect with")

            ctx.dsl().insertInto(CLIENTS, CLIENTS.SERVICE_ID, CLIENTS.IP)
                .values(service, ip)
                .execute()

        }

        return IpInformation(ip, 0u)
    }

    fun freeIp(ipInfo: IpInformation, targetType: TargetType){
        when (targetType.ipPortTarget){
            SERVER -> {
                Connect.dsl.transaction { ctx ->
                    ctx.dsl().update(SERVERS_IPS)
                        .set(SERVERS_IPS.ALLOCATED, false)
                        .set(SERVERS_IPS.DATE, null as String?)
                        .where(SERVERS_IPS.IP.eq(ipInfo.ip))
                        .returningResult()
                        .fetch(SERVERS_IPS.IP)

                    ctx.dsl().deleteFrom(SERVERS)
                        .where(SERVERS.IP.eq(ipInfo.ip))
                        .execute()

                    ctx.dsl().deleteFrom(SERVICES)
                        .where(SERVICES.IP.eq(ipInfo.ip))
                        .execute()
                }
            }
            CLIENT -> {
                Connect.dsl.transaction { ctx ->
                    ctx.dsl().update(CLIENTS_IPS)
                        .set(CLIENTS_IPS.PORT, 0)
                        .set(CLIENTS_IPS.ALLOCATED, false)
                        .set(CLIENTS_IPS.DATE, null as String?)
                        .where(CLIENTS_IPS.IP.eq(ipInfo.ip))
                        .returningResult()
                        .fetch(CLIENTS_IPS.IP)

                    ctx.dsl().deleteFrom(CLIENTS_INFO)
                        .where(CLIENTS_INFO.IP.eq(ipInfo.ip))
                        .execute()

                    ctx.dsl().deleteFrom(CLIENTS)
                        .where(CLIENTS.IP.eq(ipInfo.ip))
                        .execute()
                }

            }
        }
    }
}
