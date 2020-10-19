package dgis.interview.cinema.webcommon

import io.konform.validation.*

/**
 * @throws ValidationException если запрос не прошел валидацию для последующей обработки в [ExceptionHandler].
 */
fun <T> Validation<T>.throwIfNotValid(req: T): Unit =
    when(val res = this.validate(req)){
        is Valid -> {}
        is Invalid -> {
            val errors = res.errors.map {
                it.dataPath to it.message
            }.toMap()
            throw ValidationException(errors)
        }
    }

/**
 * содержит список ошибок в виде путь->сообщение
 */
class ValidationException(
    val errors: Map<String, String>
): RuntimeException()

data class ValidationError(
    val errors: Map<String, String>
)
