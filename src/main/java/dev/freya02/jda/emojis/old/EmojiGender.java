package dev.freya02.jda.emojis.old;

import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;

interface EmojiGender {
	UnicodeEmoji FEMALE_SIGN = new UnicodeEmojiImpl("♀️");
	
	UnicodeEmoji FEMALE_SIGN_UNQUALIFIED = new UnicodeEmojiImpl("♀");
	
	UnicodeEmoji MALE_SIGN = new UnicodeEmojiImpl("♂️");
	
	UnicodeEmoji MALE_SIGN_UNQUALIFIED = new UnicodeEmojiImpl("♂");
	
	UnicodeEmoji TRANSGENDER_SYMBOL = new UnicodeEmojiImpl("⚧️");
	
	UnicodeEmoji TRANSGENDER_SYMBOL_UNQUALIFIED = new UnicodeEmojiImpl("⚧");
	
	
}
