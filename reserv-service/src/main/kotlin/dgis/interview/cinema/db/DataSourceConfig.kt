package dgis.interview.cinema.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dgis.interview.cinema.transaction.IJdbcTxManager
import dgis.interview.cinema.transaction.Isolation
import dgis.interview.cinema.transaction.JdbcTransactionDef
import dgis.interview.cinema.transaction.Propagation
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.sql.Connection
import javax.sql.DataSource

@Configuration
class DataSourceConfig(
    @Value("\${spring.datasource.url}")
    private val url: String,
    @Value("\${spring.datasource.username}")
    private val username: String,
    @Value("\${spring.datasource.password}")
    private val password: String,
    @Value("\${spring.datasource.driver-class-name}")
    private val driverClassName: String
) {

    @Bean
    fun dataSource(): DataSource {
        val conf = HikariConfig().apply {
            jdbcUrl = url
            username = this@DataSourceConfig.username
            password = this@DataSourceConfig.password
            driverClassName = this@DataSourceConfig.driverClassName
        }
        return HikariDataSource(conf)
    }
    
    @Bean
    fun db(dataSource: DataSource): DB =
        DB(NoPropagationJdbcTxManager(dataSource))
}

class DB(
    private val transactionManager: IJdbcTxManager
){

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