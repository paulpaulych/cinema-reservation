package dgis.interview.cinema.session

import com.fasterxml.jackson.databind.ObjectMapper
import dgis.interview.cinema.room.AddRoomReq
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
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
internal class SessionEndpointTest(
    @LocalServerPort
    val port: Int,
    @Autowired
    val flyway: Flyway
){

    init {
        RestAssured.port = port
        flyway.clean()
        flyway.migrate()
    }

    private val serializer = ObjectMapper()
    private val room = AddRoomReq(1, mapOf(1 to 10))
    private val session1 = AddSessionReq(1L, 1)

    @Test
    @Order(1)
    fun `room not found`() {
        Given {
            body(serializer.writeValueAsString(session1))
            contentType(ContentType.JSON)
        } When {
            put("/session")
        } Then {
            statusCode(409)
            body("code", equalTo("ROOM_NOT_FOUND"))
        }
    }

    @Test
    @Order(2)
    fun `success`(){
        Given {
            body(serializer.writeValueAsString(room))
            contentType(ContentType.JSON)
        } When {
            put("/room")
        } Then {
            statusCode(200)
        }

        Given {
            body(serializer.writeValueAsString(session1))
            contentType(ContentType.JSON)
            filter(ResponseLoggingFilter())
        } When {
            put("/session")
        } Then {
            statusCode(200)
        }
    }

    @Test
    @Order(3)
    fun `already exists`(){
        Given {
            body(serializer.writeValueAsString(session1))
            contentType(ContentType.JSON)
        } When {
            put("/session")
        } Then {
            statusCode(409)
            body("code", equalTo("ALREADY_EXISTS"))
        }
    }
}