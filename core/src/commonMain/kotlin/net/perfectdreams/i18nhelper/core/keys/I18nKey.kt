package net.perfectdreams.i18nhelper.core.keys

import kotlinx.serialization.Serializable

/**
 * I18nKey represents any internationalization translation key represented by the [key] argument.
 *
 * Because the class is [Serializable], it is possible to use it inside of serializable objects, allowing
 * to share the translation key itself.
 *
 * @see StringI18nKey
 * @see ListI18nKey
 */
@Serializable
sealed class I18nKey {
    abstract val key: String
}