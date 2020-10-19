package dgis.interview.cinema.reservation

import dgis.interview.cinema.room.Room
import dgis.interview.cinema.room.Seat

data class SeatStatus(
    val seat: Seat,
    val customerId: Long?,
)

/**
 * returns list of all room's [SeatStatus]es
 */
//TODO: Cannot put this fun into companion object of [SeatStatus] because of java.lang.NoSuchFieldError: Companion
fun getSeatStatuses(room: Room, reservations: Collection<Reservation>): List<SeatStatus> {
    val customerBySeat = reservations
        .flatMap { (customerId, seats) -> seats.map { it to customerId } }
        .toMap()
    return room.getAllSeats()
        .map { SeatStatus(it, customerBySeat[it]) }
        .toList()
}