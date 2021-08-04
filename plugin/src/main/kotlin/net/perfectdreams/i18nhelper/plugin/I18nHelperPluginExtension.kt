package net.perfectdreams.i18nhelper.plugin

import org.gradle.api.provider.Property

interface I18nHelperPluginExtension {
    val generatedPackage: Property<String>
    val languageSourceFolder: Property<String>
}