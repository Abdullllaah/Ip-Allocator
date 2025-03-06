import ClientTargetType.CLIENT
import ClientTargetType.SERVER
import jooq.generated.tables.references.CLIENTS_IPS
import jooq.generated.tables.references.SERVERS_IPS

/** A class that acts like a registry*/
object Registry : GrpcApi() {

    /**
     * Determine the type of the ip (Client or Server) *
     *
     * @param ipInfo The ip of class IpInformation
     * @return [TargetType] The type of the ip of class TargetType
     */
    private fun determineTargetType(ipInfo: IpInformation): TargetType {
        return if (Connect.dsl.fetchExists(
                Connect.dsl.selectFrom(SERVERS_IPS).where(SERVERS_IPS.IP.eq(ipInfo.ip))
            )){
            TargetType(SERVER)
        } else if (Connect.dsl.fetchExists(
                Connect.dsl.selectFrom(CLIENTS_IPS).where(CLIENTS_IPS.IP.eq(ipInfo.ip))
            )) {
            TargetType(CLIENT)
        } else {
            throw IllegalArgumentException("IP ${ipInfo.ip} not found")
        }
    }

    override fun secureIp(targetType: TargetType): IpInformation {
        return IpAllocator.assignIp(targetType)
    }

    override fun securePort(ipInfo: IpInformation): IpInformation {
        val targetType = determineTargetType(ipInfo)
        return PortAllocator.assignPort(ipInfo.ip, targetType)
    }

    override fun SecureIpAndPort(targetType: TargetType): IpInformation {
        return securePort(secureIp(targetType))
    }

    override fun FreePort(ipInfo: IpInformation) {
        val targetType = determineTargetType(ipInfo)
        PortAllocator.freePort(ipInfo, targetType)
    }

    override fun FreeIpAndAllPorts(ipInfo: IpInformation) {
        val targetType = determineTargetType(ipInfo)
        IpAllocator.freeIp(ipInfo, targetType)
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
