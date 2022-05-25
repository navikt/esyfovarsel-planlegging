package no.nav.syfo.domain

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

data class PPlanlagtVarsel (
    val uuid: String,
    val varselDato: LocalDate,
    val type: String,
    val arbeidstakerFnr: String,
    val orgnummer: String,
    val opprettet: LocalDateTime,
    val sistEndret: LocalDateTime
) : Serializable
