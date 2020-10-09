package dgis.interview.cinema.reservation

import dgis.interview.cinema.room.Seat
import dgis.interview.cinema.webcommon.HTTP
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private enum class ErrorCode {
    ALREADY_RESERVED,
    CUSTOMER_NOT_FOUND,
    SEATS_MISSING,
    SESSION_NOT_FOUND
}

@RestController
@RequestMapping("/reservation")
class ReservationEndpoint(
    private val reservationService: ReservationService
) {

    @PutMapping
    fun createReservations(
        @RequestParam("sessionId") sessionId: Long,
        @RequestParam("customerId") customerId: Long,
        @RequestBody seats: List<Seat>
    ): ResponseEntity<*> =
        when(val res = reservationService.reserveSeats(sessionId, customerId, seats)){
            is ReservationRes.SessionNotFound -> HTTP.conflict(code = ErrorCode.SESSION_NOT_FOUND.name)
            is ReservationRes.AlreadyReserved -> HTTP.conflict(
                    code = ErrorCode.ALREADY_RESERVED.name,
                    payload = res.reservedSeats)
            is ReservationRes.SeatsMissing -> HTTP.conflict(
                    code = ErrorCode.SEATS_MISSING.name,
                    payload = res.seats)
            is ReservationRes.Success -> HTTP.ok()
        }

    @GetMapping
    fun getReservations(@RequestParam("sessionId") sessionId: Long): ResponseEntity<*> =
        when(val res = reservationService.getReservationStatus(sessionId)){
            is ReservationStatusRes.SessionNotFound -> HTTP.conflict(code = ErrorCode.SESSION_NOT_FOUND.name)
            is ReservationStatusRes.Success -> HTTP.ok(res.seatStatuses)
        }
}
