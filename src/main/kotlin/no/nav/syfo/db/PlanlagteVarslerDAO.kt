package no.nav.syfo.db

import no.nav.syfo.domain.PPlanlagtVarsel
import no.nav.syfo.domain.PlanlagtVarsel
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

fun DatabaseInterface.storePlanlagtVarsel(planlagtVarsel: PlanlagtVarsel): UUID {

    val insertStatement1 = """INSERT INTO PLANLAGTE_VARSLER (
        uuid,
        varsel_dato
        mottaker_fnr,
        type,
        data,                        
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
            it.setTimestamp(6, now)
            it.setTimestamp(7, now)
            it.executeUpdate()
        }
        connection.commit()
    }
    return varselUUID
}

fun DatabaseInterface.fetchPlanlagtVarselByFnrAndType(fnr: String, type: String): PPlanlagtVarsel? {
    val queryStatement = """SELECT *
                            FROM PLANLAGTE_VARSLER
                            WHERE mottaker_fnr = ? and type = ?
    """.trimIndent()

    val varsler = connection.use { connection ->
        connection.prepareStatement(queryStatement).use {
            it.setString(1, fnr)
            it.setString(2, type)
            it.executeQuery().toList { toPPlanlagtVarsel() }
        }
    }

    if (varsler.isEmpty())
        return null
    return varsler.first()
}

fun DatabaseInterface.updateVarseldato(uuid: String, varseldato: LocalDate) {
    val updateStatement = """UPDATE PLANLAGTE_VARSLER
                            SET varsel_dato = ?
                                sist_endret = ?
                            WHERE uuid = ?
    """.trimIndent()

    val now = Timestamp.valueOf(LocalDateTime.now())

    connection.use { connection ->
        connection.prepareStatement(updateStatement).use {
            it.setDate(1, Date.valueOf(varseldato))
            it.setTimestamp(2, now)
            it.setObject(3, UUID.fromString(uuid))
            it.executeUpdate()
        }
        connection.commit()
    }
}