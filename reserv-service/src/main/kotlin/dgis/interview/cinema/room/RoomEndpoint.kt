package dgis.interview.cinema.room

import dgis.interview.cinema.IdAccessRepo
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/room")
class RoomEndpoint(
    private val roomRepo: RoomRepo
){

    @PutMapping
    fun createRoom(@RequestBody room: Room): ResponseEntity<Unit> {
        roomRepo.add(room)
        return ResponseEntity.ok().build()
    }
}

@Repository
class RoomRepo: IdAccessRepo<Room, Long> {

    fun add(room: Room){
        TODO()
    }

    override fun findById(id: Long): Room? {
        TODO("Not yet implemented")
    }

}
