import io.github.oshai.kotlinlogging.KotlinLogging
import jooq.generated.tables.records.ClientsIpsRecord
import jooq.generated.tables.references.*
import org.jooq.Record
import org.jooq.Record2
import org.jooq.impl.DSL
import java.sql.DriverManager

/** An object that handles the database through the admin registry*/
object Admin {
    private val logger = KotlinLogging.logger {}

    val connection = DriverManager.getConnection("jdbc:sqlite:app/my_database.db")
    val dsl = DSL.using(connection)

    fun allocateIps(segment: Segment, type: ClientTargetType) {
        for (i in segment.fromFields[0]..segment.toFields[0]) {
            for (j in segment.fromFields[1]..segment.toFields[1]) {
                for (k in segment.fromFields[2]..segment.toFields[2]) {
                    for (m in segment.fromFields[3]..segment.toFields[3]) {
                        val ip = "$i.$j.$k.$m"
                        when (type) {
                            ClientTargetType.SERVER ->
                                dsl.insertInto(SERVERS_IPS, SERVERS_IPS.IP)
                                    .values(ip)
                                    .execute()
                            ClientTargetType.CLIENT ->
                                dsl.insertInto(CLIENTS_IPS, CLIENTS_IPS.IP)
                                    .values(ip)
                                    .execute()
                        }
                    }
                }
            }
        }
    }
}