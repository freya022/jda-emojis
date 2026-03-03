import net.fellbaum.jemoji.EmojiManager
import net.fellbaum.jemoji.EmojiSubGroup
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue
import java.io.File
import kotlin.jvm.optionals.getOrNull

private val skippedNames = setOf(
    "+1", "-1",
    "+1_tone1", "+1_tone2", "+1_tone3", "+1_tone4", "+1_tone5",
    "_1_tone1", "_1_tone2", "_1_tone3", "_1_tone4", "_1_tone5",
    "1234",
)

private val emojiReplacements = mapOf(
    // Discord name => Field name
    "100" to "HUNDRED_POINTS",
    "8ball" to "POOL_8_BALL",
    "piñata" to "PINATA",
    "skin-tone-1" to "SKIN_TONE_1",
    "skin-tone-2" to "SKIN_TONE_2",
    "skin-tone-3" to "SKIN_TONE_3",
    "skin-tone-4" to "SKIN_TONE_4",
    "skin-tone-5" to "SKIN_TONE_5",
)

abstract class GenerateEmojisTask : DefaultTask() {
    @get:InputFile
    abstract var inputJson: Provider<File>

    @get:OutputDirectory
    val outputDir: Provider<Directory> = project.layout.buildDirectory.dir("generated/sources/jda-emojis/main/java")

    private data class DiscordEmojis(
        val emojis: List<Emoji>,
    ) {
        data class Emoji(
            val names: List<String>,
            val surrogates: String,
        )
    }

    @TaskAction
    fun generate() {
        val emojiJson = inputJson.get().readText()
        val discordEmojis = jacksonObjectMapper().readValue<DiscordEmojis>(emojiJson)

        val map = buildMap<String, MutableList<String>> {
            //Add the normal emoji aliases
            discordEmojis.emojis.forEachIndexed { index, emoji ->
                val names = mutableListOf<String>()
                emoji.names.forEach { names.add(it) }

                put(emoji.surrogates, names)
            }
        }

        val subGroups = map.entries.groupBy { (surrogates, names) ->
            val jemoji = EmojiManager.getEmoji(surrogates).getOrNull()
            jemoji?.subgroup
                ?: if (names.any { it.startsWith("regional_indicator_") }) {
                    EmojiSubGroup.OTHER_SYMBOL
                } else {
                    error("No group found for $names")
                }
        }
        val classNames = mutableListOf<String>()

        val fieldRegex = Regex("[a-z][\\w_]*", RegexOption.IGNORE_CASE)
        subGroups.forEach { (subgroup, emojis) ->
            val className = "Emoji${subgroup.name.split('_').joinToString("") { it.lowercase().replaceFirstChar { it.uppercase() } }}"
            classNames += className

            val fileContent = createClass(className) {
                val fields = sortedMapOf<String, String>()

                for ((surrogates, names) in emojis) {
                    for (alias in names) {
                        val emojiFieldName = if (fieldRegex.matches(alias)) {
                            alias.uppercase()
                        } else if (skippedNames.contains(alias)) {
                            continue
                        } else if (emojiReplacements.containsKey(alias)) {
                            emojiReplacements[alias]!!
                        } else {
                            error("Alias would produce an invalid field name: $alias")
                        }

                        fields.put(emojiFieldName, """UnicodeEmoji $emojiFieldName = new UnicodeEmojiImpl("$surrogates");""")
                    }
                }

                for ((fieldName, code) in fields) {
                    appendLine(code)
                    appendLine()
                }
            }

            writeOutput("dev.freya02.jda.emojis.unicode", className, fileContent)
        }

        val finalContent = """
            package dev.freya02.jda.emojis.unicode;

            public interface Emojis extends ${classNames.joinToString(", ")} {

            }
        """.trimIndent()
        writeOutput("dev.freya02.jda.emojis.unicode", "Emojis", finalContent)
    }

    private fun createClass(name: String, block: StringBuilder.() -> Unit): String = buildString {
        appendLine("package dev.freya02.jda.emojis.unicode;")
        appendLine()
        appendLine("import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;")
        appendLine("import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;")
        appendLine()
        appendLine("@SuppressWarnings(\"unused\")")
        appendLine("interface $name {")
        appendLine(buildString { block() }.prependIndent("\t"))
        appendLine("}")
    }

    private fun writeOutput(packageName: String, className: String, content: String) {
        val outFile = outputDir.get().dir(packageName.replace('.', '/')).file("$className.java").asFile
        outFile.parentFile.mkdirs()
        outFile.writeText(content)
    }
}
