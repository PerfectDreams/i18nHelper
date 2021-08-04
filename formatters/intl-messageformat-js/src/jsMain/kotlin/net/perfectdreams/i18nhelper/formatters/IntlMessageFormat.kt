@file:JsModule("intl-messageformat")
@file:JsNonModule
package net.perfectdreams.i18nhelper.formatters

@JsName("default")
external class IntlMessageFormat(
    message: String,
    locales: String = definedExternally,
    overrideFormats: Any = definedExternally,
    opts: Any = definedExternally
) {
    fun format(values: dynamic): String
}