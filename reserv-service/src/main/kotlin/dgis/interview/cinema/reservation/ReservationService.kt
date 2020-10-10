package dgis.interview.cinema.reservation

import dgis.interview.cinema.LoggerProperty
import dgis.interview.cinema.db.DB
import dgis.interview.cinema.room.SeatPresenceRes
import dgis.interview.cinema.room.Seat
import dgis.interview.cinema.session.SessionRepo
import dgis.interview.cinema.transaction.Isolation
import org.springframework.stereotype.Service

@Service
class ReservationService(
    private val reservationRepo: ReservationRepo,
    private val sessionRepo: SessionRepo,
    private val db: DB
) {

    private val log by LoggerProperty()

    fun reserveSeats(sessionId: Long, acquired: Reservation): ReservationRes {
        /*
        не включаем это в транзакцию с учетом того что сеанс и кинозал
        не могут быть удалены или изменены
        */
        val session = sessionRepo.findById(sessionId)
            ?: return ReservationRes.SessionNotFound
        (session.room.hasSeats(acquired.seats) as? SeatPresenceRes.Missed)
            ?.let { return ReservationRes.SeatsMissing(it.seats) }

        return db.inTransaction(
            isolation = Isolation.REPEATABLE_READ) {

            val reserved = reservationRepo.findBySession(session.id)
                .flatMap { it.seats }
                .toSet()
            log.debug("fetched reserved seats by session(id = {}): {}", sessionId, reserved)
            val (collided, free) = acquired.seats.partition { reserved.contains(it) }
            if (collided.isNotEmpty()) {
                return@inTransaction ReservationRes.AlreadyReserved(collided)
            }
            reservationRepo.add(sessionId, acquired.customerId, free)
            ReservationRes.Success
        }
    }

    fun getReservationStatus(sessionId: Long): ReservationStatusRes {
        val session = sessionRepo.findById(sessionId)
            ?: return ReservationStatusRes.SessionNotFound
        val customerBySeat = reservationRepo.findBySession(sessionId)
            .flatMap { (customerId, seats) -> seats.map { it to customerId } }
            .toMap()
        val seatStatuses = session.room.getAllSeats()
            .map { SeatStatus(it, customerBySeat[it]) }
            .toList()
        return ReservationStatusRes.Success(seatStatuses)
    }
}



sealed class ReservationStatusRes {
    object SessionNotFound: ReservationStatusRes()
    data class Success(val seatStatuses: List<SeatStatus>)
        : ReservationStatusRes()
}

data class SeatStatus(
    val seat: Seat,
    val customerId: Long?,
)

sealed class ReservationRes {
    object Success: ReservationRes()
    object SessionNotFound: ReservationRes()
    data class AlreadyReserved(val reservedSeats: List<Seat>): ReservationRes()
    data class SeatsMissing(val seats: List<Seat>): ReservationRes()
}
