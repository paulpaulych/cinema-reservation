package dgis.interview.cinema.room

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.restassured.RestAssured
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
internal class RoomEndpointTest(
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

    private val serializer = jacksonObjectMapper()
    private val room1 = AddRoomReq(1, mapOf(1 to 1, 2 to 2))
    private val room2 = AddRoomReq(1, mapOf(1 to 1, 2 to 2))

    @Test
    fun `should register new customer successfully`() {
        Given {
            body(serializer.writeValueAsString(room1))
            contentType(ContentType.JSON)
        } When {
            put("/room")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `duplication error`(){
        Given {
            body(serializer.writeValueAsString(room2))
            contentType(ContentType.JSON)
        } When {
            put("/room")
        } Then {
            statusCode(200)
        }

        Given {
            body(serializer.writeValueAsString(room2))
            contentType(ContentType.JSON)
        } When {
            put("/room")
        } Then {
            statusCode(409)
            body("code", equalTo("ALREADY_EXISTS"))
        }
    }

    @Test
    fun `validation error`() {
        Given {
            body(serializer.writeValueAsString(AddRoomReq(12, mapOf())))
            contentType(ContentType.JSON)
            filter(ResponseLoggingFilter())
        } When {
            put("/room")
        } Then {
            statusCode(400)
        }
    }
}