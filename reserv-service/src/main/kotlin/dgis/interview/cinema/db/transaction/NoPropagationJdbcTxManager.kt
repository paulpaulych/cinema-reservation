package dgis.interview.cinema.db.transaction

import dgis.interview.cinema.LoggerProperty
import javax.sql.DataSource

/**
 * [IJdbcTxManager] which creates new transaction every time it's acquired
 */
class NoPropagationJdbcTxManager(
    private val dataSource: DataSource
): IJdbcTxManager {

    private val log by LoggerProperty()
    private val txStorage = ThreadLocalTxHolder()

    /**
     * Propagation parameter will be ignored
     */
    override fun getTransaction(def: JdbcTransactionDef): JdbcTransaction {
        val connection = dataSource.connection.apply {
            autoCommit = false
            transactionIsolation = def.isolation.jdbcConst()
        }
        log.info("transaction {} created", def.id)
        return JdbcTransaction(def, connection)
            .apply(txStorage::add)
    }

    override fun commit(tx: JdbcTransaction) {
        tx.commit()
        tx.connection.close()
        txStorage.remove(tx)
        log.info("transaction {} committed", tx.definition.id)
    }

    override fun rollback(tx: JdbcTransaction) {
        tx.rollback()
        tx.connection.close()
        txStorage.remove(tx)
        log.info("transaction {} rolled back", tx.definition.id)
    }
}

class ThreadLocalTxHolder {

    private val threadLocal = ThreadLocal.withInitial { ArrayDeque<JdbcTransaction>() }

    fun getLast(): JdbcTransaction? =
        threadLocal.get().lastOrNull()

    fun remove(tx: JdbcTransaction) =
        threadLocal.get().remove(tx)

    fun add(tx: JdbcTransaction) =
        threadLocal.get().addLast(tx)
}
