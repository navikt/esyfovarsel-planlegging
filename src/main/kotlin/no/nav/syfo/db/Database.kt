package no.nav.syfo.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.util.IsolationLevel
import no.nav.syfo.DbEnv
import org.flywaydb.core.Flyway
import java.sql.Connection

const val postgresJdbcPrefix = "jdbc:postgresql"

interface DatabaseInterface {
    val connection: Connection
}

class Database(val env: DbEnv) : DatabaseInterface {

    val hikariDataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl = generateJdbcUrlFromEnv(env)
        username = env.dbUsername
        password = env.dbPassword
        maximumPoolSize = 2
        minimumIdle = 1
        isAutoCommit = false
        transactionIsolation = IsolationLevel.TRANSACTION_READ_COMMITTED.name
        validate()
    })

    init {
        runFlywayMigrations(hikariDataSource)
    }

    override val connection: Connection
        get() = hikariDataSource.connection


    private fun runFlywayMigrations(hikariDataSource: HikariDataSource) = Flyway.configure().run {
        dataSource(hikariDataSource)
        load().migrate().migrationsExecuted
    }
}

fun generateJdbcUrlFromEnv(env: DbEnv): String {
    return "$postgresJdbcPrefix://${env.dbHost}:${env.dbPort}/${env.dbName}"
}
