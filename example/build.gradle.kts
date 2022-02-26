group = "net.perfectdreams.i18nhelper.formatters"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("net.perfectdreams.i18nhelper.plugin")
}

dependencies {
    api(project(":formatters:icu-messageformat-jvm"))
    implementation("com.ibm.icu:icu4j:70.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
}

val generateI18nKeys = tasks.register<net.perfectdreams.i18nhelper.plugin.GenerateI18nKeysTask>("generateI18nKeys") {
    languageSourceFolder.set(file("src/main/resources/languages/en/"))
    languageTargetFolder.set(file("$buildDir/generated/languages"))
    generatedPackage.set("net.perfectdreams.i18nhelper.example")
    translationLoadTransform.set { file, map ->
        if (file.parentFile.name == "subfolder")
            mapOf("subfolder" to map)
        else
            map
    }

}

sourceSets.main {
    java.srcDir(generateI18nKeys.get().languageTargetFolder)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}