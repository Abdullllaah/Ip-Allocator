import IpAllocator.IpAllocationType.SEQUENTIAL
import org.jooq.impl.DSL
import java.sql.DriverManager
import jooq.generated.tables.references.*

/** A class that acts like a registry*/
object Registry : GrpcApi() {

    val connection = DriverManager.getConnection("jdbc:sqlite:app/my_database.db")
    val dsl = DSL.using(connection)

    /** Print the lists for testing */
    fun viewAll() {
        println(DB.clientsIPsAndPorts)
        println(DB.serversIPsAndPorts)
    }

    /**
     * Determine the type of the ip (Client or Server) *
     *
     * @param ipInfo The ip of class IpInformation
     * @return [TargetType] The type of the ip of class TargetType
     */
    private fun determineTargetType(ipInfo: IpInformation): TargetType {
        val clientsIps = dsl.selectFrom(CLIENTS_IPS).fetch(CLIENTS_IPS.IP)
        val serversIps = dsl.selectFrom(SERVERS_IPS).fetch(SERVERS_IPS.IP)
        require(clientsIps.contains(ipInfo.ip) || serversIps.contains(ipInfo.ip))
        { "IP ${ipInfo.ip} not found" }

        return if (clientsIps.contains(ipInfo.ip)) TargetType(ClientTargetType.CLIENT)
        else TargetType(ClientTargetType.SERVER)
    }

    fun main() {
        println(determineTargetType(IpInformation("1", 0u)))
    }

    override fun secureIp(targetType: TargetType): IpInformation {
        return IpAllocator.assignIp(SEQUENTIAL, TargetTypeInfo(targetType))
    }

    override fun securePort(ipInfo: IpInformation): IpInformation {
        val targetType = determineTargetType(ipInfo)
        return PortAllocator.assignPort(ipInfo.ip, TargetTypeInfo(targetType))
    }

    override fun SecureIpAndPort(targetType: TargetType): IpInformation {
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
