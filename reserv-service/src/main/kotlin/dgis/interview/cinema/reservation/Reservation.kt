package dgis.interview.cinema.reservation

import dgis.interview.cinema.customer.Customer
import dgis.interview.cinema.room.Seat
import dgis.interview.cinema.session.Session

data class Reservation(
    val id: Long?,
    val session: Session,
    val customer: Customer,
    val seats: List<Seat>
)