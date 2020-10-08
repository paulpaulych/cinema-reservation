package dgis.interview.cinema.room

import dgis.interview.cinema.AddOneRes
import dgis.interview.cinema.webcommon.HTTP
import dgis.interview.cinema.webcommon.throwIfNotValid
import io.konform.validation.Validation
import io.konform.validation.jsonschema.minimum
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AddRoomReq(
    val id: Long,
    val rowSizes: Map<Int, Int>
)

private const val ALREADY_EXISTS = "ALREADY_EXISTS"
@RestController
@RequestMapping("/room")
class RoomEndpoint(
    private val roomRepo: RoomRepo
){

    val validation = Validation<AddRoomReq> {
        AddRoomReq::rowSizes {
            Map<Int, Int>::size {
                minimum(1)
            }
        }
    }

    @PutMapping
    fun createRoom(@RequestBody req: AddRoomReq): ResponseEntity<*> {
        validation.throwIfNotValid(req)

        return when(roomRepo.add(req.id, req.rowSizes)){
            is AddOneRes.Success -> HTTP.ok()
            is AddOneRes.AlreadyExists -> HTTP.conflict(code = ALREADY_EXISTS)
        }
    }

}


