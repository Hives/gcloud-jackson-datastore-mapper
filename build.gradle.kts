import com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"

    id("com.adarshr.test-logger") version "2.0.0"
}

group = "me.hives"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.google.cloud:libraries-bom:16.1.0"))
    implementation("com.google.cloud:google-cloud-datastore")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")

    testImplementation("io.kotest:kotest-runner-junit5:5.0.3")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.0.3")
    testImplementation("io.mockk:mockk:1.12.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    testlogger {
        theme = MOCHA
    }
}
