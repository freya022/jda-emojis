package dev.freya02.jda.emojis.old;

import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;

interface EmojiSound {
	UnicodeEmoji MUTED_SPEAKER = new UnicodeEmojiImpl("🔇");
	
	UnicodeEmoji SPEAKER_LOW_VOLUME = new UnicodeEmojiImpl("🔈");
	
	UnicodeEmoji SPEAKER_MEDIUM_VOLUME = new UnicodeEmojiImpl("🔉");
	
	UnicodeEmoji SPEAKER_HIGH_VOLUME = new UnicodeEmojiImpl("🔊");
	
	UnicodeEmoji LOUDSPEAKER = new UnicodeEmojiImpl("📢");
	
	UnicodeEmoji MEGAPHONE = new UnicodeEmojiImpl("📣");
	
	UnicodeEmoji POSTAL_HORN = new UnicodeEmojiImpl("📯");
	
	UnicodeEmoji BELL = new UnicodeEmojiImpl("🔔");
	
	UnicodeEmoji BELL_WITH_SLASH = new UnicodeEmojiImpl("🔕");
	
	
}
