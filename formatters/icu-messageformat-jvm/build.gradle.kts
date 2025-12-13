group = "net.perfectdreams.i18nhelper.formatters"

plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":core"))
    implementation("com.ibm.icu:icu4j:70.1")
}

publishing {
    publications {
        register("PerfectDreams", MavenPublication::class.java) {
            from(components["java"])
        }
    }
}