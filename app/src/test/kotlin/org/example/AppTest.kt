package org.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder

@TestMethodOrder(OrderAnnotation::class)
class RegistryTest {

    @Test
    fun secureIpTest() {
        println("Secure IP Test")
        val expected = IpInformation("192.1.134.161", 0u)
        val actual = Registry.secureIp(TargetType(ClientTargetType.CLIENT))
        assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun securePortTest() {
        println("Secure Port Test")
        val expected = IpInformation("127.0.0.1", 1u)
        val actual = Registry.securePort(Registry.secureIp(TargetType(ClientTargetType.SERVER)))
        assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun secureIpAndPortTest() {
        println("Secure IP and Port Test")
        val expected = IpInformation("127.0.0.2", 1u)
        val actual = Registry.SecureIpAndPort(TargetType(ClientTargetType.SERVER))
        assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun freePortTest() {
        println("Free IP Test")
        val ipInfo = Registry.SecureIpAndPort(TargetType(ClientTargetType.SERVER))
        Registry.FreePort(ipInfo)
        val expected = hashMapOf<UInt, UInt>(1u to 0u)
        val actual = DB.serversIPsAndPorts[ipInfo.ip]
        assertEquals(expected, actual)
    }

    @Test
    fun freeIpAndAllPorts() {
        println("Free IP and All Ports")
        val ipInfo = Registry.SecureIpAndPort(TargetType(ClientTargetType.SERVER))
        Registry.FreeIpAndAllPorts(ipInfo)
        assertNull(DB.ipsAssignedDate[ipInfo.ip])
    }
}