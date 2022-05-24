package no.nav.syfo.db

import no.nav.syfo.domain.Melding
import no.nav.syfo.domain.PPlanlagtVarsel
import no.nav.syfo.domain.PlanlagtVarsel
import no.nav.syfo.domain.VarselType
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
    varselDato = getDate("varsel_dato").toLocalDate(),
    mottakerFnr = getString("mottaker_fnr"),
    type = getString("type"),
    data = getString("data"),
    json = getString("json"),
    opprettet = getTimestamp("opprettet").toLocalDateTime(),
    sistEndret = getTimestamp("sist_endret").toLocalDateTime(),
)
