package net.perfectdreams.i18nhelper.core.keydata

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

internal object ArgumentsSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ArgumentsAny", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Any) {
        when (value) {
            is Int -> {
                val surrogate = IntArgumentSurrogate(value)
                encoder.encodeSerializableValue(ArgumentSurrogate.serializer(), surrogate)
            }
            is Long -> {
                val surrogate = LongArgumentSurrogate(value)
                encoder.encodeSerializableValue(ArgumentSurrogate.serializer(), surrogate)
            }
            is Double -> {
                val surrogate = DoubleArgumentSurrogate(value)
                encoder.encodeSerializableValue(ArgumentSurrogate.serializer(), surrogate)
            }
            is String -> {
                val surrogate = StringArgumentSurrogate(value)
                encoder.encodeSerializableValue(ArgumentSurrogate.serializer(), surrogate)
            }
            else -> error("I don't know how to serialize a $value for the I18nData!")
        }
    }
    override fun deserialize(decoder: Decoder): Any {
        return when (val surrogate = decoder.decodeSerializableValue(ArgumentSurrogate.serializer())) {
            is IntArgumentSurrogate -> {
                surrogate.value
            }
            is LongArgumentSurrogate -> {
                surrogate.value
            }
            is DoubleArgumentSurrogate -> {
                surrogate.value
            }
            is StringArgumentSurrogate -> {
                surrogate.value
            }
            else -> error("I don't know to deserialize a $surrogate for the I18nData!")
        }
    }
}