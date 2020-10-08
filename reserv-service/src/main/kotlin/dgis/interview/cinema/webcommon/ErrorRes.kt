package dgis.interview.cinema.webcommon

data class ErrorRes(
    val code: String,
    val message: String? = null,
    val payload: Any? = null
)
