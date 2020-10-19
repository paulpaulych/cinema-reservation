package dgis.interview.cinema.room

import dgis.interview.cinema.db.AddOneRes
import dgis.interview.cinema.db.IdAccessRepo
import dgis.interview.cinema.db.transaction.DB
import dgis.interview.cinema.db.hasAny
import dgis.interview.cinema.db.queryList
import dgis.interview.cinema.ResourceLoader
import dgis.interview.cinema.db.transaction.Isolation
import org.springframework.stereotype.Repository

@Repository
class RoomRepo(
        private val db: DB
): IdAccessRepo<Room, Long> {

    fun add(id: Long, rowSizes: Map<Int, Int>): AddOneRes =
        db.inTransaction(Isolation.REPEATABLE_READ) {

            if (existsById(id)) return@inTransaction AddOneRes.AlreadyExists

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
        val rowSizes = db.inTransaction(Isolation.READ_COMMITTED) {
            prepareStatement(ResourceLoader.asText("sql/room_by_id.sql"))
                .apply { setLong(1, id) }
                .queryList { rs, _ -> rs.getInt("row_num") to rs.getInt("seat_count") }
                .toMap()
        }
        return if (rowSizes.isNotEmpty()) Room(rowSizes) else null
    }

    fun existsById(id: Long): Boolean =
        db.inTransaction(Isolation.READ_COMMITTED) {
            prepareStatement("select id from rooms where id = ?")
                .apply { setLong(1, id) }
                .executeQuery()
                .hasAny()
        }



}

