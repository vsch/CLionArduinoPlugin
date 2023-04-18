@file:Suppress("PublicApiImplicitType")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

val javaVersion = "11"
val flexmarkVersion = "0.64.0"
val pluginSinceBuild = "203"
val pluginUntilBuild = ""
val pluginVersion = "1.5.35"

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.vladsch.clionarduinoplugin"
version = pluginVersion

repositories {
    mavenLocal()
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2020.3")
    type.set("CL") // Target IDE Platform

    plugins.set(listOf("com.intellij.clion", "com.intellij.cidr.base"))
}

dependencies {
    annotationProcessor("junit:junit:4.13.2")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.vladsch.flexmark:flexmark-test-util:$flexmarkVersion")

    // for newer versions of CLion need to include the older clion.jar
    // compileOnly(files("excluded_lib/clion.jar"))

    implementation("com.vladsch.flexmark:flexmark:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-util:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-tree-iteration:$flexmarkVersion")

    implementation("org.jetbrains:annotations:24.0.1")
    implementation("commons-io:commons-io:2.7")
    implementation("org.scream3r:jssc:2.8.0")

    implementation(files("lib/plugin-util.jar"))
}

sourceSets {
    main {
        java {
            setSrcDirs(mutableListOf("src"))
            resources.setSrcDirs(mutableListOf("resources"))
        }

        kotlin {
            setSrcDirs(mutableListOf("src"))
        }
    }

    test {
        java {
            setSrcDirs(mutableListOf("test"))
            resources.setSrcDirs(mutableListOf("test-resources", "test"))
        }

        kotlin {
            setSrcDirs(mutableListOf("test"))
        }
    }
}

tasks { // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion
    }

    patchPluginXml {
        sinceBuild.set(pluginSinceBuild)
        untilBuild.set(pluginUntilBuild)
        version.set(pluginVersion)
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
