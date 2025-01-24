package dev.freya02.jda.emojis

import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji
import net.fellbaum.jemoji.Emoji
import net.fellbaum.jemoji.Emojis
import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty0
import kotlin.reflect.full.staticProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.test.assertEquals

object EmojisTests {

    @Test
    fun `Match unicode of JDA and JEmoji`() {
        val jemojiProperties = Emojis::class.staticProperties

        UnicodeEmojis::class.staticProperties
            .filter { it.returnType.classifier == UnicodeEmoji::class }
            .forEach { jdaProperty ->
                val unicodeEmoji = jdaProperty.get() as UnicodeEmoji
                val emoji = jemojiProperties.first { it.name == jdaProperty.name }.getAccessible() as Emoji

                assertEquals(emoji.emoji, unicodeEmoji.name)
            }
    }

    private fun <T> KProperty0<T>.getAccessible(): T {
        isAccessible = true
        return get()
    }
}