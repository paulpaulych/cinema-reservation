package dgis.interview.cinema.room

import dgis.interview.cinema.AddOneRes
import dgis.interview.cinema.IdAccessRepo
import dgis.interview.cinema.db.DB
import dgis.interview.cinema.db.hasAny
import dgis.interview.cinema.db.queryList
import dgis.interview.cinema.reservation.ResourceLoader
import dgis.interview.cinema.transaction.Isolation
import org.springframework.stereotype.Repository

@Repository
class RoomRepo(
        private val db: DB
): IdAccessRepo<Room, Long> {

    fun add(id: Long, rowSizes: Map<Int, Int>): AddOneRes =
        db.inTransaction(isolation = Isolation.REPEATABLE_READ) {

            val exists = prepareStatement("select id from rooms where id = ?")
                .apply { setLong(1, id) }
                .hasAny()

            if (exists) return@inTransaction AddOneRes.AlreadyExists

            prepareStatement("insert into rooms(id) values (?)")
                .apply { setLong(1, id) }
                .execute()

            prepareStatement("insert into room_rows(room_id, row_num, seat_count) values (?, ?, ?)")
                .apply {
                    rowSizes.forEach { (rowNum, seatCount) ->
                        setLong(1, id)
                        setInt(2, rowNum)
                        setInt(3, seatCount)
                        addBatch()
                    }
                }.executeBatch()
            AddOneRes.Success
        }

    override fun findById(id: Long): Room? {
        val rowSizes =
            db.inTransaction(isolation = Isolation.READ_COMMITTED) {
                prepareStatement(ResourceLoader.asText("sql/room_by_id.sql"))
                    .apply { setLong(1, id) }
                    .queryList { rs, _ -> rs.getInt("row_num") to rs.getInt("seat_count") }
                    .toMap()
            }
        return if (rowSizes.isNotEmpty()) Room(rowSizes) else null
    }

}

