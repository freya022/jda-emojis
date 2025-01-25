package dev.freya02.jda.emojis.old;

import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;

interface EmojiLock {
	UnicodeEmoji LOCKED = new UnicodeEmojiImpl("🔒");
	
	UnicodeEmoji UNLOCKED = new UnicodeEmojiImpl("🔓");
	
	UnicodeEmoji LOCKED_WITH_PEN = new UnicodeEmojiImpl("🔏");
	
	UnicodeEmoji LOCKED_WITH_KEY = new UnicodeEmojiImpl("🔐");
	
	UnicodeEmoji KEY = new UnicodeEmojiImpl("🔑");
	
	UnicodeEmoji OLD_KEY = new UnicodeEmojiImpl("🗝️");
	
	
}
