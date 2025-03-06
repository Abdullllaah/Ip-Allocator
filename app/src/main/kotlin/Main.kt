import jooq.generated.tables.references.*
import org.jooq.impl.DSL.*
import org.jooq.impl.DSL
import java.sql.DriverManager

fun main() {

//    Connect.dsl.insertInto(CLIENTS_IPS, CLIENTS_IPS.IP, CLIENTS_IPS.PORT)
//        .values("1", 0)
//        .execute()

//    Admin.allocateIps(Segment("2.2.2.0", "2.2.2.1"), ClientTargetType.SERVER)
//    Admin.allocateIps(Segment("1.1.1.10", "1.1.1.20"), ClientTargetType.CLIENT)
//    Registry.secureIp(TargetType(ClientTargetType.SERVER))
//    Registry.securePort(IpInformation("2.2.2.0", 0u))
//    Registry.securePort(IpInformation("1.1.1.10", 0u))
//    Registry.secureIp(TargetType(ClientTargetType.SERVER))
//    Registry.secureIp(TargetType(ClientTargetType.CLIENT))
//    Registry.FreePort(IpInformation("1.1.1.10", 49152u))
//    Registry.FreeIpAndAllPorts(IpInformation("2.2.2.0", 0u))
//    Admin.freeExpiredIps()

//    Connect.dsl
//        .insertInto(CONFIG, CONFIG.LIFE_TIME_SEC)
//        .values(2)
//        .execute()

//    println(Registry.secureIp(TargetType(ClientTargetType.CLIENT)))

//    Connect.dsl.insertInto(SERVICES, SERVICES.IP, SERVICES.PORT)
//        .values("20.20.200.10", 50_000)
//        .execute()

//    val result = Connect.dsl.select(max(SERVICES.PORT))
//        .from(SERVICES)
//        .where(SERVICES.IP.eq("20.20.200.10"))
//        .fetchOne()?.value1()


//    Connect.dsl.insertInto(SERVICES, SERVICES.IP, SERVICES.PORT)
//        .values("20.20.200.10", result?.plus(1))
//        .execute()

//    Registry.securePort(IpInformation("20.20.200.9", 0u))

//    Registry.secureIp(TargetType(ClientTargetType.CLIENT))
//    var result = Connect.dsl.selectFrom(SERVERS).fetch()
//    println(result)
//    var res = Connect.dsl.selectFrom(CLIENTS_IPS).fetch()
//    println(res)
//    println(Admin.freeExpiredIps())
//    result = Connect.dsl.selectFrom(CLIENTS_INFO).fetch()
//    println(result)
//    res = Connect.dsl.selectFrom(CLIENTS_IPS).fetch()
//    println(res)
//    Registry.securePort(IpInformation("2.2.2.0", 0u))
}

