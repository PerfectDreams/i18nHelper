group = "net.perfectdreams.i18nhelper.formatters"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("net.perfectdreams.i18nhelper.plugin")
}

dependencies {
    api(project(":formatters:icu-messageformat-jvm"))
    implementation("com.ibm.icu:icu4j:69.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
}

i18nHelper {
    languageSourceFolder.set("src/main/resources/languages/en/")
    generatedPackage.set("net.perfectdreams.i18nhelper.example")
    translationLoadTransform.set { file, map ->
        if (file.parentFile.name == "subfolder")
            mapOf("subfolder" to map)
        else
            map
    }
}

sourceSets.main {
    java.srcDir("build/generated/languages")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}