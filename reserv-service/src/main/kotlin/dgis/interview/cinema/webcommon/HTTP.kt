package dgis.interview.cinema.webcommon

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity


object HTTP {

    fun created(): ResponseEntity<Unit> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .build()

    fun <T> ok(body: T): ResponseEntity<T> =
        ResponseEntity
            .ok()
            .body(body)

    fun conflict(
            code: ErrorCode,
            message: String? = null,
            payload: Any? = null): ResponseEntity<ErrorRes> =
        conflict(ErrorRes(code, message, payload))

    private fun conflict(body: ErrorRes): ResponseEntity<ErrorRes> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(body)
}