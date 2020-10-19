package dgis.interview.cinema.reservation

import dgis.interview.cinema.room.Seat

data class Reservation(
        val customerId: Long,
        val seats: List<Seat>
)