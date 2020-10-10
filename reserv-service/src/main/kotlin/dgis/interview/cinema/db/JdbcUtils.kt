package dgis.interview.cinema.db

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

interface ResultSetReader<T> {
    fun read(resultSet: ResultSet): Collection<T>
}

class RowMapperResultSetReader<T>(
    private val rowMapper: (ResultSet, Int) -> T
): ResultSetReader<T> {

    override fun read(resultSet: ResultSet): Collection<T> {
        var rowCounter = 0
        return generateSequence {
            if (!resultSet.next()) null
            else rowMapper(resultSet, rowCounter++)
        }.toList()
    }
}

fun <T> PreparedStatement.queryOne(rowMapper: (ResultSet) -> T): T? =
    queryList { rs, _ -> rowMapper(rs)}
        .takeIf { it.size == 1 }
        ?.first()

fun PreparedStatement.hasAny(): Boolean {
    return executeQuery().next()
}

fun <T> PreparedStatement.queryList(rowMapper: (ResultSet, Int) -> T): Collection<T> =
    RowMapperResultSetReader(rowMapper).read(this.executeQuery())

/**
 * prepares [PreparedStatement] which returns generated keys
 */
fun Connection.prepareInsertStatement(sql: String): PreparedStatement =
        prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
