package net.perfectdreams.i18nhelper.core.keys

import kotlinx.serialization.Serializable

/**
 * StringI18nKey is used to store a translation [key], used for type safe calls in [net.perfectdreams.i18nhelper.core.I18nContext].
 *
 * Because the class is [Serializable], it is possible to use it inside of serializable objects, allowing
 * to share the translation key itself.
 *
 * @param key the translation string key
 * @see ListI18nKey
 * @see I18nKey
 */
@Serializable
class StringI18nKey(override val key: String) : I18nKey()