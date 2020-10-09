package dgis.interview.cinema.transaction

interface IJdbcTxManager{
    fun getTransaction(def: JdbcTransactionDef): JdbcTransaction
    fun commit(tx: JdbcTransaction)
    fun rollback(tx: JdbcTransaction)
}