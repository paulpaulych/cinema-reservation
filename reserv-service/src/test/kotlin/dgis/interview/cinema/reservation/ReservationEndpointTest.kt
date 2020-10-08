package dgis.interview.cinema.reservation

import dgis.interview.cinema.customer.Customer

import com.fasterxml.jackson.databind.ObjectMapper
import dgis.interview.cinema.room.AddRoomReq
import dgis.interview.cinema.room.Seat
import dgis.interview.cinema.session.AddSessionReq
import io.restassured.RestAssured
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
internal class ReservationEndpointTest(
        @LocalServerPort
        val port: Int,
        @Autowired
        val flyway: Flyway
){

    private val serializer = ObjectMapper()

    private val room = AddRoomReq(1, mapOf(1 to 2, 2 to 2))
    private val session = AddSessionReq(id = 1, roomId = room.id)
    private val customer1 = Customer(1L)
    private val customer2 = Customer(2L)
    private val customer3 = Customer(3L)
    private val seats1 = listOf(
            Seat(1, 1),
            Seat(2, 2)
    )
    private val seats2 = listOf(
            Seat(1, 1),
            Seat(1, 2)
    )
    private val after1Statuses = setOf(
            SeatStatus(Seat(1, 1), false),
            SeatStatus(Seat(1, 2), true),
            SeatStatus(Seat(2, 2), false),
            SeatStatus(Seat(2, 2), true),
    )

    init {
        RestAssured.port = port
        flyway.clean()
        flyway.migrate()

        Given {
            body(serializer.writeValueAsString(room))
            contentType(ContentType.JSON)
        } When {
            put("/room")
        } Then {
            statusCode(200)
        }

        Given {
            body(serializer.writeValueAsString(session))
            contentType(ContentType.JSON)
        } When {
            put("/session")
        } Then {
            statusCode(200)
        }

        Given {
            body(serializer.writeValueAsString(customer1))
            contentType(ContentType.JSON)
        } When {
            put("/customer")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `should reserve seats successfully`() {
        Given {
            param("sessionId", session.id)
            param("customerId", customer1.id)
            body(serializer.writeValueAsString(seats1))
            contentType(ContentType.JSON)
            filter(ResponseLoggingFilter())
        } When {
            put("/reservation")
        } Then {
            statusCode(200)
        }

        Given {
            param("sessionId", session.id)
            filter(ResponseLoggingFilter())
        } When {
            get("/reservation")
        } Then {
            statusCode(200)
            //TODO: проверить тело
        }
    }

    @Test
    fun `already reserved`() {
        Given {
            body(serializer.writeValueAsString(customer2))
            contentType(ContentType.JSON)
        } When {
            put("/customer")
        } Then {
            statusCode(200)
        }

        Given {
            param("sessionId", session.id)
            param("customerId", customer2.id)
            body(serializer.writeValueAsString(seats2))
            contentType(ContentType.JSON)
            filter(ResponseLoggingFilter())
        } When {
            put("/reservation")
        } Then {
            statusCode(409)
            body("code", equalTo("ALREADY_RESERVED"))
        }

        Given {
            param("sessionId", session.id)
            filter(ResponseLoggingFilter())
        } When {
            get("/reservation")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `customer not found`(){
        Given {
            param("sessionId", session.id)
            param("customerId", customer3.id)
            body(serializer.writeValueAsString(seats1))
            contentType(ContentType.JSON)
        } When {
            put("/reservation")
        } Then {
            statusCode(409)
            body("code", equalTo("CUSTOMER_NOT_FOUND"))
        }
    }

    @Test
    fun `session not found`(){
        Given {
            param("sessionId", session.id + 10)
            param("customerId", customer1.id)
            body(serializer.writeValueAsString(seats1))
            contentType(ContentType.JSON)
        } When {
            put("/reservation")
        } Then {
            statusCode(409)
            body("code", equalTo("SESSION_NOT_FOUND"))
        }
    }
}