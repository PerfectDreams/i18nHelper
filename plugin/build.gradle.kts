plugins {
    `maven-publish`
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "net.perfectdreams.i18nhelper"
version = "0.0.7"

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.squareup:kotlinpoet:2.2.0")
    implementation("org.yaml:snakeyaml:2.5")
    implementation("com.ibm.icu:icu4j:78.1")
}

gradlePlugin {
    plugins {
        create("i18nhelper") {
            id = "net.perfectdreams.i18nhelper.plugin"
            implementationClass = "net.perfectdreams.i18nhelper.plugin.I18nHelperPlugin"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

publishing {
    repositories {
        maven {
            name = "PerfectDreamsPlugin"
            url = uri("https://repo.perfectdreams.net/")
            credentials(PasswordCredentials::class)
        }
    }
}