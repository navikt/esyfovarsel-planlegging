package no.nav.syfo.domain

import java.io.Serializable
import java.time.LocalDate

data class PlanlagtVarsel (
    val varselDato: LocalDate,
    val melding : Melding
) : Serializable

data class Melding(
    val mottakerFnr: String,
    val type: VarselType,
    val data: String?
) : Serializable

enum class VarselType : Serializable {
    NL_SVAR_MOTEBEHOV
}
