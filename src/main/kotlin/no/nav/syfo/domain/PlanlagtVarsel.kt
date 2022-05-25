package no.nav.syfo.domain

import java.io.Serializable
import java.time.LocalDate

data class PlanlagtVarsel (
    val varselDato: LocalDate,
    val type: VarselType,
    val arbeidstakerFnr: String,
    val orgnummer: String,
) : Serializable
enum class VarselType : Serializable {
    NL_SVAR_MOTEBEHOV
}
