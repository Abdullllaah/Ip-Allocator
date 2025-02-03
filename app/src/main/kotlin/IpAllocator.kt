import IpAllocator.IpAllocationType.RANDOM
import IpAllocator.IpAllocationType.SEQUENTIAL
import kotlin.random.Random

/** Handles ip address allocations */
object IpAllocator {
  enum class IpAllocationType {
    SEQUENTIAL,
    RANDOM
  }

  /** How many times to iterate when assigning an ip randomly */
  private val numberOfRandomIterations =
      10

  /**
   * Assign Ip based on the user type and allocation type
   *
   * @param allocationType Either [RANDOM] or [SEQUENTIAL]
   * @param targetTypeInfo An object that contains list and segment based on the user type from [DB]
   * @return [IpInformation] Including the secured ip and the port will be 0
   */
  fun assignIp(allocationType: IpAllocationType, targetTypeInfo: TargetTypeInfo): IpInformation {
    Logger.logger.info { "Attempting to assign ip to $allocationType" }
    return when (allocationType) {
      SEQUENTIAL -> assignSequentialIp(targetTypeInfo)
      RANDOM -> assignRandomIp(targetTypeInfo)
    }
  }

  /**
   * Loop through each filed in the segment and check if it exists in the secured ips
   *
   * @param targetTypeInfo An object that contains list and segment based on the user type
   * @return [IpInformation] Including the secured ip and the port will be 0
   * @throws [IllegalArgumentException] If there is no remaining address
   */
  private fun assignSequentialIp(targetTypeInfo: TargetTypeInfo): IpInformation {
    Logger.logger.debug { "Assigning sequential ip" }
    for (segment in targetTypeInfo.segments) {
      for (i in segment.fromFields[0]..segment.toFields[0]) {
        for (j in segment.fromFields[1]..segment.toFields[1]) {
          for (k in segment.fromFields[2]..segment.toFields[2]) {
            for (m in segment.fromFields[3]..segment.toFields[3]) {
              val ip = "$i.$j.$k.$m"
              if (!targetTypeInfo.list.containsKey(ip)) {
                targetTypeInfo.list[ip] = User(ip)
                Logger.logger.info { "Successfully assigned ip: $ip" }
                return IpInformation(ip, 0u)
              }
            }
          }
        }
      }
    }
    Logger.logger.warn { "There is no remaining IP address" }
    throw IllegalArgumentException("There is no remaining IP address")
  }

  /**
   * Assign a random number for each field in the segment and try [numberOfRandomIterations] times
   *
   * @param targetTypeInfo An object that contains list and segment based on the user type
   * @return [IpInformation] Including the secured ip and the port will be 0
   * @throws [IllegalArgumentException] If it reaches [numberOfRandomIterations] and couldn't assign
   *   an ip
   */
  private fun assignRandomIp(targetTypeInfo: TargetTypeInfo): IpInformation {
    Logger.logger.debug { "Assigning random ip in $numberOfRandomIterations tries" }
    for (segment in targetTypeInfo.segments) {
      for (n in 1..numberOfRandomIterations) {
        val first = Random.nextInt(segment.fromFields[0], segment.toFields[0] + 1)
        val second = Random.nextInt(segment.fromFields[1], segment.toFields[1] + 1)
        val third = Random.nextInt(segment.fromFields[2], segment.toFields[2] + 1)
        val fourth = Random.nextInt(segment.fromFields[3], segment.toFields[3] + 1)
        val ip = "$first.$second.$third.$fourth"
        if (!targetTypeInfo.list.containsKey(ip)) {
          targetTypeInfo.list[ip] = User(ip)
          Logger.logger.info { "Successfully assigned ip: $ip" }
          return IpInformation(ip, 0u)
        }
      }
    }
    Logger.logger.warn { "Could not assign random IP address" }
    throw IllegalArgumentException("Could not assign random IP address")
  }
}
