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
        private const val TASK_NAME = "generateI18nKeys"
    }

    override fun apply(target: Project) {
        with(target) {
            val extension = project.extensions.create<I18nHelperPluginExtension>("i18nHelper")

            val task = tasks.register(TASK_NAME) {
                doLast {
                    val localeFolder = File(this.project.buildDir, "generated/languages")
                    localeFolder.deleteRecursively()
                    localeFolder.mkdirs()

                    val loadTransformCallback = extension.translationLoadTransform.getOrElse { file, map ->
                        map
                    }

                    val map = loadTranslations(
                        File(
                            this.project.projectDir,
                            extension.languageSourceFolder.get()
                        ),
                        loadTransformCallback
                    )

                    val i18nKeysFile = FileSpec.builder(extension.generatedPackage.get(), "I18nKeys")
                    i18nKeysFile.addComment("Auto-generated by the \"$TASK_NAME\" Gradle task, do not modify this manually!\nhttps://github.com/PerfectDreams/i18nHelper\ni18n means \"Internationalization\"")

                    i18nKeysFile.addType(
                        addI18nKeys(
                            "I18nKeys",
                            "",
                            map
                        )
                    )

                    val i18nDataFile = FileSpec.builder(extension.generatedPackage.get(), "I18nKeysData")
                    i18nDataFile.addComment("Auto-generated by the \"$TASK_NAME\" Gradle task, do not modify this manually!\nhttps://github.com/PerfectDreams/i18nHelper\ni18n means \"Internationalization\"")

                    i18nDataFile.addType(
                        addI18nKeysData(
                            extension.generatedPackage.get(),
                            listOf(),
                            "I18nKeysData",
                            "",
                            map
                        )
                    )

                    i18nKeysFile.build().writeTo(localeFolder)
                    i18nDataFile.build().writeTo(localeFolder)
                }
            }

            // HACKY WORKAROUND!!!
            // This makes the generateI18nKeys task to always be ran after the compileKotlin step
            // We need to do this (instead of using withType) because, for some reason, it doesn't work and the task isn't executed.
            //
            // We need to keep it within the "afterEvaluate" block because, if it isn't, the compile*InsertStuffHere* tasks won't exist!
            //
            // And the reason it is "compile" instead of compileKotlin is because, in Kotlin 1.6.20-M1, a task named "compileCommonMainKotlinMetadata" was added
            // https://stackoverflow.com/a/58763804/7271796
            project.afterEvaluate {
                project.tasks.filter { it.name.startsWith("compile") }.forEach {
                    it.dependsOn(task)
                }
            }
        }
    }

    fun addI18nKeys(firstChild: String, prefix: String, currentKey: Map<String, Any>): TypeSpec {
        val obj = TypeSpec.objectBuilder(firstChild.capitalize())

        for ((key, value) in currentKey) {
            if (value is Map<*, *>) {
                obj.addType(
                    addI18nKeys(
                        key,
                        "$prefix$key.",
                        value as Map<String, Any>
                    )
                )
            } else if (value is List<*>) {
                obj.addProperty(
                    PropertySpec.builder(
                        key.capitalize(),
                        ClassName("net.perfectdreams.i18nhelper.core.keys", "ListI18nKey")
                    )
                        .initializer("ListI18nKey(\"$prefix$key\")")
                        .build()
                )
            } else {
                obj.addProperty(
                    PropertySpec.builder(
                        key.capitalize(),
                        ClassName("net.perfectdreams.i18nhelper.core.keys", "StringI18nKey")
                    )
                        .initializer("StringI18nKey(\"$prefix$key\")")
                        .build()
                )
            }
        }

        return obj.build()
    }

    private fun loadTranslations(file: File, loadTransform: LoadTransformCallback): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        loadTranslationsFromFolder(file, map, true, loadTransform)
        return map
    }

    private fun loadTranslationsFromFolder(file: File, map: MutableMap<String, Any>, skipLanguageYaml: Boolean, loadTransform: LoadTransformCallback) {
        val files = file.listFiles() ?: error("Not a valid file!")

        for (childFile in files) {
            if (childFile.isDirectory) {
                loadTranslationsFromFolder(childFile, map, false, loadTransform)
            } else if (childFile.extension == "yml") {
                // language.yml is the language settings file, we don't want to load it unless if it is in a subfolder
                if (skipLanguageYaml && childFile.nameWithoutExtension == "language")
                    continue

                loadTranslationFileToMap(childFile, map, loadTransform)
            }
        }
    }

    private fun loadTranslationFileToMap(file: File, map: MutableMap<String, Any>, loadTransform: LoadTransformCallback) {
        val loadedMap = yaml.load<Map<String, Any>>(
            file.readText()
        )
        val result = loadTransform.invoke(file, loadedMap)

        // I don't think this is a good solution, but hey, it works...
        val toKeys = mutableMapOf<String, Any>()
        flatten(map, toKeys)
        flatten(result, toKeys)

        val exploded = mutableMapOf<String, Any>()
        explode(toKeys, exploded)
        map += exploded
    }

    private fun addI18nKeysData(classPackage: String, children: List<String>, firstChild: String, prefix: String, currentKey: Map<String, Any>): TypeSpec {
        val obj = TypeSpec.objectBuilder(
            if (prefix.isEmpty()) { // Do not change the "I18nKeysData" class name to be lowercase
                firstChild
            } else {
                firstChild.toLowerCase().capitalize()
            }
        )

        for ((key, value) in currentKey) {
            if (value is Map<*, *>) {
                obj.addProperty(
                    PropertySpec.builder(
                        key.capitalize(),
                        ClassName(
                            classPackage,
                            "I18nKeysData",
                            *(children + key).map { ("Inner$it".toLowerCase().capitalize()) }
                                .toTypedArray()
                        )
                    ).initializer(
                        "${classPackage}.I18nKeysData.${
                            (children + key).joinToString(".") {
                                ("Inner$it".toLowerCase().capitalize())
                            }
                        }"
                    ).build()
                )
                obj.addType(
                    addI18nKeysData(
                        classPackage,
                        children.toMutableList()
                            .apply {
                                this.add(key.capitalize())
                            },
                        "Inner$key",
                        "$prefix$key.",
                        value as Map<String, Any>
                    )
                )
            } else if (value is List<*>) {
                convertToKotlinPropertyOrFunctionAndAddToObject(classPackage, children, "ListI18nData", obj, key, value.joinToString("\n")) // We will join the list into a single string, to make it easier to process them
            } else {
                convertToKotlinPropertyOrFunctionAndAddToObject(classPackage, children, "StringI18nData", obj, key, value as String)
            }
        }

        return obj.build()
    }

    private fun convertToKotlinPropertyOrFunctionAndAddToObject(classPackage: String, children: List<String>, classKeyToBeUsed: String, obj: TypeSpec.Builder, key: String, value: String) {
        val node = MessagePatternUtil.buildMessageNode(value)
        val hasAnyArgument = node.contents.any { it is MessagePatternUtil.ArgNode }

        if (hasAnyArgument) {
            obj.addFunction(convertToKotlinFunction(classPackage, children, classKeyToBeUsed, node, key, value))
        } else {
            obj.addProperty(convertToKotlinProperty(classPackage, children, classKeyToBeUsed, key, value))
        }
    }

    private fun convertToKotlinFunction(classPackage: String, children: List<String>, classKeyToBeUsed: String, node: MessagePatternUtil.MessageNode, key: String, value: String): FunSpec {
        val arguments = mutableSetOf<String>()
        val function = FunSpec.builder(key.capitalize())
            .returns(
                ClassName(
                    "net.perfectdreams.i18nhelper.core.keydata",
                    classKeyToBeUsed
                )
            )

        for (innerNode in node.contents) {
            if (innerNode is MessagePatternUtil.ArgNode) {
                convertNodeToFunctionParameters(
                    innerNode,
                    arguments,
                    function
                )
            }
        }

        function.addCode(
            CodeBlock.builder()
                .add("return ")
                .add("net.perfectdreams.i18nhelper.core.keydata.$classKeyToBeUsed(${"${classPackage}.I18nKeys.${
                    children.toMutableList().apply {
                        this.add(key.capitalize())
                    }.joinToString(".")
                }"}")
                .add(", ")
                .add("mutableMapOf(")
                .apply {
                    for (argument in arguments) {
                        add("%S to $argument,", argument)
                    }
                }
                .add(")")
                .add(")")
                .build()
        )

        return function.build()
    }

    private fun convertNodeToFunctionParameters(
        node: MessagePatternUtil.ArgNode,
        arguments: MutableSet<String>,
        function: FunSpec.Builder
    ): FunSpec.Builder {
        // Sometimes, lists may have duplicate arguments, so just ignore if they have one
        if (arguments.contains(node.name))
            return function

        when (node.typeName) {
            "number" -> {
                when (node.simpleStyle) {
                    "integer" -> function.addParameter(node.name, Int::class)
                    "short" -> function.addParameter(node.name, Short::class)
                    "long" -> function.addParameter(node.name, Long::class)
                    else -> function.addParameter(node.name, Any::class)
                }
            }
            "plural" -> {
                function.addParameter(node.name, Any::class)

                for (variant in node.complexStyle.variants) {
                    variant.message.contents.forEach {
                        if (it is MessagePatternUtil.ArgNode) {
                            convertNodeToFunctionParameters(it, arguments, function)
                        }
                    }
                }
            }
            else -> {
                function.addParameter(node.name, Any::class)
            }
        }

        arguments.add(node.name)

        return function
    }

    private fun convertToKotlinProperty(classPackage: String, children: List<String>, classKeyToBeUsed: String, key: String, value: String): PropertySpec {
        // If there isn't any arguments, we can use a property!
        // So let's create the property and return it here :)
        return PropertySpec.builder(
            key.capitalize(),
            ClassName(
                "net.perfectdreams.i18nhelper.core.keydata",
                classKeyToBeUsed
            )
        )
            .initializer("$classKeyToBeUsed(${"${classPackage}.I18nKeys.${
                children.toMutableList().apply {
                    this.add(key.capitalize())
                }.joinToString(".")
            }"}, emptyMap())")
            .build()
    }

    // From https://stackoverflow.com/a/65101224/7271796
    private fun flatten(source: Map<String, Any>, target: MutableMap<String, Any>, prefix: String = "") {
        for (entry in source) {
            val fullKey = if (prefix.isNotEmpty()) prefix + "." + entry.key else entry.key
            if (entry.value is Map<*, *>) {
                flatten(entry.value as Map<String, Any>, target, fullKey)
                continue
            }
            target[fullKey] = entry.value
        }
    }

    private fun explode(source: Map<String, Any>, target: MutableMap<String, Any>) {
        for (entry in source) {
            var targetMap = target
            val keySegments = entry.key.split(".")
            // Rebuild the map
            for (keySegment in keySegments.dropLast(1)) {
                if (targetMap.containsKey(keySegment)) {
                    val value = targetMap.get(keySegment)
                    if (value is Map<*, *>) {
                        targetMap = value as HashMap<String, Any>
                        continue
                    }
                }
                val newNestedMap = HashMap<String, Any>()
                targetMap[keySegment] = newNestedMap
                targetMap = newNestedMap
            }
            // Put values into it
            targetMap[keySegments.last()] = entry.value
        }
    }
}