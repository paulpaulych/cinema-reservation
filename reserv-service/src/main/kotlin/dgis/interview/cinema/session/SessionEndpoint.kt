package dgis.interview.cinema.session

import dgis.interview.cinema.webcommon.HTTP
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AddSessionReq(
        val id: Long,
        val roomId: Long
)

private enum class ErrorCode {
    ROOM_NOT_FOUND,
    ALREADY_EXISTS
}

@RestController
@RequestMapping("/session")
class SessionEndpoint(
    private val sessionService: SessionService
) {

    @PostMapping
    fun addSession(@RequestBody req: AddSessionReq): ResponseEntity<*> =
        when(sessionService.addSession(req.id, req.roomId)){
            is AddSessionRes.Success -> HTTP.created()
            is AddSessionRes.RoomNotFound -> HTTP.conflict(code = ErrorCode.ROOM_NOT_FOUND.name)
            is AddSessionRes.AlreadyExists -> HTTP.conflict(code = ErrorCode.ALREADY_EXISTS.name)
        }
}
