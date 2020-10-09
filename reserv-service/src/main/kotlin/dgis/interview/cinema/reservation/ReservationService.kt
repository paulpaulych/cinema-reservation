package dgis.interview.cinema.reservation

import dgis.interview.cinema.LoggerProperty
import dgis.interview.cinema.room.SeatPresenceRes
import dgis.interview.cinema.room.Seat
import dgis.interview.cinema.session.SessionRepo
import org.springframework.stereotype.Service

@Service
class ReservationService(
    private val reservationRepo: ReservationRepo,
    private val sessionRepo: SessionRepo
) {

    private val log by LoggerProperty()
    //TODO: обернуть в транзакцию
    fun reserveSeats(sessionId: Long, customerId: Long, seats: List<Seat>): ReservationRes {
        val session = sessionRepo.findById(sessionId)
            ?: return ReservationRes.SessionNotFound

        (session.room.hasSeats(seats) as? SeatPresenceRes.Missed)
            ?. let { return ReservationRes.SeatsMissing(it.seats) }

        val reserved = reservationRepo.findBySession(session.id)
            .flatMap { it.seats }
            .toSet()
        log.debug("fetched reserved seats by session(id = {}): {}", sessionId, reserved)

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
    object SessionNotFound: ReservationRes()
    data class AlreadyReserved(val reservedSeats: List<Seat>): ReservationRes()
    //TODO: missing или absent
    data class SeatsMissing(val seats: List<Seat>): ReservationRes()
}
