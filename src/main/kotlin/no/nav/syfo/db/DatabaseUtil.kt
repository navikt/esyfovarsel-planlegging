package no.nav.syfo.db

import no.nav.syfo.domain.PPlanlagtVarsel
import java.sql.ResultSet

fun DatabaseInterface.grantAccessToIAMUsers() {
    val statement = """
        GRANT ALL ON ALL TABLES IN SCHEMA PUBLIC TO CLOUDSQLIAMUSER
    """.trimIndent()

    connection.use { conn ->
        conn.prepareStatement(statement).use {
            it.executeUpdate()
        }
        conn.commit()
    }
}

fun <T> ResultSet.toList(mapper: ResultSet.() -> T) = mutableListOf<T>().apply {
    while (next()) {
        add(mapper())
    }
}

fun ResultSet.toPPlanlagtVarsel() = PPlanlagtVarsel(
    uuid = getString("uuid"),
    varselDato = getDate("varseldato").toLocalDate(),
    mottakerFnr = getString("mottaker_fnr"),
    type = getString("type"),
    data = getString("data"),
    opprettet = getTimestamp("opprettet").toLocalDateTime(),
    sistEndret = getTimestamp("sist_endret").toLocalDateTime(),
)
