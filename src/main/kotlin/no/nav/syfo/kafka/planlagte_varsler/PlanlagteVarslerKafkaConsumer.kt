package no.nav.syfo.kafka.planlagte_varsler

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.delay
import no.nav.syfo.ApplicationState
import no.nav.syfo.Environment
import no.nav.syfo.kafka.KafkaListener
import no.nav.syfo.kafka.aivenConsumerProperties
import no.nav.syfo.kafka.planlagte_varsler.domain.PlanlagtVarsel
import no.nav.syfo.kafka.topicVarselPlanlegging
import no.nav.syfo.service.PlanlagtVarselService
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.time.Duration

class PlanlagteVarslerKafkaConsumer(
    env: Environment,
    val planlagtVarselService: PlanlagtVarselService
): KafkaListener {
    private val log: Logger = LoggerFactory.getLogger("no.nav.syfo.kafka.OppfolgingstilfelleConsumer")
    private val kafkaListener: KafkaConsumer<String, String>
    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerKotlinModule()
        registerModule(JavaTimeModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
    }

    init {
        val kafkaConfig = aivenConsumerProperties(env)
        kafkaListener = KafkaConsumer(kafkaConfig)
        kafkaListener.subscribe(listOf(topicVarselPlanlegging))
    }

    override suspend fun listen(applicationState: ApplicationState) {
        log.info("Started listening to topic $topicVarselPlanlegging")
        while (applicationState.running) {
            kafkaListener.poll(Duration.ofMillis(0)).forEach {
                log.info("Received record from topic: [$topicVarselPlanlegging]")
                try {
                    val varsel: PlanlagtVarsel = objectMapper.readValue(it.value())
                    // TODO: FJERN FØR PRODSETTING!
                    log.info("CONTENT: $varsel")
                    planlagtVarselService.createOrUpdate(varsel)
                } catch (e: IOException) {
                    log.error(
                        "Error in [$topicVarselPlanlegging] listener: Could not parse message | ${e.message}",
                        e
                    )
                }
                kafkaListener.commitSync()
                delay(10)
            }
        }
        log.info("Stopped listening to $topicVarselPlanlegging")
    }


}
