package dev.freya02.jda.emojis.newer;

import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;

interface EmojiLock {
	UnicodeEmoji LOCKED = new UnicodeEmojiImpl("🔒");
	
	UnicodeEmoji LOCK = new UnicodeEmojiImpl("🔒");
	
	UnicodeEmoji UNLOCKED = new UnicodeEmojiImpl("🔓");
	
	UnicodeEmoji UNLOCK = new UnicodeEmojiImpl("🔓");
	
	UnicodeEmoji LOCK_WITH_INK_PEN = new UnicodeEmojiImpl("🔏");
	
	UnicodeEmoji CLOSED_LOCK_WITH_KEY = new UnicodeEmojiImpl("🔐");
	
	UnicodeEmoji KEY = new UnicodeEmojiImpl("🔑");
	
	UnicodeEmoji KEY2 = new UnicodeEmojiImpl("🗝️");
	
	UnicodeEmoji OLD_KEY = new UnicodeEmojiImpl("🗝️");
	
	
}
