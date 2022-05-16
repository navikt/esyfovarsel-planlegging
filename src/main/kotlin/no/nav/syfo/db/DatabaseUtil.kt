package no.nav.syfo.db

import no.nav.syfo.db.domain.PPlanlagtVarsel
import java.sql.ResultSet
import kotlin.collections.ArrayList

fun <T> ResultSet.toList(mapper: ResultSet.() -> T) = mutableListOf<T>().apply {
    while (next()) {
        add(mapper())
    }
}

fun ResultSet.toPPlanlagtVarsel() = PPlanlagtVarsel(
    uuid = getString("uuid"),
    fnr = getString("fnr"),
    aktorId = getString("aktor_id"),
    orgnummer = getString("orgnummer"),
    type = getString("type"),
    utsendingsdato = getDate("utsendingsdato").toLocalDate(),
    opprettet = getTimestamp("opprettet").toLocalDateTime(),
    sistEndret = getTimestamp("sist_endret").toLocalDateTime()
)

fun ResultSet.toVarslingIdsListe(): List<String> {
    val rader = ArrayList<String>()
    while (this.next()) {
        rader.add(getString("sykmelding_id"))
    }
    return rader
}

fun ResultSet.toVarslingIdsListeCount(): Int {
    this.last()
    return this.row
}
