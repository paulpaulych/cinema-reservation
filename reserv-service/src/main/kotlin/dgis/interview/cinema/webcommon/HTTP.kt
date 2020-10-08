package dgis.interview.cinema.webcommon

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Используется для формирования ответа RestController.
 * Ничто кроме этого класса для данной цели не используется
 */
object HTTP {

    fun ok(): ResponseEntity<Unit> = ResponseEntity.ok().build()

    fun <T> ok(body: T): ResponseEntity<T> = ResponseEntity.ok().body(body)

    fun conflict(
            code: String,
            message: String? = null,
            payload: Any? = null): ResponseEntity<ErrorRes> =
        conflict(ErrorRes(code, message, payload))

    fun conflict(body: ErrorRes): ResponseEntity<ErrorRes> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(body)
}