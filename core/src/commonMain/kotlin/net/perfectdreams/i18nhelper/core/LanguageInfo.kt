package net.perfectdreams.i18nhelper.core

import kotlinx.serialization.Serializable

@Serializable
data class LanguageInfo(
    val name: String,
    val inheritsFrom: String? = null,
    val formattingLanguageId: String
)