package net.perfectdreams.i18nhelper.plugin

import java.io.File

typealias LoadTransformCallback = (File, MutableMap<String, Any>) -> (MutableMap<String, Any>)