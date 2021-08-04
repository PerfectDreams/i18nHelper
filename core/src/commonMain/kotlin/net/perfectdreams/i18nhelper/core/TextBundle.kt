package net.perfectdreams.i18nhelper.core

import kotlinx.serialization.Serializable

@Serializable
class TextBundle(
    // We use separate entries for strings and lists, to be easier for kotlinx.serialization to serialize/deserialize them
    val strings: Map<String, String>,
    val lists: Map<String, List<String>>
)