group = "net.perfectdreams.i18nhelper.formatters"

plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":core"))
    implementation("com.ibm.icu:icu4j:69.1")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

publishing {
    publications {
        register("PerfectDreams", MavenPublication::class.java) {
            from(components["java"])
        }
    }
}