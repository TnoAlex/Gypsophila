import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
}

group = "priv.alex"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

java.sourceCompatibility = JavaVersion.VERSION_17



dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.0-1.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("com.squareup:kotlinpoet-ksp:1.12.0")
}


tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}