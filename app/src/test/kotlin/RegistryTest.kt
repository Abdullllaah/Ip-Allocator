import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RegistryTest {

  @BeforeEach
  fun setUp() {
    DB.clientsIPsAndPorts.clear()
    DB.serversIPsAndPorts.clear()
  }

  @Test
  fun secureIpForClient() {
    val expected = IpInformation("192.0.0.0", 0u)
    val actual = Registry.secureIp(TargetType(ClientTargetType.CLIENT))
    assertEquals(expected.toString(), actual.toString())
  }

  @Test
  fun secureIpForServer() {
    val expected = IpInformation("127.0.0.0", 0u)
    val actual = Registry.secureIp(TargetType(ClientTargetType.SERVER))
    assertEquals(expected.toString(), actual.toString())
  }

  @Test
  fun secureIpForClientTwice() {
    val expected = IpInformation("192.1.0.0", 0u)
    Registry.secureIp(TargetType(ClientTargetType.CLIENT))
    val actual = Registry.secureIp(TargetType(ClientTargetType.CLIENT))
    assertEquals(expected.toString(), actual.toString())
  }

  @Test
  fun secureIpForServerTwice() {
    val expected = IpInformation("127.0.1.0", 0u)
    Registry.secureIp(TargetType(ClientTargetType.SERVER))
    val actual = Registry.secureIp(TargetType(ClientTargetType.SERVER))
    assertEquals(expected.toString(), actual.toString())
  }

  @Test
  fun securePortForClient() {
    val expected = IpInformation("192.0.0.0", 49_152u)
    val ip = Registry.secureIp(TargetType(ClientTargetType.CLIENT))
    val actual = Registry.securePort(ip)
    assertEquals(expected.toString(), actual.toString())
  }

  @Test
  fun securePortForServer() {
    val expected = IpInformation("127.0.0.0", 49_152u)
    val actual = Registry.securePort(Registry.secureIp(TargetType(ClientTargetType.SERVER)))
    assertEquals(expected.toString(), actual.toString())
  }

  @Test
  fun securePortForClientTwice() {
    val ip = Registry.secureIp(TargetType(ClientTargetType.CLIENT))
    Registry.securePort(ip)
    assertThrows<IllegalArgumentException> { Registry.securePort(ip) }
  }

  @Test
  fun securePortForServerTwice() {
    val ip = Registry.secureIp(TargetType(ClientTargetType.SERVER))
    Registry.securePort(ip)
    val actual = Registry.securePort(ip)
    val expected = IpInformation("127.0.0.0", 49_153u)
    assertEquals(expected.toString(), actual.toString())
  }

  @Test
  fun secureIpAndPortForClient() {
    val expected = IpInformation("192.0.0.0", 49_152u)
    val actual = Registry.SecureIpAndPort(TargetType(ClientTargetType.CLIENT))
    assertEquals(expected.toString(), actual.toString())
  }

  @Test
  fun secureIpAndPortForServer() {
    val expected = IpInformation("127.0.0.0", 49_152u)
    val actual = Registry.SecureIpAndPort(TargetType(ClientTargetType.SERVER))
    assertEquals(expected.toString(), actual.toString())
  }

  @Test
  fun freePortForClient() {
    val ipInfo = Registry.SecureIpAndPort(TargetType(ClientTargetType.CLIENT))
    Registry.FreePort(ipInfo)
    assert(DB.clientsIPsAndPorts[ipInfo.ip]!!.ports.isEmpty())
  }

  @Test
  fun freePortForServer() {
    val ipInfo = Registry.SecureIpAndPort(TargetType(ClientTargetType.SERVER))
    Registry.FreePort(ipInfo)
    assert(DB.serversIPsAndPorts[ipInfo.ip]!!.ports.isEmpty())
  }

  @Test
  fun freePortThatDoesntExist() {
    val ipInfo = Registry.secureIp(TargetType(ClientTargetType.CLIENT))
    assertDoesNotThrow { Registry.FreePort(ipInfo) }
  }

  @Test
  fun freeIpAndAllPortsForClient() {
    val ipInfo = Registry.SecureIpAndPort(TargetType(ClientTargetType.CLIENT))
    Registry.FreeIpAndAllPorts(ipInfo)
    assertFalse(DB.clientsIPsAndPorts.containsKey(ipInfo.ip))
  }

  @Test
  fun freeIpAndAllPortsForServer() {
    val ipInfo = Registry.SecureIpAndPort(TargetType(ClientTargetType.SERVER))
    Registry.FreeIpAndAllPorts(ipInfo)
    assertFalse(DB.serversIPsAndPorts.containsKey(ipInfo.ip))
  }
}
