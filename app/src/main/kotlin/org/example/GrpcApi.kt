abstract class GrpcApi(){
    /*Secure random ip for the caller (governed by the type of the target) and return the secured ip information*/
    abstract fun secureIp(targetType: TargetType): IpInformation
    /*Secure a random port for the given ip*/
    abstract fun securePort(ipInfo: IpInformation): IpInformation
    /*Secure random ip and port for the caller (governed by the type of the target) and return the secured ip and port information*/
    abstract fun SecureIpAndPort(targetType: TargetType): IpInformation
    /*Free the given port from the given ip*/
    abstract fun FreePort(ipInfo: IpInformation): Unit
    /*Free the given ip and all ports associated with it*/
    abstract fun FreeIpAndAllPorts(ipInfo: IpInformation): Unit
    /*Sync the currently in use ips and ports from the caller to the registry*/
    abstract fun SyncSecuredIpsAndPorts(listOfIpInfo: ListOfIpInformation): Unit
    /*Gets the domain name(endpoint) for the servers services(should then be used with DNS to get the actual ip)*/
    abstract fun GetDomainName(): DomainNameInformation
    /*Gets the allowed ips for clients*/
    abstract fun GetAllowedIPsForClients(): ListOfAllowedIPs
}
