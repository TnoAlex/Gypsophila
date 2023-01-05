import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
}

group = "priv.alex"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java.sourceCompatibility = JavaVersion.VERSION_11

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.yaml:snakeyaml:1.33")
    implementation("org.jgrapht:jgrapht-core:1.5.1")
    implementation("com.github.ajalt.clikt:clikt:3.5.1")
    testImplementation("com.github.vlsi.mxgraph:jgraphx:4.2.2")
    testImplementation("org.jgrapht:jgrapht-ext:1.5.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}