package dgis.interview.cinema.room


data class Room (
    val rowSizes: Map<Int, Int>
){

    fun hasSeats(seats: Collection<Seat>): SeatPresenceRes =
        seats.filter {
            !rowSizes.containsKey(it.rowNum)
                    || rowSizes[it.rowNum]!! < it.seatNum
                    || it.seatNum < 1
        }.takeIf { it.isNotEmpty() }
            ?.let { SeatPresenceRes.Missed(it) }
            ?: SeatPresenceRes.AllPresent

    /**
     * Generates sequence of all seats present in this room
     */
    fun getAllSeats(): Sequence<Seat> =
        rowSizes.map { (rowNum, seatCount) ->
            generateSequence(Seat(rowNum, 1)){ seat ->
                (seat.seatNum + 1).takeIf { it <= seatCount}
                    ?.let{seat.copy(seatNum = it)}
            }
        }.reduce(Sequence<Seat>::plus)
}

data class Seat (
    val rowNum: Int,
    val seatNum: Int
)

sealed class SeatPresenceRes{
    data class Missed(val seats: List<Seat>): SeatPresenceRes()
    object AllPresent: SeatPresenceRes()
}