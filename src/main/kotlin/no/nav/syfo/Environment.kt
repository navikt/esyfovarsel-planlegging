package no.nav.syfo
fun getEnv(): Environment {
    return Environment(
            AppEnv(
                applicationPort = getEnvVar("APPLICATION_PORT", "8080").toInt(),
                applicationThreads = getEnvVar("APPLICATION_THREADS", "4").toInt(),
                remote = true
            ),
            KafkaEnv(
                broker = getEnvVar("KAFKA_BROKERS"),
                auth = KafkaAuth(
                    truststoreLocation = getEnvVar("KAFKA_TRUSTSTORE_PATH"),
                    keystoreLocation = getEnvVar("KAFKA_KEYSTORE_PATH"),
                    credstorePassword = getEnvVar("KAFKA_CREDSTORE_PASSWORD")
                )
            )
        )
}

data class Environment(
    val appEnv: AppEnv,
    val kafkaEnv: KafkaEnv
)
data class AppEnv(
    val applicationPort: Int,
    val applicationThreads: Int,
    val remote: Boolean = false
)
data class KafkaEnv(
    var broker: String,
    val auth: KafkaAuth
)
data class KafkaAuth(
    val truststoreLocation: String,
    val keystoreLocation: String,
    val credstorePassword: String
)
fun getEnvVar(varName: String, defaultValue: String? = null) =
    System.getenv(varName) ?: defaultValue ?: throw RuntimeException("Missing required variable \"$varName\"")
