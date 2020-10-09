package dgis.interview.cinema.session

import dgis.interview.cinema.webcommon.HTTP
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AddSessionReq(
        val id: Long,
        val roomExternalId: Long
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

    @PutMapping
    fun addSession(@RequestBody req: AddSessionReq): ResponseEntity<*> =
        when(sessionService.addSession(req.id, req.roomExternalId)){
            is AddSessionRes.Success -> HTTP.ok()
            is AddSessionRes.RoomNotFound -> HTTP.conflict(code = ErrorCode.ROOM_NOT_FOUND.name)
            is AddSessionRes.AlreadyExists -> HTTP.conflict(code = ErrorCode.ALREADY_EXISTS.name)
        }
}
