import ClientTargetType.CLIENT
import ClientTargetType.SERVER
import io.github.oshai.kotlinlogging.KotlinLogging
import jooq.generated.tables.references.CLIENTS_IPS
import jooq.generated.tables.references.SERVERS_IPS
import jooq.generated.tables.references.SERVICES
import org.jooq.impl.DSL
import org.jooq.impl.DSL.max
import java.sql.DriverManager

/** Handles port allocations */
object PortAllocator {

    private val connection = DriverManager.getConnection("jdbc:sqlite:app/my_database.db")
    private val dsl = DSL.using(connection)


    /** The lower bound for ports */
    private const val MINIMUM_ALLOWED_PORT = 49_152

    /** The upper bound for ports */
    private const val MAXIMUM_ALLOWED_PORT = 65_535

    private const val DEFAULT_CLIENT_PORT = 50_000

    private val logger = KotlinLogging.logger {}

    /**
     * Loop through ports starting form 0 to the maximum port (65_535)
     *
     * @param ip The user ip to assign a port for
     * @param targetTypeInfo An object that contains list and segment based on the user type
     * @return [IpInformation] Including the secured ip and the secured port
     * @throws [IllegalArgumentException] If the client has a port
     */
    fun assignPort(ip: String, targetTypeInfo: TargetTypeInfo): IpInformation {
        logger.info { "Assigning port to $ip with ${targetTypeInfo.targetType} type " }
        when (targetTypeInfo.targetType.ipPortTarget) {
            SERVER -> {
                val newPort = dsl.select(max(SERVICES.PORT))
                    .from(SERVICES)
                    .where(SERVICES.IP.eq(ip))
                    .fetchOne()?.value1()?.plus(1) ?: MINIMUM_ALLOWED_PORT

                dsl.insertInto(SERVICES, SERVICES.IP, SERVICES.PORT)
                    .values(ip, newPort)
                    .execute()
                return IpInformation(ip, newPort.toUInt())
            }
            CLIENT -> {
                if (dsl.select(CLIENTS_IPS.PORT)
                        .from(CLIENTS_IPS)
                        .where(CLIENTS_IPS.IP.eq(ip))
                        .fetchOne()?.value1() != 0)
                    throw IllegalArgumentException("The client can't assign more than one port")
                dsl.update(CLIENTS_IPS)
                    .set(CLIENTS_IPS.PORT, DEFAULT_CLIENT_PORT)
                    .where(CLIENTS_IPS.IP.eq(ip))
                    .execute()
                return IpInformation(ip, DEFAULT_CLIENT_PORT.toUInt())
            }
        }
//        logger.warn { "There is no available port" }
//        throw IllegalArgumentException("There is no available port")
    }
}
