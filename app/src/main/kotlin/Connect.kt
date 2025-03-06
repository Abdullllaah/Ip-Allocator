import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.SQLDialect
import org.jooq.impl.DSL

object Connect {
    private val hikariConfig = HikariConfig().apply {
        jdbcUrl = "jdbc:sqlite:app/my_database.db"
        username = "username"
        password = "password"
        maximumPoolSize = 10
    }

    private val dataSource = HikariDataSource(hikariConfig)
    val dsl = DSL.using(dataSource, SQLDialect.SQLITE)
}