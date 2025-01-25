package dev.freya02.jda.emojis

import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

object EmojisTests {

    @Test
    fun `Match unicode of JDA and JEmoji`() {
        //TODO don't match with name as they differ, just see if the unicode exists
//        val jemojiProperties = Emojis::class.staticProperties
//
//        UnicodeEmojis::class.staticProperties
//            .filter { it.returnType.classifier == UnicodeEmoji::class }
//            .forEach { jdaProperty ->
//                val unicodeEmoji = jdaProperty.getAccessible() as UnicodeEmoji
//                val emoji = jemojiProperties.first { it.name == jdaProperty.name }.getAccessible() as Emoji
//
//                assertEquals(emoji.emoji, unicodeEmoji.name)
//            }
    }

    private fun <T> KProperty0<T>.getAccessible(): T {
        isAccessible = true
        return get()
    }
}