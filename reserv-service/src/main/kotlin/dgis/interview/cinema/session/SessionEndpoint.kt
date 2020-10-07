package dgis.interview.cinema.session

import dgis.interview.cinema.room.RoomRepo
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AddSessionReq(
    val id: Long,
    val roomId: Long
)

@RestController
@RequestMapping("/session")
class SessionEndpoint(
    private val sessionService: SessionService
) {

    @PutMapping
    fun addSession(@RequestBody req: AddSessionReq): ResponseEntity<*>{
        return when(val res = sessionService.addSession(req.id, req.roomId)){
            is AddSessionRes.Success -> { TODO() }
            is AddSessionRes.RoomNotFound -> { TODO() }
        }
    }

}

@Service
class SessionService(
    private val sessionRepo: SessionRepo,
    private val roomRepo: RoomRepo
){

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun addSession(sessionId: Long, roomId: Long): AddSessionRes {
        val room = roomRepo.findById(roomId)
            ?: return AddSessionRes.RoomNotFound(roomId)
        sessionRepo.add(Session(sessionId, room))
        return AddSessionRes.Success
    }
}


sealed class AddSessionRes{
    object Success: AddSessionRes()
    data class RoomNotFound(val roomId: Long): AddSessionRes()
}