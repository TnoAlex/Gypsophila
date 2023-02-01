import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.8.0"
}

group = "priv.alex"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java.sourceCompatibility = JavaVersion.VERSION_11

noArg {
    annotation("priv.alex.annotation.NoArg")
    invokeInitializers = true
}
dependencies {
    testImplementation(kotlin("test"))
    implementation("org.yaml:snakeyaml:1.33")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
    implementation("org.jgrapht:jgrapht-core:1.5.1")
    implementation("com.github.ajalt.clikt:clikt:3.5.1")
    implementation("org.apache.commons:commons-collections4:4.4")
    testImplementation("com.github.vlsi.mxgraph:jgraphx:4.2.2")
    testImplementation("org.jgrapht:jgrapht-ext:1.5.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}