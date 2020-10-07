package dgis.interview.cinema.reservation

import dgis.interview.cinema.room.Seat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/reservation")
class ReservationEndpoint(
    private val reservationService: ReservationService
) {

    @PutMapping
    fun createReservations(
        @RequestParam("sessionId") sessionId: Long,
        @RequestParam("customerId") customerId: Long,
        @RequestBody seats: List<Seat>): ResponseEntity<*> {
        return when(val res = reservationService.reserveSeats(sessionId, customerId, seats)){
            is ReservationRes.CollisionOccurred -> { TODO() }
            is ReservationRes.CustomerNotFound -> { TODO() }
            is ReservationRes.SeatsMissing -> { TODO() }
            is ReservationRes.SessionNotFound -> {TODO() }
            is ReservationRes.Success -> { TODO() }
        }
    }

    @GetMapping
    fun getReservations(@RequestParam("sessionId") sessionId: Long): ResponseEntity<*> =
        when(val res = reservationService.getReservationStatus(sessionId)){
            is ReservationStatusRes.SessionNotFound -> { TODO() }
            is ReservationStatusRes.Success -> ResponseEntity.ok(res.seatStatuses)
        }
}
