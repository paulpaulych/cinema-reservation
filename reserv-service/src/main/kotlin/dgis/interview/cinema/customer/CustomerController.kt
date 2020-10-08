package dgis.interview.cinema.customer

import dgis.interview.cinema.AddOneRes
import dgis.interview.cinema.webcommon.HTTP
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class Customer(
    val id: Long
)

private const val ALREADY_EXISTS = "ALREADY_EXISTS"

@RestController
@RequestMapping("/customer")
class CustomerController(
    private val repo: CustomerRepo
){
    @PutMapping
    fun addCustomer(@RequestBody customer: Customer): ResponseEntity<*> =
        when(repo.addCustomer(customer)){
            is AddOneRes.Success -> HTTP.ok()
            is AddOneRes.AlreadyExists -> HTTP.conflict(code = ALREADY_EXISTS)
        }
}