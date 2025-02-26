//fun main() {
//    val registry = Registry
//
//    // Test: Secure IP for a client
//    println("### Securing IP for Client ###")
//    val clientIp1 = registry.secureIp(TargetType(ClientTargetType.CLIENT))
//    println("Secured Client IP: $clientIp1")
//
//    val clientIp2 = registry.secureIp(TargetType(ClientTargetType.CLIENT))
//    println("Secured Client IP: $clientIp2")
//
//    // Test: Secure IP for a server
//    println("\n### Securing IP for Server ###")
//    val serverIp1 = registry.secureIp(TargetType(ClientTargetType.SERVER))
//    println("Secured Server IP: $serverIp1")
//
//    // Test: Secure Port for a Server IP
//    println("\n### Securing 2 Ports sequentially for Server ###")
//    val securedPortServer1 = registry.securePort(serverIp1)
//    val securedPortServer2 = registry.securePort(serverIp1)
//    println("Secured Port for Server IP: $securedPortServer1, $securedPortServer2")
//
//    // Test: Secure IP and Port together
//    println("\n### Securing IP and Port Together ###")
//    val securedIpAndPort = registry.SecureIpAndPort(TargetType(ClientTargetType.SERVER))
//    println("Secured IP and Port: $securedIpAndPort")
//
//    // Test: Free Port
//    println("\n### Freeing a Port ###")
//    registry.FreePort(securedPortServer1)
//    println("Port 1 Freed for Server IP: ${securedPortServer1.ip}")
//    registry.viewAll() // View all to verify
//
//    // Test: Free IP and All Ports
//    println("\n### Freeing an IP and All Ports ###")
//    registry.FreeIpAndAllPorts(serverIp1)
//    println("Freed IP and All Ports for: ${serverIp1.ip}")
//    registry.viewAll() // View all to verify
//
//    // View Final State
//    println("\n### Final State of DB ###")
//    registry.viewAll()
//}
import jooq.generated.tables.references.*
import org.jooq.impl.DSL.*
import org.jooq.impl.DSL
import java.sql.DriverManager

fun main() {
    val connection = DriverManager.getConnection("jdbc:sqlite:app/my_database.db")
    val dsl = DSL.using(connection)

//    dsl.insertInto(CLIENTS_IPS, CLIENTS_IPS.IP, CLIENTS_IPS.PORT)
//        .values("1", 0)
//        .execute()

//    Admin.allocateIps(Segment("10.10.10.0", "10.10.10.10"), ClientTargetType.CLIENT)

//    println(Registry.secureIp(TargetType(ClientTargetType.SERVER)))

//    dsl.insertInto(SERVICES, SERVICES.IP, SERVICES.PORT)
//        .values("20.20.200.10", 50_000)
//        .execute()

//    val result = dsl.select(max(SERVICES.PORT))
//        .from(SERVICES)
//        .where(SERVICES.IP.eq("20.20.200.10"))
//        .fetchOne()?.value1()


//    dsl.insertInto(SERVICES, SERVICES.IP, SERVICES.PORT)
//        .values("20.20.200.10", result?.plus(1))
//        .execute()

//    Registry.securePort(IpInformation("20.20.200.9", 0u))

//    val result = dsl.selectFrom(SERVICES).fetch()
//    println(result)
}

