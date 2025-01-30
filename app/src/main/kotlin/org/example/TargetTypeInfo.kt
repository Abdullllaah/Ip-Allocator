class TargetTypeInfo(val targetType: TargetType) {
    val list = if (targetType.ipPortTarget == ClientTargetType.CLIENT) DB.clientsIPsAndPorts else DB.serversIPsAndPorts
    val segment = if (targetType.ipPortTarget == ClientTargetType.CLIENT) DB.clientSegment else DB.serverSegment
}