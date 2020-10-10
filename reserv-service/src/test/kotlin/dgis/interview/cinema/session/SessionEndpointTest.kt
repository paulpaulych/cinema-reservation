package dgis.interview.cinema.session

import dgis.interview.cinema.BaseIntegrationTest
import dgis.interview.cinema.room.AddRoomReq
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

internal class SessionEndpointTest(
    @LocalServerPort port: Int,
    @Autowired flyway: Flyway
): BaseIntegrationTest(port, flyway){

    private val room = AddRoomReq(1, mapOf(1 to 10))
    private val session1 = AddSessionReq(1L, 1)
    private val session2 = AddSessionReq(2L, 1)

    @Test
    fun `room not found`() {
        Given {
            body(json.writeValueAsString(session1))
            contentType(ContentType.JSON)
        } When { post("/session") } Then {
            statusCode(409)
            body("code", equalTo("ROOM_NOT_FOUND"))
        }
    }

    @Test
    fun `success`(){
        addRoom(room)

        Given {
            body(json.writeValueAsString(session1))
            contentType(ContentType.JSON)
            filter(ResponseLoggingFilter())
        } When { post("/session")
        } Then { statusCode(201) }
    }

    @Test
    fun `already exists`(){
        addRoom(room)

        Given {
            body(json.writeValueAsString(session2))
            contentType(ContentType.JSON)
        } When { post("/session")
        } Then { statusCode(201) }

        Given {
            body(json.writeValueAsString(session2))
            contentType(ContentType.JSON)
            filter(ResponseLoggingFilter())
        } When { post("/session")
        } Then {
            statusCode(409)
            body("code", equalTo("ALREADY_EXISTS"))
        }
    }

    private fun addRoom(room: AddRoomReq){
        Given {
            body(json.writeValueAsString(room))
            contentType(ContentType.JSON)
        } When { post("/room")
        } Then { statusCode(201) }
    }
}