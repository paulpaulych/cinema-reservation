package dgis.interview.cinema.db.transaction

import java.sql.Connection
import java.util.concurrent.atomic.AtomicLong

data class JdbcTransactionDef(
    val isolation: Isolation,
    val propagation: Propagation
) {
    val id: Long = nextId()

    companion object {
        private val lastId = AtomicLong(0)
        private fun nextId() = lastId.incrementAndGet()
    }
}

data class JdbcTransaction(
    val definition: JdbcTransactionDef,
    val connection: Connection
) {
    fun commit() = connection.commit()
    fun rollback() = connection.rollback()
}

enum class Isolation{
    READ_UNCOMMITTED,
    READ_COMMITTED,
    REPEATABLE_READ,
    SERIALIZABLE;

    fun jdbcConst(): Int =
        when(this){
            READ_UNCOMMITTED -> Connection.TRANSACTION_READ_UNCOMMITTED
            READ_COMMITTED -> Connection.TRANSACTION_READ_COMMITTED
            REPEATABLE_READ -> Connection.TRANSACTION_REPEATABLE_READ
            SERIALIZABLE -> Connection.TRANSACTION_SERIALIZABLE
        }
}

enum class Propagation{
    REQUIRES_NEW,
    USE_EXISTING,
    USE_EXISTING_STRICT
}