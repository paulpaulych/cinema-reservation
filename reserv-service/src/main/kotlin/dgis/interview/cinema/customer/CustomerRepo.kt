package dgis.interview.cinema.customer

import dgis.interview.cinema.IdAccessRepo
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class CustomerRepo(
    private val jdbc: NamedParameterJdbcTemplate
): IdAccessRepo<Customer, Long> {

    private val customers = mutableSetOf<Customer>()

    fun addCustomer(customer: Customer): AddCustomerRes =
        if(customers.add(customer)) {
            AddCustomerRes.Success
        } else {
            AddCustomerRes.AlreadyExists
        }

    override fun findById(id: Long): Customer? {
        return customers.find{ it.id == id }
    }
}

sealed class AddCustomerRes{
    object Success: AddCustomerRes()
    object AlreadyExists: AddCustomerRes()
}

