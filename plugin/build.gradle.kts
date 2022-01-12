plugins {
    `maven-publish`
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    google()
    gradlePluginPortal()
}

dependencies {
    implementation("com.squareup:kotlinpoet:1.9.0")
    implementation("org.yaml:snakeyaml:1.29")
    implementation("com.ibm.icu:icu4j:69.1")
}

gradlePlugin {
    plugins {
        create("i18nhelper") {
            id = "net.perfectdreams.i18nhelper.plugin"
            implementationClass = "net.perfectdreams.i18nhelper.plugin.I18nHelperPlugin"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

publishing {
    repositories {
        maven {
            name = "PerfectDreams"
            url = uri("https://repo.perfectdreams.net/")
            credentials(PasswordCredentials::class)
        }
    }
}