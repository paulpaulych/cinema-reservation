package dgis.interview.cinema.db.transaction

import dgis.interview.cinema.LoggerProperty
import javax.sql.DataSource


/**
 * TODO: needs testing
 */
class PropagationSupportedJdbcTxManager(
    private val dataSource: DataSource
): IJdbcTxManager {

    private val log by LoggerProperty()
    private val txStorage = ThreadLocalTxHolder()
    private val depth = ThreadLocal.withInitial{0}

    /**
     * Creates new transaction if {@param propagation} is [Propagation.REQUIRES_NEW]
     * or if isolation level of existing transaction is lower than required by new one
     */
    override fun getTransaction(def: JdbcTransactionDef): JdbcTransaction {
        when (def.propagation) {
            Propagation.USE_EXISTING_STRICT -> {
                val last = txStorage.getLast()
                    ?: error(
                        "cannot init transaction: propagation set as USE_EXISTING_STRICT," +
                                " but no open transactions found"
                    )
                return last.ifIsolatedEnough(def.isolation)
            }
            Propagation.USE_EXISTING -> {
                val last = txStorage.getLast()
                    ?: return createTransaction(def)
                log.info("trying to use existing transaction...")
                return last.ifIsolatedEnough(def.isolation)
            }
            Propagation.REQUIRES_NEW -> return createTransaction(def)
        }
    }

    private fun createTransaction(def: JdbcTransactionDef): JdbcTransaction {
        val connection = dataSource.connection.apply {
            autoCommit = false
            transactionIsolation = def.isolation.jdbcConst()
        }
        log.info("transaction {} created", def.id)
        return JdbcTransaction(def, connection)
            .apply(txStorage::add)
    }

    override fun commit(tx: JdbcTransaction) {
        if(depth.get() > 0){
            depth.set(depth.get() - 1)
        }
        if(depth.get() > 0) return

        tx.commit()
        tx.connection.close()
        txStorage.remove(tx)
        log.info("transaction {} committed", tx.definition.id)
    }

    override fun rollback(tx: JdbcTransaction) {
        if(depth.get() > 0){
            depth.set(depth.get() - 1)
        }
        if(depth.get() > 0) return

        tx.rollback()
        tx.connection.close()
        txStorage.remove(tx)
        log.info("transaction {} rolled back", tx.definition.id)
    }

    private fun JdbcTransaction.ifIsolatedEnough(isolation: Isolation): JdbcTransaction {
        val tx = takeIf{ it.definition.isolation >= isolation}
            ?: error( "cannot init transaction: opened transaction has lower isolation level " +
                    "than new one")
        depth.set(depth.get() + 1)
        return tx
    }
}
