package dgis.interview.cinema

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.restassured.RestAssured
import org.flywaydb.core.Flyway
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
abstract class BaseIntegrationTest(
        port: Int,
        flyway: Flyway
) {

    protected val json = jacksonObjectMapper()

    init {
        RestAssured.port = port
        //перед запуском каждого тест-класса чистим базу и накатываем миграции
        flyway.clean()
        flyway.migrate()
    }
}