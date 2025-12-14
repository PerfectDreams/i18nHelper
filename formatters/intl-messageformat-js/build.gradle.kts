group = "net.perfectdreams.i18nhelper.formatters"

plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        browser()
        nodejs()
    }

    sourceSets {
        jsMain {
            dependencies {
                api(project(":core"))

                // Used for message formatting
                implementation(npm("intl-messageformat", "10.7.18"))
            }
        }
    }
}