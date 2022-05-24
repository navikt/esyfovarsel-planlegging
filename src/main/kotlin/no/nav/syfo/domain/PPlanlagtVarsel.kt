package no.nav.syfo.domain

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

data class PPlanlagtVarsel (
    val uuid: String,
    val varselDato: LocalDate,
    val mottakerFnr: String,
    val type: String,
    val data: String?,
    val opprettet: LocalDateTime,
    val sistEndret: LocalDateTime
) : Serializable
