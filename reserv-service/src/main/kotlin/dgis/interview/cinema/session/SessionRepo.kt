package dgis.interview.cinema.session

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dgis.interview.cinema.AddOneRes
import dgis.interview.cinema.IdAccessRepo
import dgis.interview.cinema.db.hasAny
import dgis.interview.cinema.db.queryOne
import dgis.interview.cinema.reservation.ResourceLoader
import dgis.interview.cinema.room.Room
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class SessionRepo(
    private val ds: DataSource
): IdAccessRepo<Session, Long> {

    private val json = jacksonObjectMapper()

    override fun findById(id: Long): Session? {
        return ds.connection.use {  conn ->
            conn.prepareStatement(ResourceLoader.asText("sql/session_by_id.sql"))
                .apply { setLong(1, id) }
                .queryOne { rs ->
                    Session(
                        id = rs.getLong("id"),
                        room = Room(json.readValue(rs.getString("rows")))
                    )
                }
        }
    }

    //TODO: транзакция
    fun add(sessionId: Long, roomId: Long): AddOneRes {
        ds.connection.use { conn ->
            val exists = conn.prepareStatement("select id from sessions where id = ?")
                .apply { setLong(1, sessionId) }
                .hasAny()

            if (exists) return AddOneRes.AlreadyExists

            conn.prepareStatement("insert into sessions(id, room_id) values (?, ?)")
                .apply {
                    setLong(1, sessionId)
                    setLong(2, roomId)
                }.execute()
            return AddOneRes.Success
        }
    }
}

