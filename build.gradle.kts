import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.syfo"
version = "1.0"

val kluentVersion = "1.68"
val ktorVersion = "1.6.8"
val prometheusVersion = "0.15.0"
val micrometerVersion = "1.8.4"
val spekVersion = "2.0.18"
val mockkVersion = "1.12.3"
val slf4jVersion = "1.7.36"
val logbackVersion = "1.2.11"
val javaxVersion = "2.1.1"
val logstashEncoderVersion = "7.0.1"
val postgresVersion = "42.3.3"
val hikariVersion = "5.0.1"
val flywayVersion = "7.5.2"
val vaultJdbcVersion = "1.3.9"
val jacksonVersion = "2.13.2"
val jacksonDatabindVersion = "2.13.2.2"
val postgresEmbeddedVersion = "0.13.3"
val kafkaVersion = "2.7.0"
val kafkaEmbeddedVersion = "2.8.1"
val avroVersion = "1.11.0"
val confluentVersion = "6.2.1"

val githubUser: String by project
val githubPassword: String by project

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.5.31"
    id("com.diffplug.gradle.spotless") version "3.18.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

allOpen {
    annotation("no.nav.syfo.annotation.Mockable")
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://packages.confluent.io/maven/")
    maven(url = "https://repo.adeo.no/repository/maven-releases/")
    maven(url = "https://github.com/navikt/vault-jdbc")
}

dependencies {

    // Kotlin / Server
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("javax.ws.rs:javax.ws.rs-api:$javaxVersion")

    // JSON parsing
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonDatabindVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

}

configurations.implementation {
    exclude(group = "com.fasterxml.jackson.module", module = "jackson-module-scala_2.13")
}

tasks {
    create("printVersion") {
        println(project.version)
    }

    withType<ShadowJar> {
        manifest.attributes["Main-Class"] = "no.nav.syfo.BootstrapApplicationKt"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "14"
    }

    withType<Test> {
        useJUnitPlatform {
            includeEngines("spek2")
        }
        testLogging.showStandardStreams = true
    }

}
