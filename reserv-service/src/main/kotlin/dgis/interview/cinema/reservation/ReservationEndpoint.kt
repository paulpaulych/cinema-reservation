package dgis.interview.cinema.reservation

import dgis.interview.cinema.webcommon.ErrorCode
import dgis.interview.cinema.webcommon.HTTP
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/session/{sessionId}/reservation")
class ReservationEndpoint(
    private val reservationService: ReservationService
) {

    @PostMapping
    fun createReservations(
            @PathVariable("sessionId") sessionId: Long,
            @RequestBody reservation: Reservation
    ): ResponseEntity<*> =
        when(val res = reservationService.reserveSeats(sessionId, reservation)){
            is ReservationRes.SessionNotFound -> HTTP.conflict(code = ErrorCode.SESSION_NOT_FOUND)
            is ReservationRes.AlreadyReserved -> HTTP.conflict(
                    code = ErrorCode.ALREADY_RESERVED,
                    payload = res.reservedSeats)
            is ReservationRes.SeatsMissing -> HTTP.conflict(
                    code = ErrorCode.SEATS_MISSING,
                    payload = res.seats)
            is ReservationRes.Success -> HTTP.created()
        }

    @GetMapping
    fun getReservations(@PathVariable("sessionId") sessionId: Long): ResponseEntity<*> =
        when(val res = reservationService.getReservationStatus(sessionId)){
            is ReservationStatusRes.SessionNotFound -> HTTP.conflict(
                    code = ErrorCode.SESSION_NOT_FOUND
            )
            is ReservationStatusRes.Success -> HTTP.ok(res.seatStatuses)
        }
}
