import gg.jte.gradle.GenerateJteTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.21"
    id("com.google.devtools.ksp") version "1.9.21-1.0.16"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.2.1"
    id("gg.jte.gradle") version "3.0.3"
    id("io.micronaut.aot") version "4.2.1"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}

version = "0.1"
group = "cz.glubo"

val kotlinVersion = project.properties.get("kotlinVersion")
repositories {
    mavenCentral()
}

dependencies {
    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.security:micronaut-security-annotations")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.security:micronaut-security-oauth2")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.views:micronaut-views-jte")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("ch.qos.logback.contrib:logback-json-classic:0.1.5")
    runtimeOnly("ch.qos.logback.contrib:logback-jackson:0.1.5")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    aotPlugins(platform("io.micronaut.platform:micronaut-platform:4.2.3"))
    aotPlugins("io.micronaut.security:micronaut-security-aot")
    runtimeOnly("org.yaml:snakeyaml")
    implementation("io.github.serpro69:kotlin-faker:1.15.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.7.3")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
}

application {
    mainClass.set("cz.glubo.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("kotest5")
    processing {
        incremental(true)
        annotations("cz.glubo.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading.set(false)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
        configurationProperties.put("micronaut.security.jwks.enabled", "false")
        configurationProperties.put("micronaut.security.openid-configuration.enabled", "false")
    }
}

jte {
    sourceDirectory.set(file("src/main/jte").toPath())
    generate()
}

// Gradle requires that generateJte is run before some tasks
tasks.configureEach {
    if (name == "inspectRuntimeClasspath") {
        mustRunAfter("generateJte")
    }
}

tasks {
    compileKotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    compileTestKotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

jte {
    generate()
}

tasks.withType<KotlinCompile>().configureEach {
    dependsOn(tasks.withType<GenerateJteTask>())
}
tasks.withType<Jar>().configureEach {
    dependsOn(tasks.withType<GenerateJteTask>())
}
tasks.named("inspectRuntimeClasspath") {
    dependsOn(tasks.withType<GenerateJteTask>())
}
tasks.named("runKtlintFormatOverMainSourceSet") {
    dependsOn(tasks.withType<GenerateJteTask>())
}
tasks.named("runKtlintCheckOverMainSourceSet") {
    dependsOn(tasks.withType<GenerateJteTask>())
}
ktlint {
    version = "1.0.0"
}
