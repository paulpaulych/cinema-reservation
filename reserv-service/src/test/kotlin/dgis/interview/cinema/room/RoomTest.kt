package dgis.interview.cinema.room

import io.kotest.matchers.shouldBe
import junit.framework.Assert.assertTrue
import org.junit.jupiter.api.Test

class RoomTest {

    private val room = Room(mapOf(
            1 to 1,
            2 to 2,
            3 to 3
    ))

    private val allSeats = setOf(
            Seat(1, 1),
            Seat(2, 1),
            Seat(2, 2),
            Seat(3, 1),
            Seat(3, 2),
            Seat(3, 3))

    @Test
    fun `should generate all seats`(){
        room.getAllSeats().toSet() shouldBe allSeats
    }

    @Test
    fun `should have seats`(){
        val assertIfNotPresent = { seats: List<Seat> ->
            assertTrue(room.hasSeats(seats) is SeatPresenceRes.AllPresent)
        }
        allSeats.forEach { assertIfNotPresent(listOf(it)) }
        assertIfNotPresent(allSeats.toList())
    }

    @Test
    fun `should not have seats`() {
        val assertIfPresent = { seats: List<Seat>, missed: List<Seat> ->
            (room.hasSeats(seats) as? SeatPresenceRes.Missed)
                    ?.let {  it.seats.toSet() shouldBe missed.toSet() }
                    ?: assertTrue("seat must be missed", false)
            }

        val plus3 = allSeats.map { it.copy(seatNum = it.seatNum + 3) }
        assertIfPresent(plus3, plus3)

        assertIfPresent(
                listOf(Seat(1, 0), Seat(1, 1)),
                listOf(Seat(1, 0))
        )

        val seats1 = listOf(
                Seat(1, 1),
                Seat(2, 2)
        )

        assertTrue(Room(mapOf(1 to 1, 2 to 2)).hasSeats(seats1) is SeatPresenceRes.AllPresent)
    }

}

