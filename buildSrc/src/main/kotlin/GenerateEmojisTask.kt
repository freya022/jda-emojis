import net.fellbaum.jemoji.Emoji
import net.fellbaum.jemoji.Emojis
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault
abstract class GenerateEmojisTask : DefaultTask() {
    @get:OutputDirectory
    val outputDir: Provider<Directory> = project.layout.buildDirectory.dir("generated/sources/jda-emojis/main/java/dev/freya02/jda/emojis")

    @TaskAction
    fun run() {
        val interfaces = Emojis::class.java.interfaces
        interfaces.forEach { superinterface ->
            val className = superinterface.simpleName
            val fileContent = createClass(className) {
                superinterface
                    .fields
                    .filter { it.type == Emoji::class.java }
                    .forEach { field ->
                        field.isAccessible = true

                        val unicode = (field.get(null) as Emoji).emoji
                        appendLine("""UnicodeEmoji ${field.name} = new UnicodeEmojiImpl("$unicode");""")
                        appendLine()
                    }
            }

            outputDir.get().file("$className.java").asFile.writeText(fileContent)
        }

        val finalContent = """
            package dev.freya02.jda.emojis;
            
            public interface Emojis extends ${interfaces.joinToString(", ") { it.simpleName }} {
                
            }
        """.trimIndent()
        outputDir.get().file("Emojis.java").asFile.writeText(finalContent)
    }

    private fun createClass(name: String, block: StringBuilder.() -> Unit): String = buildString {
        appendLine("package dev.freya02.jda.emojis;")
        appendLine()
        appendLine("import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;")
        appendLine("import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;")
        appendLine()
        appendLine("public interface $name {")
        appendLine(buildString { block() }.prependIndent("\t"))
        appendLine("}")
    }

}