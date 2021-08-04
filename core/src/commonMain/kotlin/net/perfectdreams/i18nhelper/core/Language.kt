package net.perfectdreams.i18nhelper.core

import kotlinx.serialization.Serializable

@Serializable
class Language(
    val info: LanguageInfo,
    val textBundle: TextBundle
)