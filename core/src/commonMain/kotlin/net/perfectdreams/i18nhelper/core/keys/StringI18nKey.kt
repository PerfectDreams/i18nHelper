package net.perfectdreams.i18nhelper.core.keys

import kotlinx.serialization.Serializable
import net.perfectdreams.i18nhelper.core.keydata.ArgumentSurrogate

/**
 * Used to store a translation [key], used for type safe calls in [net.perfectdreams.i18nhelper.core.I18nContext].
 *
 * Because the class is [Serializable], it is possible to use it inside of serializable objects, allowing
 * to share the translation key itself.
 *
 * Keep in mind that there is caveats: Because the [arguments] map is a [Any], not all arguments will be
 * successfully serializable! Check the [ArgumentSurrogate] classes to know what types are supported.
 *
 * @param key the translation string key
 * @see ListI18nKey
 * @see I18nKey
 */
@Serializable
class StringI18nKey(override val key: String) : I18nKey()