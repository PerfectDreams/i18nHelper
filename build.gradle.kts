plugins {
    `maven-publish`
    kotlin("multiplatform") version "2.2.21" apply false
    kotlin("plugin.serialization") version "2.2.21" apply false
}

allprojects {
    group = "net.perfectdreams.i18nhelper"
    version = "0.0.6"

    repositories {
        mavenCentral()
        maven("https://repo.perfectdreams.net/")
    }
}

subprojects {
    apply<MavenPublishPlugin>()

    publishing {
        repositories {
            maven {
                name = "PerfectDreams"
                url = uri("https://repo.perfectdreams.net/")
                credentials(PasswordCredentials::class)
            }
        }
    }
}