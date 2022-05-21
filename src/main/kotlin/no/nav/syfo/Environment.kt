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
            ),
            DbEnv(
                dbHost = getEnvVar("GCP_DB_HOST", "127.0.0.1"),
                dbPort = getEnvVar("GCP_DB_PORT", "5432"),
                dbName = getEnvVar("GCP_DB_DATABASE"),
                dbUsername = getEnvVar("GCP_DB_USERNAME"),
                dbPassword = getEnvVar("GCP_DB_PASSWORD")
            )
        )
}

data class Environment(
    val appEnv: AppEnv,
    val kafkaEnv: KafkaEnv,
    val dbEnv: DbEnv
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
data class DbEnv(
    val dbHost: String,
    val dbPort: String,
    val dbName: String,
    val dbUsername: String,
    val dbPassword: String
)
data class KafkaAuth(
    val truststoreLocation: String,
    val keystoreLocation: String,
    val credstorePassword: String
)
fun getEnvVar(varName: String, defaultValue: String? = null) =
    System.getenv(varName) ?: defaultValue ?: throw RuntimeException("Missing required variable \"$varName\"")
