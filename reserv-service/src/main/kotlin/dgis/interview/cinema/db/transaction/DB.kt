package dgis.interview.cinema.db.transaction

import java.sql.Connection

/**
 * interface for transactional JDBC operations
 */
class DB(
    private val transactionManager: IJdbcTxManager
){

    /**
     * runs action in jdbc transaction. Uses existing transaction by default
     */
    fun <T> inTransaction(isolation: Isolation,
                          propagation: Propagation = Propagation.USE_EXISTING,
                          action: Connection.() -> T): T {
        val def = JdbcTransactionDef(isolation, propagation)
        val tx = transactionManager.getTransaction(def)
        return runCatching {
            action.invoke(tx.connection)
        }.onFailure {
            transactionManager.rollback(tx)
        }.onSuccess {
            transactionManager.commit(tx)
        }.getOrThrow()
    }
}