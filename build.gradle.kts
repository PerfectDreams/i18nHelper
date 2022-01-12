plugins {
    `maven-publish`
    kotlin("multiplatform") version "1.5.21" apply false
    kotlin("plugin.serialization") version "1.5.21" apply false
}

allprojects {
    group = "net.perfectdreams.i18nhelper"
    version = "0.0.3-SNAPSHOT"

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

                credentials {
                    username = System.getProperty("USERNAME") ?: System.getenv("USERNAME")
                    password = System.getProperty("PASSWORD") ?: System.getenv("PASSWORD")
                }
            }
        }
    }
}