/** Handles port allocations */
object PortAllocator {

  /** The lower bound for ports */
  private val minimumAllowedPorts = 49_152u
  /** The upper bound for ports */
  private val maximumAllowedPorts = 65_535u

  /**
   * Loop through ports starting form 0 to the maximum port (65_535)
   *
   * @param ip The user ip to assign a port for
   * @param targetTypeInfo An object that contains list and segment based on the user type
   * @return [IpInformation] Including the secured ip and the secured port
   * @throws [IllegalArgumentException] If the client has a port
   */
  fun assignPort(ip: String, targetTypeInfo: TargetTypeInfo): IpInformation {
    Logger.logger.info { "Assigning port to $ip with ${targetTypeInfo.targetType} type " }
    val user = targetTypeInfo.list[ip]
    if (targetTypeInfo.targetType.ipPortTarget == ClientTargetType.CLIENT &&
        user!!.ports.isNotEmpty()) {
      Logger.logger.warn { "The client can't assign more than one port" }
      throw IllegalArgumentException("The client can't assign more than one port")
    }
    for (i in minimumAllowedPorts..maximumAllowedPorts) if (!user!!.ports.contains(i)) {
      user.ports.add(i)
      Logger.logger.info { "Successfully assigned port $i" }
      return IpInformation(ip, i)
    }
    Logger.logger.warn { "There is no available port" }
    throw IllegalArgumentException("There is no available port")
  }
}
