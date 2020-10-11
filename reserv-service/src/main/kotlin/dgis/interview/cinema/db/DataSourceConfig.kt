package dgis.interview.cinema.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dgis.interview.cinema.db.transaction.DB
import dgis.interview.cinema.db.transaction.NoPropagationJdbcTxManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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

