package dgis.interview.cinema.db.transaction

interface IJdbcTxManager{
    fun getTransaction(def: JdbcTransactionDef): JdbcTransaction
    fun commit(tx: JdbcTransaction)
    fun rollback(tx: JdbcTransaction)
}