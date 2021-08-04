package net.perfectdreams.i18nhelper.core.keydata

import net.perfectdreams.i18nhelper.core.keys.StringTranslationKey

open class StringTranslationData(
    val key: StringTranslationKey,
    val arguments: Map<String, Any?>
)