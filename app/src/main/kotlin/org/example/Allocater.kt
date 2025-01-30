import kotlin.random.Random
import Allocater.IpAllocationType.SEQUENTIAL
import Allocater.IpAllocationType.RANDOM

object Allocater {
    enum class IpAllocationType {
        SEQUENTIAL,
        RANDOM
    }

    private val reservedPorts = listOf<Int>(
        1,
        5,
        7,
        9,
        11,
        13,
        17,
        19,
        20,
        21,
        22,
        23,
        25,
        37,
        42,
        43,
        49,
        53,
        67,
        68,
        69,
        70,
        79,
        80,
        88,
        101,
        102,
        107,
        109,
        110,
        111,
        113,
        119,
        123,
        135,
        137,
        138,
        139,
        143,
        161,
        162,
        177,
        179,
        194,
        201,
        202,
        204,
        206,
        209,
        213,
        220,
        389,
        443,
        444,
        445,
        464,
        465,
        500,
        512,
        513,
        514,
        515,
        520,
        525,
        530,
        531,
        532,
        540,
        543,
        544,
        546,
        547,
        548,
        554,
        563,
        587,
        601,
        636,
        993,
        995,
        1080,
        1024,
        1025,
        1080,
        1433,
        1521,
        1723,
        1883,
        2375,
        2376,
        2483,
        2484,
        2948,
        2949,
        3306,
        3389,
        4333,
        4664,
        5432,
        5800,
        5900,
        5984,
        6379,
        6443,
        6665,
        6666,
        6667,
        7001,
        8000,
        8080,
        8081,
        8443,
        8888,
        9200,
        10000,
        11211,
        27017,
        27018,
        50000
    )

    private val numberOfRandomIterations = 10 // How many times to iterate when assigning an ip randomly

    /**
     * Assign Ip based on the user type and allocation type
     * @param allocationType Either random or sequential
     * @param targetTypeInfo An object that contains list and segment based on the user type
     * @return [IpInformation] Including the secured ip and the port will be 0 */
    fun assignIp(
        allocationType: IpAllocationType,
        targetTypeInfo: TargetTypeInfo
    ): IpInformation {
        return when (allocationType) {
            SEQUENTIAL -> assignSequentialIp(targetTypeInfo)
            RANDOM -> assignRandomIp(targetTypeInfo)
        }
    }

    /**
     * Loop through each filed in the segment and check if it exists in the secured ips
     * @param targetTypeInfo An object that contains list and segment based on the user type
     * @return [IpInformation] Including the secured ip and the port will be 0 */
    private fun assignSequentialIp(targetTypeInfo: TargetTypeInfo): IpInformation {
        val segment = targetTypeInfo.segment
        for (i in segment.firstFieldFrom..segment.firstFieldTo) {
            for (j in segment.secondFieldFrom..segment.secondFieldTo) {
                for (k in segment.thirdFieldFrom..segment.thirdFieldTo) {
                    for (m in segment.fourthFieldFrom..segment.fourthFieldTo) {
                        val ip = "$i.$j.$k.$m"
                        if (!targetTypeInfo.list.containsKey(ip)) {
                            targetTypeInfo.list[ip] = User(ip)
                            return IpInformation(ip, 0u)
                        }
                    }
                }
            }
        }
        throw IllegalArgumentException("There is no remaining IP address")
    }
    /**
     * Assign a random number for each field in the segment and try [numberOfRandomIterations] times
     * @param targetTypeInfo An object that contains list and segment based on the user type
     * @return [IpInformation] Including the secured ip and the port will be 0 */
    private fun assignRandomIp(targetTypeInfo: TargetTypeInfo): IpInformation {
        val segment = targetTypeInfo.segment
        for (n in 1..numberOfRandomIterations) {
            val first = Random.nextInt(segment.firstFieldFrom, segment.firstFieldTo + 1)
            val second = Random.nextInt(segment.secondFieldFrom, segment.secondFieldTo + 1)
            val third = Random.nextInt(segment.thirdFieldFrom, segment.thirdFieldTo + 1)
            val fourth = Random.nextInt(segment.fourthFieldFrom, segment.fourthFieldTo + 1)
            val ip = "$first.$second.$third.$fourth"
            if (!targetTypeInfo.list.containsKey(ip)) {
                targetTypeInfo.list[ip] = User(ip)
                return IpInformation(ip, 0u)
            }
        }
        throw IllegalArgumentException("Could not assign random IP address")
    }

    /**
     * Loop through ports starting form 0 to the maximum port (65_535)
     * @param ip The user ip to assign a port for
     * @param targetTypeInfo An object that contains list and segment based on the user type
     * @return [IpInformation] Including the secured ip and the secured port */
    fun assignPort(ip: String, targetTypeInfo: TargetTypeInfo): IpInformation {
        val user = targetTypeInfo.list[ip]
        for (i in 1u..65_535u) // number of allowed ports
            if (!user!!.ports.contains(i) && !reservedPorts.contains(i.toInt())) {
                user.ports.add(i)
                return IpInformation(ip, i)
            }
        throw IllegalArgumentException("There is no available port")
    }
}