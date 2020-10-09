package dgis.interview.cinema.room

import dgis.interview.cinema.AddOneRes
import dgis.interview.cinema.IdAccessRepo
import dgis.interview.cinema.db.hasAny
import dgis.interview.cinema.db.queryList
import dgis.interview.cinema.reservation.ResourceLoader
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class RoomRepo(
        private val ds: DataSource
): IdAccessRepo<Room, Long> {

    //TODO: навернуть транзакцию
    fun add(id: Long, rowSizes: Map<Int, Int>): AddOneRes =
        ds.connection.use { conn ->
            val exists = conn.prepareStatement("select id from rooms where id = ?")
                .apply { setLong(1, id) }
                .hasAny()

            if (exists) return AddOneRes.AlreadyExists

            conn.prepareStatement("insert into rooms(id) values (?)")
                .apply { setLong(1, id) }
                .execute()
            conn.prepareStatement("insert into room_rows(room_id, row_num, seat_count) values (?, ?, ?)")
                .apply {
                    rowSizes.forEach { (rowNum, seatCount) ->
                        setLong(1, id)
                        setInt(2, rowNum)
                        setInt(3, seatCount)
                        addBatch()
                    }
                }.executeBatch()
            return AddOneRes.Success
        }


    override fun findById(id: Long): Room? {
        val rowSizes = ds.connection.use { conn ->
            conn.prepareStatement(ResourceLoader.asText("sql/room_by_id.sql"))
                    .apply { setLong(1, id) }
                    .queryList { rs, _ -> rs.getInt("row_num") to rs.getInt("seat_count") }
                    .toMap()
        }
        return if (rowSizes.isEmpty()) null else Room(rowSizes)
    }

}

