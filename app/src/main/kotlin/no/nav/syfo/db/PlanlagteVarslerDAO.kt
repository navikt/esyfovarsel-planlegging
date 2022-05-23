package no.nav.syfo.db

import no.nav.syfo.domain.PlanlagtVarsel
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

fun DatabaseInterface.storePlanlagtVarsel(planlagtVarsel: PlanlagtVarsel, planlagtVarselJSON: String): UUID {

    // TODO: Bør orgnummer ligge utenfor 'data'-feltet i JSON (e.g. obligatorisk felt i alle varsel-meldinger i varsel-planlegger-topic) ?

    // TODO: json kan alternativt lagres i egen tabell
    val insertStatement1 = """INSERT INTO PLANLAGTE_VARSLER (
        uuid,
        varsel_dato
        mottaker_fnr,
        type,
        data,
        json,                           
        opprettet,
        sist_endret) VALUES (?, ?, ?, ?, ?, ?, ?)""".trimIndent()

    val now = Timestamp.valueOf(LocalDateTime.now())
    val varselUUID = UUID.randomUUID()

    connection.use { connection ->
        connection.prepareStatement(insertStatement1).use {
            it.setObject(1, varselUUID)
            it.setDate(2, Date.valueOf(planlagtVarsel.varselDato))
            it.setString(3, planlagtVarsel.melding.mottakerFnr)
            it.setString(4, planlagtVarsel.melding.data)
            it.setString(5, planlagtVarselJSON)
            it.setTimestamp(6, now)
            it.setTimestamp(7, now)
            it.executeUpdate()
        }
        connection.commit()
    }
    return varselUUID
}
