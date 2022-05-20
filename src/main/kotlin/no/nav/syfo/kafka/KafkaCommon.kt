package no.nav.syfo.kafka

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import no.nav.syfo.ApplicationState
import no.nav.syfo.Environment
import org.apache.kafka.clients.CommonClientConfigs.*
import org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.common.config.SaslConfigs.SASL_JAAS_CONFIG
import org.apache.kafka.common.config.SaslConfigs.SASL_MECHANISM
import org.apache.kafka.common.config.SslConfigs.*
import java.util.*

const val topicVarselPlanlegging = "team-esyfo.varsel-planlegging"

const val JAVA_KEYSTORE = "JKS"
const val PKCS12 = "PKCS12"
const val SSL = "SSL"
const val USER_INFO = "USER_INFO"

fun aivenConsumerProperties(env: Environment) : Properties {
    val auth = env.kafkaEnv.auth

    return HashMap<String,String>().apply {
        put(SECURITY_PROTOCOL_CONFIG, SSL)
        put(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "")    // Disable server host name verification
        put(SSL_TRUSTSTORE_TYPE_CONFIG, JAVA_KEYSTORE)
        put(SSL_KEYSTORE_TYPE_CONFIG, PKCS12)
        put(SSL_TRUSTSTORE_LOCATION_CONFIG, auth.truststoreLocation)
        put(SSL_TRUSTSTORE_PASSWORD_CONFIG, auth.credstorePassword)
        put(SSL_KEYSTORE_LOCATION_CONFIG, auth.keystoreLocation)
        put(SSL_KEYSTORE_PASSWORD_CONFIG, auth.credstorePassword)
        put(SSL_KEY_PASSWORD_CONFIG, auth.credstorePassword)
        put(BOOTSTRAP_SERVERS_CONFIG, env.kafkaEnv.broker)
        put(GROUP_ID_CONFIG, "esyfovarsel-planlegging-group")
        put(AUTO_OFFSET_RESET_CONFIG, "earliest")
        put(MAX_POLL_RECORDS_CONFIG, "1")
        put(ENABLE_AUTO_COMMIT_CONFIG, "false")
        put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
        put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
        remove(SASL_MECHANISM)
        remove(SASL_JAAS_CONFIG)
    }.toProperties()
}

interface KafkaListener {
    suspend fun listen(applicationState: ApplicationState)
}

suspend fun CoroutineScope.launchKafkaListener(applicationState: ApplicationState, kafkaListener: KafkaListener) {
    launch {
        try {
            kafkaListener.listen(applicationState)
        } finally {
            applicationState.running = false
        }
    }
}
