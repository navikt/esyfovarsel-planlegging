package no.nav.syfo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

const val localAppPropertiesPath = "./src/main/resources/localEnvApp.json"
const val localJobPropertiesPath = "./src/main/resources/localEnvJob.json"
const val serviceuserMounthPath = "/var/run/secrets"
val objectMapper = ObjectMapper().registerKotlinModule()


fun getEnv(): Environment {
    return if (isLocal())
        getTestEnv()
    else
        Environment(
            AppEnv(
                applicationPort = getEnvVar("APPLICATION_PORT", "8080").toInt(),
                applicationThreads = getEnvVar("APPLICATION_THREADS", "4").toInt(),
                remote = true,
                runningInGCPCluster = isGCP()
            ),
            AuthEnv(
                clientId = getEnvVar("AZURE_APP_CLIENT_ID"),
                clientSecret = getEnvVar("AZURE_APP_CLIENT_SECRET"),
                aadAccessTokenUrl = getEnvVar("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT"),
                loginserviceDiscoveryUrl = getEnvVar("LOGINSERVICE_IDPORTEN_DISCOVERY_URL"),
                loginserviceAudience = getEnvVar("LOGINSERVICE_IDPORTEN_AUDIENCE").split(",")
            ),
            UrlEnv(
                syfosmregisterUrl = getEnvVar("SYFOSMREGISTER_URL"),
                syfosmregisterScope = getEnvVar("SYFOSMREGISTER_SCOPE"),
            ),
            KafkaEnv(
                aivenBroker = getEnvVar("KAFKA_BROKERS"),
                KafkaSslEnv(
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
            ),
            ToggleEnv(
                sendMerVeiledningVarsler = getBooleanEnvVar("TOGGLE_SEND_MERVEILEDNING_VARSLER"),
                sendAktivitetskravVarsler = getBooleanEnvVar("TOGGLE_SEND_AKTIVITETSKRAV_VARSLER")
            )
        )
}

fun getTestEnv() =
    objectMapper.readValue(File(localAppPropertiesPath), Environment::class.java)

data class Environment(
    val appEnv: AppEnv,
    val authEnv: AuthEnv,
    val urlEnv: UrlEnv,
    val kafkaEnv: KafkaEnv,
    val dbEnv: DbEnv,
    val toggleEnv: ToggleEnv
)

data class AppEnv(
    val applicationPort: Int,
    val applicationThreads: Int,
    val remote: Boolean = false,
    val runningInGCPCluster: Boolean
)

data class AuthEnv(
    val clientId: String,
    val clientSecret: String,
    val aadAccessTokenUrl: String,
    val loginserviceDiscoveryUrl: String,
    val loginserviceAudience: List<String>
)

data class UrlEnv(
    val syfosmregisterUrl: String,
    val syfosmregisterScope: String,
)

data class KafkaEnv(
    val aivenBroker: String,
    val sslConfig: KafkaSslEnv
)

data class KafkaSslEnv(
    val truststoreLocation: String,
    val keystoreLocation: String,
    val credstorePassword: String
)

data class DbEnv(
    var dbHost: String,
    var dbPort: String,
    var dbName: String,
    val dbUsername: String = "",
    val dbPassword: String = "",
)

data class ToggleEnv(
    val sendMerVeiledningVarsler: Boolean,
    val sendAktivitetskravVarsler: Boolean
)

fun getEnvVar(varName: String, defaultValue: String? = null) =
    System.getenv(varName) ?: defaultValue ?: throw RuntimeException("Missing required variable \"$varName\"")

fun isGCP(): Boolean = getEnvVar("NAIS_CLUSTER_NAME").contains("gcp")

fun isLocal(): Boolean = getEnvVar("KTOR_ENV", "local") == "local"

fun isJob(): Boolean = getBooleanEnvVar("JOB")

fun getBooleanEnvVar(varName: String) = System.getenv(varName).toBoolean()
