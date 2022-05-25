package no.nav.syfo.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.syfo.db.*
import no.nav.syfo.domain.PlanlagtVarsel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate

class PlanlagtVarselService(
    val database: DatabaseInterface
) {
    private val className = PlanlagtVarselService::class.java
    private val log: Logger = LoggerFactory.getLogger(className)

    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerKotlinModule()
        registerModule(JavaTimeModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
    }
    fun createOrUpdate(varselJSON: String) {
        val planlagtVarsel: PlanlagtVarsel = objectMapper.readValue(varselJSON)

        val previousVarsel = database.findDuplicateEntry("${planlagtVarsel.type}", planlagtVarsel.arbeidstakerFnr, planlagtVarsel.orgnummer)
        previousVarsel?.let {
            if (planlagtVarsel.varselDato.isBefore(LocalDate.now())) {
                log.warn("$className: Varsel date ${planlagtVarsel.varselDato} is before today, skipping varselplanning")
                return
            }
            if (planlagtVarsel.varselDato.isEqual(previousVarsel.varselDato)) {
                log.warn("$className: Found duplicate varsel with UUID ${previousVarsel.uuid}")
                return
            }
            log.info("$className: Updating new varsel date for varsel with UUID ${previousVarsel.uuid}")
            database.updateVarseldato(previousVarsel.uuid, planlagtVarsel.varselDato)
        } ?: run {
            val storedUUID = database.storePlanlagtVarsel(planlagtVarsel)
            log.info("$className: Stored 'PLANLAGT VARSEL' with UUID $storedUUID to DB")
        }
    }
}
