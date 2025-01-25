package dev.freya02.jda.emojis.old;

import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;

interface EmojiLock {
	UnicodeEmoji LOCK = new UnicodeEmojiImpl("🔒");
	
	UnicodeEmoji UNLOCK = new UnicodeEmojiImpl("🔓");
	
	UnicodeEmoji LOCK_WITH_INK_PEN = new UnicodeEmojiImpl("🔏");
	
	UnicodeEmoji CLOSED_LOCK_WITH_KEY = new UnicodeEmojiImpl("🔐");
	
	UnicodeEmoji KEY = new UnicodeEmojiImpl("🔑");
	
	UnicodeEmoji KEY2 = new UnicodeEmojiImpl("🗝️");
	
	
}
