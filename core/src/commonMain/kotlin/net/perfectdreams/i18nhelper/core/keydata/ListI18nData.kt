package net.perfectdreams.i18nhelper.core.keydata

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.perfectdreams.i18nhelper.core.keys.ListI18nKey

/**
 * Used to store a translation [key] and its [arguments], used for type safe calls in [net.perfectdreams.i18nhelper.core.I18nContext].
 *
 * @param key       the translation string key
 * @param arguments the translation arguments with its values
 */
@Serializable
class ListI18nData(
    override val key: ListI18nKey,
    override val arguments: Map<String, @Serializable(with = ArgumentsSerializer::class) Any?>
) : I18nData()