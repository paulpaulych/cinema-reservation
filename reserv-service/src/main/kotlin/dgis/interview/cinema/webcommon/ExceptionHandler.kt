package dgis.interview.cinema.webcommon

import dgis.interview.cinema.LoggerProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
@ControllerAdvice
class ExceptionHandler {

    private val log by LoggerProperty()

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(e: ValidationException): ResponseEntity<ValidationError>{
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ValidationError(e.errors))
    }

    @ExceptionHandler(Throwable::class)
    fun handleThrowable(e: Throwable): ResponseEntity<GenericErrorRes>{
        log.error(e.message, e)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                GenericErrorRes(
                    code = GenericErrorCode.GENERIC_SERVER_ERROR,
                    message = e.message ?: e.cause?.message ?: "unreadable error",
                    payload = null
                )
            )
    }

}

