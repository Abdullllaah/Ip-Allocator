import ClientTargetType.CLIENT
import ClientTargetType.SERVER
import IpAllocator.IpAllocationType.RANDOM
import IpAllocator.IpAllocationType.SEQUENTIAL
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jooq.impl.DSL
import java.sql.DriverManager
import kotlin.random.Random
import jooq.generated.tables.references.*
import org.jooq.impl.DSL.currentTimestamp
import java.lang.IllegalStateException
import java.sql.Timestamp

/** Handles ip address allocations */
object IpAllocator {
    private val connection = DriverManager.getConnection("jdbc:sqlite:app/my_database.db")
    private val dsl = DSL.using(connection)

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
     * @param allocationType Either [RANDOM] or [SEQUENTIAL]
     * @param targetTypeInfo An object that contains list and segment based on the user type from [DB]
     * @return [IpInformation] Including the secured ip and the port will be 0
     */
    fun assignIp(allocationType: IpAllocationType, targetTypeInfo: TargetTypeInfo): IpInformation { // Client must connect to a server?
        logger.info { "Attempting to assign ip to ${targetTypeInfo.targetType.ipPortTarget} type" }
        val table = when (targetTypeInfo.targetType.ipPortTarget) {
            SERVER -> SERVERS_IPS
            CLIENT -> CLIENTS_IPS
        }

        val record = dsl.select(table.field("ip"))
            .from(table)
            .where(table.field("allocated", Boolean::class.java)?.eq(false))
            .limit(1)
            .fetchOne()

        if (record == null) {
            throw IllegalStateException("No available IPs")
        }

        val ip = record.get(table.field("ip")).toString()

        dsl.update(table)
            .set(table.field("allocated", Boolean::class.java), true)
            .set(table.field("date", Timestamp::class.java), currentTimestamp())
            .where(table.field("ip", String::class.java)?.eq(ip))
            .execute()

       when (targetTypeInfo.targetType.ipPortTarget) {
           SERVER ->
               dsl.insertInto(SERVERS, SERVERS.IP)
                   .values(ip)
                   .execute()
           CLIENT ->
               dsl.insertInto(CLIENTS_INFO, CLIENTS_INFO.IP)
                   .values(ip)
                   .execute()
       }

        return IpInformation(ip, 0u)
//        return when (allocationType) {
//            SEQUENTIAL -> assignSequentialIp(targetTypeInfo)
//            RANDOM -> assignRandomIp(targetTypeInfo)
//        }
    }

    /**
     * Loop through each filed in the segment and check if it exists in the secured ips
     *
     * @param targetTypeInfo An object that contains list and segment based on the user type
     * @return [IpInformation] Including the secured ip and the port will be 0
     * @throws [IllegalArgumentException] If there is no remaining address
     */
    @Suppress("NestedBlockDepth")
    private fun assignSequentialIp(targetTypeInfo: TargetTypeInfo): IpInformation {
        logger.debug { "Assigning sequential ip" }
        for (segment in targetTypeInfo.segments) {
            for (i in segment.fromFields[0]..segment.toFields[0]) {
                for (j in segment.fromFields[1]..segment.toFields[1]) {
                    for (k in segment.fromFields[2]..segment.toFields[2]) {
                        for (m in segment.fromFields[3]..segment.toFields[3]) {
                            val ip = "$i.$j.$k.$m"
                            if (!targetTypeInfo.list.containsKey(ip))
                                return saveIp(targetTypeInfo, ip)
                        }
                    }
                }
            }
        }
        logger.warn { "There is no remaining IP address" }
        throw IllegalArgumentException("There is no remaining IP address")
    }

    /**
     * Assign a random number for each field in the segment and try [NUMBER_OF_RANDOM_ITERATIONS] times
     *
     * @param targetTypeInfo An object that contains list and segment based on the user type
     * @return [IpInformation] Including the secured ip and the port will be 0
     * @throws [IllegalArgumentException] If it reaches [NUMBER_OF_RANDOM_ITERATIONS] and couldn't assign
     *   an ip
     */
    private fun assignRandomIp(targetTypeInfo: TargetTypeInfo): IpInformation {
        logger.debug { "Assigning random ip in $NUMBER_OF_RANDOM_ITERATIONS tries" }
        for (segment in targetTypeInfo.segments) {
            repeat(NUMBER_OF_RANDOM_ITERATIONS) {
                val first = Random.nextInt(segment.fromFields[0], segment.toFields[0] + 1)
                val second = Random.nextInt(segment.fromFields[1], segment.toFields[1] + 1)
                val third = Random.nextInt(segment.fromFields[2], segment.toFields[2] + 1)
                val fourth = Random.nextInt(segment.fromFields[3], segment.toFields[3] + 1)
                val ip = "$first.$second.$third.$fourth"
                if (!targetTypeInfo.list.containsKey(ip))
                    return saveIp(targetTypeInfo, ip)
            }
        }
        logger.warn { "Could not assign random IP address" }
        throw IllegalArgumentException("Could not assign random IP address")
    }

    private fun saveIp(targetTypeInfo: TargetTypeInfo, ip: String): IpInformation {
        logger.debug { "Saving ip $ip" }
        targetTypeInfo.list[ip] = User(ip)
        logger.info { "Successfully assigned ip: $ip" }
        return IpInformation(ip, 0u)
    }
}
