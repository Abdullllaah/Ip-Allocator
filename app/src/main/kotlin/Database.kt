import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object Database {
  private const val DB_URL = "jdbc:sqlite:my_database.db"

  fun connect(): Connection? {
    return try {
      val connection = DriverManager.getConnection(DB_URL)
      println("Connected to SQLite database!")
      connection
    } catch (e: SQLException) {
      println("Connection failed: ${e.message}")
      null
    }
  }
}

fun main() {
  Database.connect()
}
