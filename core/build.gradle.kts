plugins {
    `maven-publish`
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm {}

    js {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.2")
                implementation("io.github.microutils:kotlin-logging:2.1.21")
            }
        }
    }
}