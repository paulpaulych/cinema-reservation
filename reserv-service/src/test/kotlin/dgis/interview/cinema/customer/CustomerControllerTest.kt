package dgis.interview.cinema.customer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.restassured.RestAssured
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
internal class CustomerControllerTest(
    @LocalServerPort
    val port: Int,
    @Autowired
    val flyway: Flyway
){

    init {
        RestAssured.port = port
    }

    private val serializer = ObjectMapper()

    private val customer1 = Customer(1L)

    private val customer2 = Customer(2L)

    @Test
    fun `should register new customer successfully`() {
        Given {
            body(serializer.writeValueAsString(customer1))
            contentType(ContentType.JSON)
            filter(ResponseLoggingFilter())
        } When {
            put("/customer")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `duplication error`(){
        Given {
            body(serializer.writeValueAsString(customer2))
            contentType(ContentType.JSON)
        } When {
            put("/customer")
        } Then {
            statusCode(200)
        }

        Given {
            body(serializer.writeValueAsString(customer2))
            contentType(ContentType.JSON)
        } When {
            put("/customer")
        } Then {
            statusCode(409)
        }
    }
}