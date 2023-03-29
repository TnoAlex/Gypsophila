import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "8.0.0"
}

group = "priv.alex"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.8.0"))
        classpath("gradle.plugin.com.github.johnrengelman:shadow:8.0.0")
    }
}

java.sourceCompatibility = JavaVersion.VERSION_17


tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

