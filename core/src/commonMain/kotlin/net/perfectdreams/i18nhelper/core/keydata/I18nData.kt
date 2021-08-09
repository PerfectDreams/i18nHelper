package net.perfectdreams.i18nhelper.core.keydata

import kotlinx.serialization.Serializable
import net.perfectdreams.i18nhelper.core.keys.I18nKey

/**
 * Used to store a translation [key] and its [arguments], used for type safe calls in [net.perfectdreams.i18nhelper.core.I18nContext].
 *
 * Because the class is [Serializable], it is possible to use it inside of serializable objects, allowing
 * to share the translation key itself.
 *
 * Keep in mind that there is caveats: Because the [arguments] map is a [Any], not all arguments will be
 * successfully serializable! Check the [ArgumentSurrogate] classes to know what types are supported.
 *
 * @param key       the translation string key
 * @param arguments the translation arguments with its values
 */
@Serializable
sealed class I18nData {
    abstract val key: I18nKey
    abstract val arguments: Map<String, @Serializable(with = ArgumentsSerializer::class) Any?>
}