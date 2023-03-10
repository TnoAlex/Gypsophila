import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
}

group = "priv.alex"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.8.0"))
    }
}

java.sourceCompatibility = JavaVersion.VERSION_11


dependencies {
    testImplementation(kotlin("test"))
}


tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}