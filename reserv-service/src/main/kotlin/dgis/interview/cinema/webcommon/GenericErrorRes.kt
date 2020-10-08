package dgis.interview.cinema.webcommon

data class GenericErrorRes(
    val code: GenericErrorCode,
    val message: String,
    val payload: Any? = null
)

enum class GenericErrorCode {
    /**
     * Необработанное исключение на сервере. message берется из исключения
     */
    GENERIC_SERVER_ERROR,
}