package dev.freya02.jda.emojis.unicode

import kotlin.test.Test
import kotlin.test.assertEquals

class UnicodeEmojisManagerTests {
    @Test
    fun `extractEmojiInOrder consecutive surrogates`() {
        val indexedEmojis = UnicodeEmojisManager.extractEmojiInOrder("\uD83D\uDE02\uD83D\uDE02")

        assertEquals(2, indexedEmojis.size)
        assertEquals(0, indexedEmojis[0].index)
        assertEquals(2, indexedEmojis[1].index)
    }

    @Test
    fun `extractEmojiInOrder matches longest surrogates first`() {
        val indexedEmojis = UnicodeEmojisManager.extractEmojiInOrder("\uD83D\uDC4D\uD83C\uDFFF\uD83D\uDC4D")

        assertEquals(2, indexedEmojis.size)

        assertEquals("\uD83D\uDC4D\uD83C\uDFFF", indexedEmojis[0].surrogates)
        assertEquals(0, indexedEmojis[0].index)

        assertEquals("\uD83D\uDC4D", indexedEmojis[1].surrogates)
        assertEquals(4, indexedEmojis[1].index)
    }
}
