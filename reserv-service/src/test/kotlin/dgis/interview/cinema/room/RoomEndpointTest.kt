package dgis.interview.cinema.room

import dgis.interview.cinema.BaseIntegrationTest
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort


internal class RoomEndpointTest(
        @LocalServerPort port: Int,
        @Autowired flyway: Flyway
): BaseIntegrationTest(port, flyway){

    private val room1 = AddRoomReq(1, mapOf(1 to 1, 2 to 2))
    private val room2 = AddRoomReq(1, mapOf(1 to 1, 2 to 2))

    @Test
    fun `should register new customer successfully`() {
        Given {
            body(json.writeValueAsString(room1))
            contentType(ContentType.JSON)
        } When { put("/room")
        } Then { statusCode(200) }
    }

    @Test
    fun `duplication error`(){
        Given {
            body(json.writeValueAsString(room2))
            contentType(ContentType.JSON)
        } When { put("/room")
        } Then { statusCode(200) }

        Given {
            body(json.writeValueAsString(room2))
            contentType(ContentType.JSON)
        } When { put("/room")
        } Then {
            statusCode(409)
            body("code", equalTo("ALREADY_EXISTS"))
        }
    }

    @Test
    fun `validation error`() {
        Given {
            body(json.writeValueAsString(AddRoomReq(12, mapOf())))
            contentType(ContentType.JSON)
            filter(ResponseLoggingFilter())
        } When { put("/room")
        } Then { statusCode(400) }
    }
}