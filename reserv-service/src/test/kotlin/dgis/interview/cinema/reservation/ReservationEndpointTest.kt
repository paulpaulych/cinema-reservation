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
    private val session = AddSessionReq(id = 1, roomExternalId = room.id)
    private val customer1 = 1
    private val customer2 = 2
    private val seats1 = listOf(
            Seat(1, 1),
            Seat(2, 2)
    )
    private val seats2 = listOf(
            Seat(1, 1),
            Seat(1, 2)
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
            param("sessionId", session.id)
            param("customerId", customer1)
            body(json.writeValueAsString(seats1))
            contentType(ContentType.JSON)
            filter(ResponseLoggingFilter())
        } When { put("/reservation")
        } Then { statusCode(200) }

        Given {
            param("sessionId", session.id)
            filter(ResponseLoggingFilter())
        } When {
            get("/reservation")
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
            param("sessionId", sessionId)
            param("customerId", customer1)
            body(json.writeValueAsString(seats1))
            contentType(ContentType.JSON)
        } When { put("/reservation")
        } Then { statusCode(200) }

        Given {
            param("sessionId", sessionId)
            param("customerId", customer2)
            body(json.writeValueAsString(seats2))
            contentType(ContentType.JSON)
        } When { put("/reservation")
        } Then {
            statusCode(409)
            body("code", equalTo("ALREADY_RESERVED"))
            extractBody<SafeErrorRes>(json)
                    .readPayload<List<Seat>>(json) shouldBe listOf(Seat(1,1))
        }

        Given {
            param("sessionId", sessionId)
            filter(ResponseLoggingFilter())
        } When { get("/reservation")
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
            param("sessionId", session.id + 2)
            param("customerId", customer1)
            body(json.writeValueAsString(seats1))
            contentType(ContentType.JSON)
        } When { put("/reservation")
        } Then {
            statusCode(409)
            body("code", equalTo("SESSION_NOT_FOUND"))
        }
    }

    private fun addSession(session: AddSessionReq) {
        Given {
            body(json.writeValueAsString(session))
            contentType(ContentType.JSON)
        } When { put("/session")
        } Then { statusCode(200) }
    }

    private fun addRoom(room: AddRoomReq) {
        Given {
            body(json.writeValueAsString(room))
            contentType(ContentType.JSON)
        } When { put("/room")
        } Then { statusCode(200) }
    }
}