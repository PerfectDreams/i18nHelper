group = "net.perfectdreams.i18nhelper.formatters"

plugins {
    kotlin("multiplatform")
}

kotlin {
    js(BOTH) {
        browser()
        nodejs()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                api(project(":core"))

                // Used for message formatting
                implementation(npm("intl-messageformat", "9.8.1"))
            }
        }
    }
}