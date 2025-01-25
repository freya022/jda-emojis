package dev.freya02.jda.emojis

import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji
import net.fellbaum.jemoji.EmojiManager
import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty0
import kotlin.reflect.full.staticProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.test.assertTrue

object EmojisTests {

    @Test
    fun `Match unicode of JDA and JEmoji`() {
        UnicodeEmojis::class.staticProperties
            .filter { it.returnType.classifier == UnicodeEmoji::class }
            .forEach { jdaProperty ->
                val unicodeEmoji = jdaProperty.getAccessible() as UnicodeEmoji
                assertTrue(EmojiManager.isEmoji(unicodeEmoji.name))
            }
    }

    private fun <T> KProperty0<T>.getAccessible(): T {
        isAccessible = true
        return get()
    }
}