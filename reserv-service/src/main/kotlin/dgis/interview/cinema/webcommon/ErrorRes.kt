package dgis.interview.cinema.webcommon

data class ErrorRes(
    val code: ErrorCode,
    val message: String? = null,
    val payload: Any? = null
)

enum class ErrorCode {
    ALREADY_RESERVED,
    SEATS_MISSING,
    SESSION_NOT_FOUND,
    ROOM_NOT_FOUND,
    ALREADY_EXISTS,
}
