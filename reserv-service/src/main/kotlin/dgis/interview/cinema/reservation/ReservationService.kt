package dgis.interview.cinema.reservation

import dgis.interview.cinema.customer.CustomerRepo
import dgis.interview.cinema.room.SeatPresenceRes
import dgis.interview.cinema.room.Seat
import dgis.interview.cinema.session.SessionRepo
import org.springframework.stereotype.Service

@Service
class ReservationService(
    private val reservationRepo: ReservationRepo,
    private val customerRepo: CustomerRepo,
    private val sessionRepo: SessionRepo
) {

    //TODO: обернуть в транзакцию
    fun reserveSeats(sessionId: Long, customerId: Long, seats: List<Seat>): ReservationRes {
        customerRepo.findById(customerId)
            ?: return ReservationRes.CustomerNotFound(customerId)
        val session = sessionRepo.findById(sessionId)
            ?: return ReservationRes.SessionNotFound(sessionId)

        (session.room.hasSeats(seats) as? SeatPresenceRes.Missed)
            ?. let { return ReservationRes.SeatsMissing(it.seats) }

        val reserved = reservationRepo.findBySession(session.id)
            .flatMap { it.seats }
            .toSet()

        val (collided, free) = seats.partition { reserved.contains(it) }

        if(collided.isNotEmpty()){
            return ReservationRes.AlreadyReserved(collided)
        }

        reservationRepo.add(sessionId, customerId, free)

        return ReservationRes.Success
    }

    //TODO: обернуть в транзакцию
    fun getReservationStatus(sessionId: Long): ReservationStatusRes {
        val session = sessionRepo.findById(sessionId)
            ?: return ReservationStatusRes.SessionNotFound(sessionId)
        val reserved = reservationRepo.findBySession(sessionId).flatMap { it.seats }.toSet()
        val seatStatuses = session.room.getAllSeats()
            .map { SeatStatus(it, !reserved.contains(it)) }
            .toList()
        return ReservationStatusRes.Success(seatStatuses)
    }

}

sealed class ReservationStatusRes {
    data class SessionNotFound(val sessionId: Long)
        : ReservationStatusRes()
    data class Success(val seatStatuses: List<SeatStatus>)
        : ReservationStatusRes()
}

data class SeatStatus(
    val seat: Seat,
    val free: Boolean
)

sealed class ReservationRes {
    object Success: ReservationRes()
    data class SessionNotFound(val sessionId: Long): ReservationRes()
    data class AlreadyReserved(val reservedSeats: List<Seat>): ReservationRes()
    data class CustomerNotFound(val customerId: Long): ReservationRes()
    //TODO: missing или absent
    data class SeatsMissing(val seats: List<Seat>): ReservationRes()
}
