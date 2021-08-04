package net.perfectdreams.i18nhelper.plugin

import com.ibm.icu.text.MessagePatternUtil
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.yaml.snakeyaml.Yaml
import java.io.File

class I18nHelperPlugin : Plugin<Project> {
    companion object {
        private val yaml = Yaml()
    }

    override fun apply(target: Project) {
        with(target) {
            val extension = project.extensions.create<I18nHelperPluginExtension>("i18nHelper")

            tasks.register("generateLanguageKeys") {
                // HACKY WORKAROUND!!!
                // This makes the generateTranslationKeys task to always be ran after the compileKotlin step
                // We need to do this (instead of using withType) because, for some reason, it doesn't work and the task isn't executed.
                project.tasks.filter { it.name.startsWith("compileKotlin") }.forEach {
                    it.dependsOn("generateLanguageKeys")
                }

                doLast {
                    val localeFolder = File(this.project.buildDir, "generated/languages")
                    localeFolder.deleteRecursively()
                    localeFolder.mkdirs()

                    val map = mutableMapOf<String, Any>()

                    File(this.project.projectDir, extension.languageSourceFolder.get())
                        .listFiles()
                        .filter { it.nameWithoutExtension != "language" }
                        .forEach {
                            map += yaml.load<Map<String, Any>>(
                                it.readText()
                            )
                        }

                    val translationKeysFile = FileSpec.builder(extension.generatedPackage.get(), "TranslationKeys")

                    fun addTranslationKeys(firstChild: String, prefix: String, currentKey: Map<String, Any>): TypeSpec {
                        val obj = TypeSpec.objectBuilder(firstChild.capitalize())

                        for ((key, value) in currentKey) {
                            if (value is Map<*, *>) {
                                obj.addType(
                                    addTranslationKeys(
                                        key,
                                        "$prefix$key.",
                                        value as Map<String, Any>
                                    )
                                )
                            } else if (value is List<*>) {
                                obj.addProperty(
                                    PropertySpec.builder(
                                        key.capitalize(),
                                        ClassName("net.perfectdreams.i18nhelper.core.keys", "ListTranslationKey")
                                    )
                                        .initializer("ListTranslationKey(\"$prefix$key\")")
                                        .build()
                                )
                            } else {
                                obj.addProperty(
                                    PropertySpec.builder(
                                        key.capitalize(),
                                        ClassName("net.perfectdreams.i18nhelper.core.keys", "StringTranslationKey")
                                    )
                                        .initializer("StringTranslationKey(\"$prefix$key\")")
                                        .build()
                                )
                            }
                        }

                        return obj.build()
                    }

                    translationKeysFile.addType(
                        addTranslationKeys(
                            "TranslationKeys",
                            "",
                            map
                        )
                    )

                    fun addTranslationData(children: List<String>, firstChild: String, prefix: String, currentKey: Map<String, Any>): TypeSpec {
                        val obj = TypeSpec.objectBuilder(firstChild.capitalize())

                        for ((key, value) in currentKey) {
                            if (value is Map<*, *>) {
                                obj.addType(
                                    addTranslationData(
                                        children.toMutableList()
                                            .apply {
                                                this.add(key.capitalize())
                                            },
                                        key,
                                        "$prefix$key.",
                                        value as Map<String, Any>
                                    )
                                )
                            } else if (value is List<*>) {
                                val valueAsString = value.joinToString("\n") // We will join the list into a single string, to make it easier to process them

                                val node = MessagePatternUtil.buildMessageNode(valueAsString)
                                val hasAnyArgument = node.contents.any { it is MessagePatternUtil.ArgNode }

                                val classBuilder = if (hasAnyArgument) {
                                    TypeSpec.classBuilder(key.capitalize())
                                } else {
                                    TypeSpec.objectBuilder(key.capitalize())
                                }

                                val arguments = mutableListOf<String>()

                                if (hasAnyArgument) {
                                    val constructor = FunSpec.constructorBuilder()

                                    node.contents.forEach {
                                        if (it is MessagePatternUtil.ArgNode) {
                                            when (it.typeName) {
                                                "integer" -> constructor.addParameter(it.name, Int::class)
                                                "short" -> constructor.addParameter(it.name, Short::class)
                                                "long" -> constructor.addParameter(it.name, Long::class)
                                                else -> constructor.addParameter(it.name, Any::class)
                                            }

                                            arguments.add(it.name)
                                        }
                                    }

                                    classBuilder.primaryConstructor(constructor.build())
                                }

                                obj.addType(
                                    classBuilder
                                        .addSuperclassConstructorParameter(
                                            CodeBlock.builder()
                                                .add("${extension.generatedPackage.get()}.TranslationKeys.${
                                                    children.toMutableList().apply {
                                                        this.add(key.capitalize())
                                                    }.joinToString(".")
                                                }")
                                                .add(", ")
                                                .add("mutableMapOf(")
                                                .apply {
                                                    for (argument in arguments) {
                                                        add("%S to $argument,", argument)
                                                    }
                                                }
                                                .add(")")
                                                .build()
                                        )
                                        .superclass(
                                            ClassName(
                                                "net.perfectdreams.i18nhelper.core.keydata",
                                                "ListTranslationData"
                                            )
                                        )
                                        .build()
                                )
                            } else {
                                val valueAsString = value as String

                                val node = MessagePatternUtil.buildMessageNode(valueAsString)
                                val hasAnyArgument = node.contents.any { it is MessagePatternUtil.ArgNode }

                                val classBuilder = if (hasAnyArgument) {
                                    TypeSpec.classBuilder(key.capitalize())
                                } else {
                                    TypeSpec.objectBuilder(key.capitalize())
                                }

                                val arguments = mutableListOf<String>()

                                if (hasAnyArgument) {
                                    val constructor = FunSpec.constructorBuilder()

                                    node.contents.forEach {
                                        if (it is MessagePatternUtil.ArgNode) {
                                            when (it.typeName) {
                                                "integer" -> constructor.addParameter(it.name, Int::class)
                                                "short" -> constructor.addParameter(it.name, Short::class)
                                                "long" -> constructor.addParameter(it.name, Long::class)
                                                else -> constructor.addParameter(it.name, Any::class)
                                            }

                                            arguments.add(it.name)
                                        }
                                    }

                                    classBuilder.primaryConstructor(constructor.build())
                                }

                                obj.addType(
                                    classBuilder
                                        .addSuperclassConstructorParameter(
                                            CodeBlock.builder()
                                                .add("${extension.generatedPackage.get()}.TranslationKeys.${
                                                    children.toMutableList().apply {
                                                        this.add(key.capitalize())
                                                    }.joinToString(".")
                                                }")
                                                .add(", ")
                                                .add("mutableMapOf(")
                                                .apply {
                                                    for (argument in arguments) {
                                                        add("%S to $argument,", argument)
                                                    }
                                                }
                                                .add(")")
                                                .build()
                                        )
                                        .superclass(
                                            ClassName(
                                                "net.perfectdreams.i18nhelper.core.keydata",
                                                "StringTranslationData"
                                            )
                                        )
                                        .build()
                                )
                            }
                        }

                        return obj.build()
                    }

                    val translationDataFile = FileSpec.builder(extension.generatedPackage.get(), "TranslationData")

                    translationDataFile.addType(
                        addTranslationData(
                            listOf(),
                            "TranslationData",
                            "",
                            map
                        )
                    )

                    translationKeysFile.build().writeTo(localeFolder)
                    translationDataFile.build().writeTo(localeFolder)
                }
            }
        }
    }
}