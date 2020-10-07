package dgis.interview.cinema.room


data class Room (
    val id: Long?
){
    fun hasSits(seats: Collection<Seat>): SeatPresenceRes {
        TODO("not implemented yet")
    }

    /**
     * Generates sequence of all seats present in this room
     */
    fun getAllSeats(): Sequence<Seat>{
        TODO("not implemented yet")
    }
}

data class Seat (
    val row: Int,
    val col: Int
)

data class SeatMissionError(
    val seat: Seat,
    val message: String
): SeatPresenceRes()

sealed class SeatPresenceRes{
    data class SeatsAbsent(val errors: List<SeatMissionError>): SeatPresenceRes()
    object AllPresent: SeatPresenceRes()
}