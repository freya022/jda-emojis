package dev.freya02.jda.emojis.unicode;

import org.jspecify.annotations.Nullable;

import java.util.List;

class Utils {
    @Nullable
    static String findSurrogate(List<String> candidates, int[] inputCodePoints, int inputCodePointIndex) {
        candidateLoop:
        for (String candidate : candidates) {
            int[] emojiCodePointsArray = stringToCodePoints(candidate);
            int emojiCodePointsLength = emojiCodePointsArray.length;

            // Check if candidate fits in the remaining range
            if (inputCodePointIndex + emojiCodePointsLength > inputCodePoints.length) {
                continue;
            }

            // Check for a mismatch
            for (int emojiCodePointIndex = 0; emojiCodePointIndex < emojiCodePointsLength; emojiCodePointIndex++) {
                //break out because the emoji is not the same
                if (inputCodePoints[inputCodePointIndex + emojiCodePointIndex] != emojiCodePointsArray[emojiCodePointIndex]) {
                    continue candidateLoop;
                }
            }

            // No mismatch, return found emoji
            return candidate;
        }

        return null;
    }

    static int[] stringToCodePoints(String text) {
        final int[] codePoints = new int[getCodePointCount(text)];
        int codePointIndex = 0;
        for (int charIndex = 0; charIndex < text.length(); ) {
            final int codePoint = text.codePointAt(charIndex);
            codePoints[codePointIndex++] = codePoint;
            charIndex += Character.charCount(codePoint);
        }
        return codePoints;
    }

    static int getCodePointCount(String string) {
        return string.codePointCount(0, string.length());
    }
}
