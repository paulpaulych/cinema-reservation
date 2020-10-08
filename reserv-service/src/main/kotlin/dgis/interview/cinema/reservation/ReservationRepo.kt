package dgis.interview.cinema.reservation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dgis.interview.cinema.customer.Customer
import dgis.interview.cinema.db.prepareInsertStatement
import dgis.interview.cinema.db.queryList
import dgis.interview.cinema.room.Seat
import org.springframework.stereotype.Repository
import java.io.InputStream
import java.io.InputStreamReader
import javax.sql.DataSource

@Repository
class ReservationRepo(
    private val ds: DataSource
) {

    private val json = jacksonObjectMapper()

    fun findBySession(sessionId: Long): Collection<Reservation> =
        ds.connection.use { conn ->
            val sql = ResourceLoader.asText("sql/reservations_by_session.sql")
            conn.prepareStatement(sql)
                .apply { setLong(1, sessionId) }
                .queryList { rs, _ ->
                    val seats = json.readValue<Map<Int, Int>>(rs.getString("seat"))
                        .map { (k, v) -> Seat(k, v) }
                    Reservation(
                            customer = Customer(rs.getLong("customer_id")),
                            seats = seats
                    )
                }
        }


    //TODO: обернуть в транзакцию
    fun add(sessionId: Long, customerId: Long, seats: Collection<Seat>){
        ds.connection.use { conn ->
            val generatedId =
                conn.prepareInsertStatement("insert into reservations(session_id, customer_id) values(?, ?)")
                .apply {
                    setLong(1, sessionId)
                    setLong(2, customerId)
                    executeUpdate()
                }.generatedKeys.use {
                    check(it.next()){ "no any generated key in resultSet" }
                    it.getLong(1)
                }

            conn.prepareStatement(
                "insert into reservation_seats(reservation_id, row_num, seat_num) values (?, ?, ?)"
            ).apply {
                seats.forEach {
                    setLong(1, generatedId)
                    setInt(2, it.rowNum)
                    setInt(3, it.seatNum)
                    addBatch()
                }
            }.executeBatch()
        }
    }
}

class ResourceLoader{

    companion object {

        private fun asStream(name: String): InputStream =
            this::class.java.classLoader.getResourceAsStream(name)
                ?: error("cannot find resource $name")

        fun asText(name: String): String =
            InputStreamReader(asStream(name)).readText()

    }

}