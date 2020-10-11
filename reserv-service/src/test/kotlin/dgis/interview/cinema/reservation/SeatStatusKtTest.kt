package dgis.interview.cinema.reservation

import dgis.interview.cinema.room.Room
import dgis.interview.cinema.room.Seat
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class SeatStatusKtTest{

    private val seats = listOf(
        Seat(1,1),
        Seat(2,1),
        Seat(2,2)
    )

    private val room = Room(mapOf(1 to 1, 2 to 2))

    @Test
    fun `all seats are free`(){
        val res = getSeatStatuses(room, listOf())
        res.toSet() shouldBe seats.map { SeatStatus(it, null) }.toSet()
    }

    @Test
    fun `all seats reserved by one customer`(){
        val res = getSeatStatuses(room, listOf(Reservation(customerId = 1, seats)))
        res.toSet() shouldBe seats.map { SeatStatus(it, 1) }.toSet()
    }

    @Test
    fun `seats reserved by two customer`(){
        val reservations = listOf(
            Reservation(customerId = 1, seats.subList(0, 1)),
            Reservation(customerId = 2, seats.subList(1, 2))
        )
        val res = getSeatStatuses(room, reservations)
        res.toSet() shouldBe seats.mapIndexed { i, e->
            SeatStatus(e, if(i == 0) 1 else if(i == 1) 2 else null)
        }.toSet()
    }


}