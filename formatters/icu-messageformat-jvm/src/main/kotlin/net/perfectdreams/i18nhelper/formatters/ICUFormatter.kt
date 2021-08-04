package net.perfectdreams.i18nhelper.formatters

import com.ibm.icu.text.MessageFormat
import net.perfectdreams.i18nhelper.core.Formatter
import java.util.*

class ICUFormatter(private val locale: Locale) : Formatter {
    override fun format(message: String, args: Map<String, Any?>): String {
        // TODO: Cache Message Format
        return MessageFormat(message, locale).format(args)
    }
}