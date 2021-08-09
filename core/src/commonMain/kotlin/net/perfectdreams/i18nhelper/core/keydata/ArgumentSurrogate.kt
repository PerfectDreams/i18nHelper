package net.perfectdreams.i18nhelper.core.keydata

import kotlinx.serialization.Serializable

@Serializable
internal sealed class ArgumentSurrogate

@Serializable
internal class IntArgumentSurrogate(val value: Int) : ArgumentSurrogate()

@Serializable
internal class LongArgumentSurrogate(val value: Long) : ArgumentSurrogate()

@Serializable
internal class DoubleArgumentSurrogate(val value: Double) : ArgumentSurrogate()

@Serializable
internal class StringArgumentSurrogate(val value: String) : ArgumentSurrogate()