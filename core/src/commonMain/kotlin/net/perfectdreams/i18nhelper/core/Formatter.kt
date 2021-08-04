package net.perfectdreams.i18nhelper.core

interface Formatter {
    fun format(message: String, args: Map<String, Any?>): String
}