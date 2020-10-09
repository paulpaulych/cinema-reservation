package dgis.interview.cinema.session

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dgis.interview.cinema.AddOneRes
import dgis.interview.cinema.IdAccessRepo
import dgis.interview.cinema.db.DB
import dgis.interview.cinema.db.hasAny
import dgis.interview.cinema.db.queryOne
import dgis.interview.cinema.reservation.ResourceLoader
import dgis.interview.cinema.room.Room
import dgis.interview.cinema.transaction.Isolation
import dgis.interview.cinema.transaction.Propagation
import org.springframework.stereotype.Repository

@Repository
class SessionRepo(
    private val db: DB
): IdAccessRepo<Session, Long> {

    private val json = jacksonObjectMapper()

    override fun findById(id: Long) =
        db.inTransaction(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.USE_EXISTING
        ) {
            prepareStatement(ResourceLoader.asText("sql/session_by_id.sql"))
                .apply { setLong(1, id) }
                .queryOne { rs ->
                    Session(
                        id = rs.getLong("id"),
                        room = Room(json.readValue(rs.getString("rows")))
                    )
                }
        }

    fun add(sessionId: Long, roomId: Long): AddOneRes =
        db.inTransaction(isolation = Isolation.REPEATABLE_READ) {
            val exists = prepareStatement("select id from sessions where id = ?")
                .apply { setLong(1, sessionId) }
                .hasAny()

            if (exists) return@inTransaction AddOneRes.AlreadyExists

            prepareStatement("insert into sessions(id, room_id) values (?, ?)")
                .apply {
                    setLong(1, sessionId)
                    setLong(2, roomId)
                }.execute()
            AddOneRes.Success
        }

}

