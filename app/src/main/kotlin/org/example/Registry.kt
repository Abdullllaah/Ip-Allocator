import Allocater.IpAllocationType.RANDOM
import Allocater.IpAllocationType.SEQUENTIAL

object Registry: GrpcApi() {
    /** Print the lists for testing */
    fun viewAll() {
        println(DB.clientsIPsAndPorts)
        println(DB.serversIPsAndPorts)
    }

    /**
     * Determine the type of the ip (Client or Server)
     * @param ipInfo The ip of class IpInformation
     * @return The type of the ip of class TargetType*/
    private fun determineTargetType(ipInfo: IpInformation): TargetType =
        if (DB.clientsIPsAndPorts.containsKey(ipInfo.ip)) TargetType(ClientTargetType.CLIENT)
        else if (DB.serversIPsAndPorts.containsKey(ipInfo.ip)) TargetType(ClientTargetType.SERVER)
        else throw IllegalArgumentException("IP ${ipInfo.ip} not found")

    override fun secureIp(targetType: TargetType): IpInformation {
        return Allocater.assignIp(RANDOM, TargetTypeInfo(targetType))
    }

    override fun securePort(ipInfo: IpInformation): IpInformation {
        val targetType = determineTargetType(ipInfo)
        if (targetType.ipPortTarget == ClientTargetType.CLIENT)
            throw IllegalArgumentException("Client is not allowed to secure a port")
        return Allocater.assignPort(ipInfo.ip, TargetTypeInfo(targetType))
    }

    override fun SecureIpAndPort(targetType: TargetType): IpInformation { // wrong formatting
        if (targetType.ipPortTarget == ClientTargetType.CLIENT)
            throw IllegalArgumentException("Client is not allowed to secure a port")
        return securePort(secureIp(targetType))
    }

    override fun FreePort(ipInfo: IpInformation) {
        val targetType = determineTargetType(ipInfo)
        // It will remove the port if it exists and if it's not it won't return an error
        TargetTypeInfo(targetType).list[ipInfo.ip]!!.ports.remove(ipInfo.port)
    }

    override fun FreeIpAndAllPorts(ipInfo: IpInformation) {
        val targetType = determineTargetType(ipInfo)
        TargetTypeInfo(targetType).list.remove(ipInfo.ip)
    }

    override fun SyncSecuredIpsAndPorts(listOfIpInfo: ListOfIpInformation) {
        TODO("Not yet implemented")
    }

    override fun GetDomainName(): DomainNameInformation {
       return DomainNameInformation("www.stc.com")
    }

    override fun GetAllowedIPsForClients(): ListOfAllowedIPs {
        TODO("Not yet implemented")
    }

}