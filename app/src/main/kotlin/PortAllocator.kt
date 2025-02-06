import io.github.oshai.kotlinlogging.KotlinLogging

/** Handles port allocations */
object PortAllocator {

    /** The lower bound for ports */
    private const val MINIMUM_ALLOWED_PORT = 49_152u

    /** The upper bound for ports */
    private const val MAXIMUM_ALLOWED_PORT = 65_535u

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
        val user = targetTypeInfo.list[ip]
        if (targetTypeInfo.targetType.ipPortTarget == ClientTargetType.CLIENT &&
            user!!.ports.isNotEmpty()
        ) {
            logger.warn { "The client can't assign more than one port" }
            throw IllegalArgumentException("The client can't assign more than one port")
        }
        for (i in MINIMUM_ALLOWED_PORT..MAXIMUM_ALLOWED_PORT) if (!user!!.ports.contains(i)) {
            user.ports.add(i)
            logger.info { "Successfully assigned port $i" }
            return IpInformation(ip, i)
        }
        logger.warn { "There is no available port" }
        throw IllegalArgumentException("There is no available port")
    }
}
