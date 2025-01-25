import net.fellbaum.jemoji.Emoji
import net.fellbaum.jemoji.Emojis
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.lang.reflect.Field

private val emojiReplacements = mapOf(
    // JEmoji field name => Discord-like emoji name
    "INPUT_NUMBERS" to "INPUT_NUMBERS",
    "HUNDRED_POINTS" to "HUNDRED_POINTS",
    "POOL_8_BALL" to "POOL_8_BALL",
    "EYE_IN_SPEECH_BUBBLE_UNQUALIFIED_1" to "EYE_IN_SPEECH_BUBBLE",
    "TRANSGENDER_SYMBOL_UNQUALIFIED" to "TRANSGENDER_SYMBOL",
    "PI_ATA" to "PINATA",
)

@DisableCachingByDefault
abstract class GenerateEmojisTask : DefaultTask() {
    @get:OutputDirectory
    val outputDir: Provider<Directory> = project.layout.buildDirectory.dir("generated/sources/jda-emojis/main/java")

    @TaskAction
    fun generate() {
        val interfaces = Emojis::class.java.interfaces
        interfaces.forEach { superinterface ->
            val className = superinterface.simpleName
            val fileContent = createClass(className) {
                superinterface
                    .fields
                    .filter { it.type == Emoji::class.java }
                    .forEach forEachField@{ field ->
                        field.isAccessible = true

                        val emoji = field.get(null) as Emoji
                        if (emoji.discordAliases.isEmpty()) return@forEachField

                        val unicode = emoji.emoji
                        getEmojiFieldNames(field, emoji).forEach { emojiFieldName ->
                            appendLine("""UnicodeEmoji $emojiFieldName = new UnicodeEmojiImpl("$unicode");""")
                            appendLine()
                        }
                    }
            }

            writeOutput("dev.freya02.jda.emojis", className, fileContent)
        }

        val finalContent = """
            package dev.freya02.jda.emojis;
            
            public interface Emojis extends ${interfaces.joinToString(", ") { it.simpleName }} {
                
            }
        """.trimIndent()
        writeOutput("dev.freya02.jda.emojis", "Emojis", finalContent)
    }

    private val fieldRegex = Regex("[a-z][\\w_]*", RegexOption.IGNORE_CASE)
    private fun getEmojiFieldNames(field: Field, emoji: Emoji): Collection<String> {
        emojiReplacements[field.name]?.let { return listOf(it) }

        // Check if the first shortcode is a valid field name, if not then require a manual replacement
        val aliases = emoji.discordAliases.map { it.replace(":", "") }
        require(aliases[0].matches(fieldRegex)) {
            "No replacement defined for ${field.name} (${aliases[0]})"
        }
        return aliases
            .filter { it.matches(fieldRegex) }
            .mapTo(hashSetOf()) { it.uppercase() }
    }

    private fun createClass(name: String, block: StringBuilder.() -> Unit): String = buildString {
        appendLine("package dev.freya02.jda.emojis;")
        appendLine()
        appendLine("import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;")
        appendLine("import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;")
        appendLine()
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