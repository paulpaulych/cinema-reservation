package dgis.interview.cinema.reservation

import dgis.interview.cinema.webcommon.HTTP
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private enum class ErrorCode {
    ALREADY_RESERVED,
    SEATS_MISSING,
    SESSION_NOT_FOUND
}

@RestController
@RequestMapping("/session")
class ReservationEndpoint(
    private val reservationService: ReservationService
) {

    @RequestMapping("/{sessionId}/reservation", method = [RequestMethod.POST])
    fun createReservations(
            @PathVariable("sessionId") sessionId: Long,
            @RequestBody reservation: Reservation
    ): ResponseEntity<*> =
        when(val res = reservationService.reserveSeats(sessionId, reservation)){
            is ReservationRes.SessionNotFound -> HTTP.conflict(code = ErrorCode.SESSION_NOT_FOUND.name)
            is ReservationRes.AlreadyReserved -> HTTP.conflict(
                    code = ErrorCode.ALREADY_RESERVED.name,
                    payload = res.reservedSeats)
            is ReservationRes.SeatsMissing -> HTTP.conflict(
                    code = ErrorCode.SEATS_MISSING.name,
                    payload = res.seats)
            is ReservationRes.Success -> HTTP.created()
        }

    @RequestMapping("/{sessionId}/reservation", method = [RequestMethod.GET])
    fun getReservations(@PathVariable("sessionId") sessionId: Long): ResponseEntity<*> =
        when(val res = reservationService.getReservationStatus(sessionId)){
            is ReservationStatusRes.SessionNotFound -> HTTP.conflict(
                    code = ErrorCode.SESSION_NOT_FOUND.name
            )
            is ReservationStatusRes.Success -> HTTP.ok(res.seatStatuses)
        }
}
