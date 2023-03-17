import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.8.0"
    id("com.google.devtools.ksp") version "1.8.0-1.0.9"
    id("com.github.johnrengelman.shadow") version "8.0.0"
    application
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
    implementation("com.github.ajalt.clikt:clikt:3.5.2")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation(project(":klogger"))
    ksp(project(":klogger"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.google.code.gson:gson:2.10.1")
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

val mainClassName = "priv.alex.GypsophilaApplicationKt" // 可执行的主类
tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    manifest {
        attributes["Main-Class"] = mainClassName
    }
    from({
        configurations.runtimeClasspath.get().filter {
            it.name.endsWith("jar")
        }.map { zipTree(it) }
    })
    from(project.sourceSets.main.get().output)
}

tasks.shadowJar {
    dependencies {
        include(project.name)
    }
    mergeServiceFiles()
    val shadowClass = mainClassName.removeSuffix("Kt")
    application {
        mainClass.set(shadowClass)
    }
}