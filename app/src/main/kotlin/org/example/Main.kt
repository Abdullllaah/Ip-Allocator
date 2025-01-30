fun main() {
    val registry = Registry

    // Test: Secure IP for a client
    println("### Securing IP for Client ###")
    val clientIp1 = registry.secureIp(TargetType(ClientTargetType.CLIENT))
    println("Secured Client IP: $clientIp1")

    val clientIp2 = registry.secureIp(TargetType(ClientTargetType.CLIENT))
    println("Secured Client IP: $clientIp2")

    // Test: Secure IP for a server
    println("\n### Securing IP for Server ###")
    val serverIp1 = registry.secureIp(TargetType(ClientTargetType.SERVER))
    println("Secured Server IP: $serverIp1")

    // Test: Secure Port for a Server IP
    println("\n### Securing 2 Ports sequentially for Server ###")
    val securedPortServer1 = registry.securePort(serverIp1)
    val securedPortServer2 = registry.securePort(serverIp1)
    println("Secured Port for Server IP: $securedPortServer1, $securedPortServer2")

    // Test: Secure IP and Port together
    println("\n### Securing IP and Port Together ###")
    val securedIpAndPort = registry.SecureIpAndPort(TargetType(ClientTargetType.SERVER))
    println("Secured IP and Port: $securedIpAndPort")

    // Test: Free Port
    println("\n### Freeing a Port ###")
    registry.FreePort(securedPortServer1)
    println("Port 1 Freed for Server IP: ${securedPortServer1.ip}")
    registry.viewAll() // View all to verify

    // Test: Free IP and All Ports
    println("\n### Freeing an IP and All Ports ###")
    registry.FreeIpAndAllPorts(serverIp1)
    println("Freed IP and All Ports for: ${serverIp1.ip}")
    registry.viewAll() // View all to verify


    // View Final State
    println("\n### Final State of DB ###")
    registry.viewAll()
}