package dev.freya02.jda.emojis.unicode

import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji
import org.junit.jupiter.api.Test
import tools.jackson.databind.SerializationFeature
import tools.jackson.module.kotlin.jacksonMapperBuilder
import tools.jackson.module.kotlin.readValue
import java.io.InputStream
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText
import kotlin.test.fail

object EmojisSnapshotTest {

    private typealias FieldName = String
    private typealias Surrogates = String

    private val mapper = jacksonMapperBuilder()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
        .build()

    @Test
    fun `Match unicode of JDA and JEmoji`() {
        val actualEmojiFields: Map<FieldName, Surrogates> = UnicodeEmojis::class.java.fields
            .filter { it.type == UnicodeEmoji::class.java }
            .onEach { it.isAccessible = true }
            .associate { field ->
                val emoji = field.get(null) as UnicodeEmoji

                field.name to emoji.name
            }

        val expectedEmojiFields: Map<FieldName, Surrogates> = readExpectedEmojiFields()

        if (expectedEmojiFields != actualEmojiFields) {
            Path("src/test/resources/emoji_fields.json")
                .createParentDirectories()
                .writeText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualEmojiFields))
            fail("Emojis were updated, please check the changes before committing")
        }
    }

    private fun readExpectedEmojiFields(): Map<FieldName, Surrogates> {
        val stream = EmojisSnapshotTest::class.java.getResourceAsStream("/emoji_fields.json") ?: return emptyMap()

        return stream.use(InputStream::readBytes)
            .decodeToString()
            .let(mapper::readValue)
    }
}
