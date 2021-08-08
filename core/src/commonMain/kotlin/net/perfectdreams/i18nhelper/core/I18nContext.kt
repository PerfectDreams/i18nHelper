package net.perfectdreams.i18nhelper.core

import mu.KotlinLogging
import net.perfectdreams.i18nhelper.core.keydata.ListI18nData
import net.perfectdreams.i18nhelper.core.keydata.StringI18nData
import net.perfectdreams.i18nhelper.core.keys.ListI18nKey
import net.perfectdreams.i18nhelper.core.keys.StringI18nKey

class I18nContext(
    val formatter: Formatter,
    val language: Language
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun get(key: StringI18nKey, arguments: MutableMap<String, Any?>.() -> Unit) = get(key, buildMap(arguments))
    fun get(key: StringI18nKey, arguments: Map<String, Any?> = mapOf()) = get(key.key, arguments)
    @OptIn(ExperimentalStdlibApi::class)
    fun get(key: String, arguments: MutableMap<String, Any?>.() -> Unit) = get(key, buildMap(arguments))
    fun get(key: StringI18nData) = get(key.key.key, key.arguments)

    fun get(key: String, arguments: Map<String, Any?> = mapOf()): String {
        try {
            val content = language.textBundle.strings[key] ?: throw RuntimeException("Key $key doesn't exist in locale!")
            return formatter.format(content, replaceKeysWithMessage(arguments))
        } catch (e: RuntimeException) {
            logger.error(e) { "Error when trying to retrieve $key for locale" }
        }
        return "!!{$key}!!"
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun get(key: ListI18nKey, arguments: MutableMap<String, Any?>.() -> Unit) = get(key, buildMap(arguments))
    fun get(key: ListI18nKey, arguments: Map<String, Any?> = mapOf()) = getList(key.key, arguments)
    fun get(key: ListI18nData) = getList(key.key.key, key.arguments)
    @OptIn(ExperimentalStdlibApi::class)
    fun getList(key: String, arguments: MutableMap<String, Any?>.() -> Unit) = getList(key, buildMap(arguments))

    fun getList(key: String, arguments: Map<String, Any?> = mapOf()): List<String> {
        try {
            val list = language.textBundle.lists[key] ?: throw RuntimeException("Key $key doesn't exist in locale!")
            val newArgs = replaceKeysWithMessage(arguments)
            return list.map { formatter.format(it, newArgs) }
        } catch (e: RuntimeException) {
            logger.error(e) { "Error when trying to retrieve $key for locale" }
        }
        return listOf("!!{$key}!!")
    }

    private fun replaceKeysWithMessage(map: Map<String, Any?>): MutableMap<String, Any?> {
        val newMap = mutableMapOf<String, Any?>()

        for ((key, value) in map) {
            when (value) {
                is StringI18nKey -> {
                    // We will use a copy of the current map but without the "key", to avoid recursion issues
                    newMap[key] = get(value, map.toMutableMap().apply { this.remove(key) })
                }
                is StringI18nData -> {
                    // We will use a copy of the current map but without the "key", to avoid recursion issues
                    newMap[key] = get(value)
                }
                else -> {
                    newMap[key] = value
                }
            }
        }

        return newMap
    }
}