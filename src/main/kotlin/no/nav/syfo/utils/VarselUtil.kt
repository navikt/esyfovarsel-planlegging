package no.nav.syfo.utils

import no.nav.syfo.db.DatabaseInterface
import no.nav.syfo.db.domain.PPlanlagtVarsel
import no.nav.syfo.db.domain.VarselType
import no.nav.syfo.db.fetchPlanlagtVarselByFnr
import no.nav.syfo.db.fetchSykmeldingerIdByPlanlagtVarselsUUID
import java.time.LocalDate

val antallUker39UkersVarsel = 39L
val antallDager39UkersVarsel = antallUker39UkersVarsel * 7L + 1

class VarselUtil(private val databaseAccess: DatabaseInterface) {
    fun isVarselDatoForIDag(varselDato: LocalDate): Boolean {
        return varselDato.isBefore(LocalDate.now())
    }

    fun isVarselDatoEtterTilfelleSlutt(varselDato: LocalDate, tilfelleSluttDato: LocalDate): Boolean {
        return tilfelleSluttDato.isEqual(varselDato) || varselDato.isAfter(tilfelleSluttDato)
    }

    fun getPlanlagteVarslerAvType(fnr: String, varselType: VarselType): List<PPlanlagtVarsel> {
        return databaseAccess.fetchPlanlagtVarselByFnr(fnr)
            .filter { it.type == varselType.name }
    }

    fun hasLagreteVarslerForForespurteSykmeldinger(planlagteVarsler: List<PPlanlagtVarsel>, ressursIds: Set<String>): Boolean {
        val gjeldendeSykmeldinger = mutableSetOf<Set<String>>()
        for (v: PPlanlagtVarsel in planlagteVarsler) {
            val sm = databaseAccess.fetchSykmeldingerIdByPlanlagtVarselsUUID(v.uuid)
            gjeldendeSykmeldinger.add(sm.toSet())
        }
        val gjeldendeSykmeldingerSet = gjeldendeSykmeldinger.flatten().toSet()
        return (ressursIds intersect gjeldendeSykmeldingerSet).isNotEmpty()
    }
}
