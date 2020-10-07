package dgis.interview.cinema.customer

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/customer")
class CustomerController(
    private val repo: CustomerRepository
){
    @PutMapping
    fun addCustomer(@RequestBody customer: Customer): ResponseEntity<Unit> =
        when(repo.addCustomer(customer)){
            is AddCustomerRes.Success -> ResponseEntity.ok().build()
            is AddCustomerRes.AlreadyExists -> ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build()
        }
}

data class Customer(
    val externalId: Long
)

@Repository
class CustomerRepository (
    private val jdbc: NamedParameterJdbcTemplate
){
    private val customers = mutableSetOf<Customer>()

    fun addCustomer(customer: Customer): AddCustomerRes =
        if(customers.add(customer)) {
            AddCustomerRes.Success
        } else {
            AddCustomerRes.AlreadyExists
        }

}

sealed class AddCustomerRes{
    object Success: AddCustomerRes()
    object AlreadyExists: AddCustomerRes()
}