package dgis.interview.cinema.webcommon

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(e: ValidationException): ResponseEntity<ValidationError>{
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ValidationError(e.errors))
    }
}

