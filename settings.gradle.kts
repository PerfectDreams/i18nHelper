pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.perfectdreams.net/")
    }
}

rootProject.name = "i18nHelper"

includeBuild("plugin")
include(":core")
include(":formatters:icu-messageformat-jvm")
include(":formatters:intl-messageformat-js")
include(":example")
