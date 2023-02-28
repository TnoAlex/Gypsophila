import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.8.0"
    id("com.google.devtools.ksp") version "1.8.0-1.0.9"
}

group = "priv.alex"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java.sourceCompatibility = JavaVersion.VERSION_17

noArg {
    annotation("priv.alex.core.NoArg")
    invokeInitializers = true
}


dependencies {
    testImplementation(kotlin("test"))
    implementation("org.yaml:snakeyaml:2.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
    implementation("org.jgrapht:jgrapht-core:1.5.1")
    implementation("com.github.ajalt.clikt:clikt:3.5.1")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation(project(":klogger"))
    ksp(project(":klogger"))
    testImplementation("com.github.vlsi.mxgraph:jgraphx:4.2.2")
    testImplementation("org.jgrapht:jgrapht-ext:1.5.1")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}