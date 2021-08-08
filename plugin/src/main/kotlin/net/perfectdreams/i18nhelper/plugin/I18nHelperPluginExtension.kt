package net.perfectdreams.i18nhelper.plugin

import org.gradle.api.provider.Property
import java.io.File

interface I18nHelperPluginExtension {
    val generatedPackage: Property<String>
    val languageSourceFolder: Property<String>
    val translationLoadTransform: Property<LoadTransformCallback>
}

typealias LoadTransformCallback = (File, Map<String, Any>) -> (Map<String, Any>)