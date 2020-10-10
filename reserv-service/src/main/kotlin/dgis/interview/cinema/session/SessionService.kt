package dgis.interview.cinema.session

import dgis.interview.cinema.AddOneRes
import dgis.interview.cinema.room.RoomRepo
import org.springframework.stereotype.Service


@Service
class SessionService(
    private val sessionRepo: SessionRepo,
    private val roomRepo: RoomRepo
){

    fun addSession(sessionId: Long, roomId: Long): AddSessionRes {
        if(!roomRepo.existsById(roomId)){
            return AddSessionRes.RoomNotFound
        }
        return when(sessionRepo.add(sessionId, roomId)){
            is AddOneRes.Success -> AddSessionRes.Success
            is AddOneRes.AlreadyExists -> AddSessionRes.AlreadyExists
        }
    }

}

sealed class AddSessionRes{
    object Success: AddSessionRes()
    object RoomNotFound: AddSessionRes()
    object AlreadyExists: AddSessionRes()
}