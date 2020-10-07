package dgis.interview.cinema.customer

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class Customer(
    val id: Long
)

@RestController
@RequestMapping("/customer")
class CustomerController(
    private val repo: CustomerRepo
){
    @PutMapping
    fun addCustomer(@RequestBody customer: Customer): ResponseEntity<Unit> =
        when(repo.addCustomer(customer)){
            is AddCustomerRes.Success -> ResponseEntity.ok().build()
            is AddCustomerRes.AlreadyExists -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .build()
        }
}