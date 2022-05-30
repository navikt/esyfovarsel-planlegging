package no.nav.syfo.kafka.planlagte_varsler

import kotlinx.coroutines.delay
import no.nav.syfo.ApplicationState
import no.nav.syfo.Environment
import no.nav.syfo.kafka.KafkaListener
import no.nav.syfo.kafka.aivenConsumerProperties
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
    private val className = PlanlagteVarslerKafkaConsumer::class.simpleName
    private val log: Logger = LoggerFactory.getLogger(className)
    private val kafkaListener: KafkaConsumer<String, String>
    init {
        val kafkaConfig = aivenConsumerProperties(env)
        kafkaListener = KafkaConsumer(kafkaConfig)
        kafkaListener.subscribe(listOf(topicVarselPlanlegging))
    }

    override suspend fun listen(applicationState: ApplicationState) {
        log.info("$className: Started listening to topic $topicVarselPlanlegging")
        while (applicationState.running) {
            kafkaListener.poll(Duration.ofMillis(0)).forEach {
                log.info("$className: Received record from topic: [$topicVarselPlanlegging]")
                try {
                    planlagtVarselService.createOrUpdate(it.value())
                } catch (e: IOException) {
                    log.error(
                        "$className: Error in [$topicVarselPlanlegging] listener: Could not parse message | ${e.message}",
                        e
                    )
                }
                kafkaListener.commitSync()
                delay(10)
            }
        }
        log.info("$className: Stopped listening to $topicVarselPlanlegging")
    }


}
