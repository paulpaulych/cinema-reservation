package dgis.interview.cinema.customer

import dgis.interview.cinema.AddOneRes
import dgis.interview.cinema.IdAccessRepo
import dgis.interview.cinema.db.hasAny
import dgis.interview.cinema.db.queryOne
import org.springframework.stereotype.Repository
import javax.sql.DataSource


@Repository
class CustomerRepo(
    private val ds: DataSource
): IdAccessRepo<Customer, Long> {

    //TODO: навернуть транзакцию
    fun addCustomer(customer: Customer): AddOneRes {
        ds.connection.use { conn ->
            val exists = conn.prepareStatement("select id from customers where id = ?")
                .apply { setLong(1, customer.id) }
                .hasAny()

            if(exists){
                return AddOneRes.AlreadyExists
            }

            conn.prepareStatement("insert into customers(id) values (?)")
                .apply {
                    setLong(1, customer.id)
                    executeUpdate()
                }
        }
        return AddOneRes.Success
    }

    override fun findById(id: Long): Customer? =
        ds.connection.use { conn ->
            conn.prepareStatement("select id from customers where id = ?")
                    .apply { setLong(1, id) }
                    .queryOne { Customer(it.getLong("id")) }
        }
}

sealed class AddCustomerRes{
    object Success: AddCustomerRes()
    object AlreadyExists: AddCustomerRes()
}

