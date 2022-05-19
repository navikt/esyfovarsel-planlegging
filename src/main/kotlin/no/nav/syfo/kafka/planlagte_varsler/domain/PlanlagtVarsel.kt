package no.nav.syfo.kafka.planlagte_varsler.domain

import java.io.Serializable
import java.time.LocalDate

data class PlanlagtVarsel (
    val varselDato: LocalDate,
    val melding : Melding
) : Serializable

data class Melding(
    val mottakerFnr: String,
    val type: VarselType,
    val data: Any?
) : Serializable

enum class VarselType : Serializable {
    NL_SVAR_MOTEBEHOV
}
