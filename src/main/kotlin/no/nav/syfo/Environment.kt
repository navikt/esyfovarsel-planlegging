package no.nav.syfo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

const val localAppPropertiesPath = "./src/main/resources/localEnvApp.json"
val objectMapper = ObjectMapper().registerKotlinModule()


fun getEnv(): Environment {
    return if (isLocal())
        getTestEnv()
    else
        Environment(
            AppEnv(
                applicationPort = getEnvVar("APPLICATION_PORT", "8080").toInt(),
                applicationThreads = getEnvVar("APPLICATION_THREADS", "4").toInt(),
                remote = true
            )
        )
}

fun getTestEnv() =
    objectMapper.readValue(File(localAppPropertiesPath), Environment::class.java)

data class Environment(
    val appEnv: AppEnv
)

data class AppEnv(
    val applicationPort: Int,
    val applicationThreads: Int,
    val remote: Boolean = false
)




fun getEnvVar(varName: String, defaultValue: String? = null) =
    System.getenv(varName) ?: defaultValue ?: throw RuntimeException("Missing required variable \"$varName\"")

fun isGCP(): Boolean = getEnvVar("NAIS_CLUSTER_NAME").contains("gcp")

fun isLocal(): Boolean = getEnvVar("KTOR_ENV", "local") == "local"

fun isJob(): Boolean = getBooleanEnvVar("JOB")

fun getBooleanEnvVar(varName: String) = System.getenv(varName).toBoolean()
