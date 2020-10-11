package dgis.interview.cinema.reservation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dgis.interview.cinema.LoggerProperty
import dgis.interview.cinema.ResourceLoader
import dgis.interview.cinema.db.transaction.DB
import dgis.interview.cinema.db.prepareInsertStatement
import dgis.interview.cinema.db.queryList
import dgis.interview.cinema.room.Seat
import dgis.interview.cinema.db.transaction.Isolation
import org.springframework.stereotype.Repository

@Repository
class ReservationRepo(
    private val db: DB
) {

    private val log by LoggerProperty()
    private val json = jacksonObjectMapper()

    fun findBySession(sessionId: Long): Collection<Reservation> =
        db.inTransaction(Isolation.READ_COMMITTED) {
            val sql = ResourceLoader.asText("sql/reservations_by_session.sql")
            prepareStatement(sql)
                .apply { setLong(1, sessionId) }
                .queryList { rs, _ ->
                    val seats = json.readValue<Map<Int, Int>>(rs.getString("seats"))
                        .map { (k, v) -> Seat(k, v) }
                    Reservation(
                        customerId = rs.getLong("customer_id"),
                        seats = seats
                    )
                }
        }


    fun add(sessionId: Long, customerId: Long, seats: Collection<Seat>){
        log.debug("saving reservation: sessionId: {}, customerId: {}, seats: {}",
            sessionId, customerId, seats)

        db.inTransaction(Isolation.READ_COMMITTED) {
            val generatedId =
                prepareInsertStatement("insert into reservations(session_id, customer_id) values(?, ?)")
                .apply {
                    setLong(1, sessionId)
                    setLong(2, customerId)
                    executeUpdate()
                }.generatedKeys.use {
                    check(it.next()){ "no any generated key in result set" }
                    it.getLong(1)
                }

            prepareStatement(
                "insert into reservation_seats(reservation_id, row_num, seat_num) values (?, ?, ?)"
            ).apply {
                seats.forEach {
                    log.trace("setting params: $generatedId, ${it.rowNum}, ${it.seatNum}")
                    setLong(1, generatedId)
                    setInt(2, it.rowNum)
                    setInt(3, it.seatNum)
                    addBatch()
                }
            }.executeBatch()
        }
    }
}
