package dgis.interview.cinema.reservation

import dgis.interview.cinema.BaseIntegrationTest
import dgis.interview.cinema.SafeErrorRes
import dgis.interview.cinema.extractBody
import dgis.interview.cinema.readPayload
import dgis.interview.cinema.room.AddRoomReq
import dgis.interview.cinema.room.Seat
import dgis.interview.cinema.session.AddSessionReq
import io.kotest.matchers.shouldBe
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort

internal class ReservationEndpointTest(
        @LocalServerPort port: Int,
        @Autowired flyway: Flyway
): BaseIntegrationTest(port, flyway){

    private val room = AddRoomReq(1, mapOf(1 to 2, 2 to 2))
    private val session = AddSessionReq(id = 1, roomId = room.id)
    private val customer1 = 1L
    private val customer2 = 2L
    private val reservation1 = Reservation(
            customer1,
            listOf(Seat(1, 1), Seat(2, 2))
    )
    private val reservation2 = Reservation(
            customer2,
            listOf(Seat(1, 1), Seat(1, 2))
    )
    private val after1Statuses = setOf(
            SeatStatus(Seat(1, 1), 1),
            SeatStatus(Seat(1, 2), null),
            SeatStatus(Seat(2, 1), null),
            SeatStatus(Seat(2, 2), 1),
    )

    @Test
    fun `should reserve seats successfully`() {
        addRoom(room)
        addSession(session)

        Given {
            body(json.writeValueAsString(reservation1))
            contentType(ContentType.JSON)
            filter(ResponseLoggingFilter())
        } When { post("/session/${session.id}/reservation")
        } Then { statusCode(201) }

        Given {
            filter(ResponseLoggingFilter())
        } When { get("/session/${session.id}/reservation")
        } Then {
            statusCode(200)
            extractBody<List<SeatStatus>>(json).let {
                it.size shouldBe after1Statuses.size
                it.toSet() shouldBe after1Statuses
            }
        }
    }

    @Test
    fun `already reserved`() {
        val roomId = this.room.id + 1
        val sessionId = session.id + 1
        val room = this.room.copy(id = roomId)
        addRoom(room)
        addSession(AddSessionReq(sessionId, roomId))

        Given {
            body(json.writeValueAsString(reservation1))
            contentType(ContentType.JSON)
        } When { post("/session/${sessionId}/reservation")
        } Then { statusCode(201) }

        Given {
            body(json.writeValueAsString(reservation2))
            contentType(ContentType.JSON)
        } When { post("/session/${sessionId}/reservation")
        } Then {
            statusCode(409)
            body("code", equalTo("ALREADY_RESERVED"))
            extractBody<SafeErrorRes>(json)
                    .readPayload<List<Seat>>(json) shouldBe listOf(Seat(1,1))
        }

        Given {
            filter(ResponseLoggingFilter())
        } When { get("/session/${sessionId}/reservation")
        } Then {
            statusCode(200)
            extractBody<List<SeatStatus>>(json).let {
                it.size shouldBe after1Statuses.size
                it.toSet() shouldBe after1Statuses
            }
        }
    }

    @Test
    fun `session not found`(){
        Given {
            body(json.writeValueAsString(reservation1))
            contentType(ContentType.JSON)
            filter(ResponseLoggingFilter())
        } When { post("/session/${session.id + 2}/reservation")
        } Then {
            statusCode(409)
            body("code", equalTo("SESSION_NOT_FOUND"))
        }
    }

    private fun addSession(session: AddSessionReq) {
        Given {
            body(json.writeValueAsString(session))
            contentType(ContentType.JSON)
        } When { post("/session")
        } Then { statusCode(201) }
    }

    private fun addRoom(room: AddRoomReq) {
        Given {
            body(json.writeValueAsString(room))
            contentType(ContentType.JSON)
        } When { post("/room")
        } Then { statusCode(201) }
    }
}