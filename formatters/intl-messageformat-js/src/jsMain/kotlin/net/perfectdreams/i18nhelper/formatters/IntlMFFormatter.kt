package net.perfectdreams.i18nhelper.formatters

import net.perfectdreams.i18nhelper.core.Formatter

class IntlMFFormatter : Formatter {
    override fun format(message: String, args: Map<String, Any?>): String {
        // TODO: Cache
        val mf = IntlMessageFormat(message, "en-US")

        return mf.format(
            jsObject {
                for (arg in args) {
                    this[arg.key] = arg.value
                }
            }
        )
    }

    // From Jetpack Compose Web
    @Suppress("NOTHING_TO_INLINE")
    private inline fun <T : Any> jsObject(): T =
        js("({})")

    private inline fun <T : Any> jsObject(builder: T.() -> Unit): T =
        jsObject<T>().apply(builder)
}