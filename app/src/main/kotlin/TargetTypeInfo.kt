/** A class that holds the list and segments based on user type */
class TargetTypeInfo(val targetType: TargetType) {
  /** The list of secured ips based on user type */
  val list =
      if (targetType.ipPortTarget == ClientTargetType.CLIENT) DB.clientsIPsAndPorts
      else DB.serversIPsAndPorts

  /** The segments of the allowed ips based on user type */
  val segments =
      if (targetType.ipPortTarget == ClientTargetType.CLIENT) DB.clientSegments
      else DB.serverSegments
}
