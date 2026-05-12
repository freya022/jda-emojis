package dev.freya02.jda.emojis.unicode;

import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.*;

public class UnicodeEmojisManager {
    private static final Map<String, List<String>> surrogateAliases;
    private static final Map<String, String> aliasSurrogates;
    private static final Map<Integer, List<String>> surrogatesByFirstCodepoints;

    static {
        var stream = UnicodeEmojisManager.class.getResourceAsStream("/aliases.csv");
        if (stream == null) {
            throw new IllegalStateException("'aliases.csv' is missing");
        }

        try (var reader = new BufferedReader(new InputStreamReader(stream))) {
            Map<String, List<String>> tmpSurrogateAliases = new HashMap<>();
            Map<String, String> tmpAliasSurrogates = new HashMap<>();
            Map<Integer, List<String>> tmpSurrogatesByFirstCodepoints = new HashMap<>();

            String line;
            while ((line = reader.readLine()) != null) {
                var fields = line.split(",");
                var surrogates = fields[0];
                var aliases = Arrays.asList(Arrays.copyOfRange(fields, 1, fields.length));

                tmpSurrogateAliases.put(surrogates, aliases);
                for (String alias : aliases) {
                    tmpAliasSurrogates.put(alias, surrogates);
                }
                tmpSurrogatesByFirstCodepoints
                        .computeIfAbsent(surrogates.codePointAt(0), codePoint -> new ArrayList<>())
                        .add(surrogates);
            }

            // Sort all surrogates by descending length
            //  as to find emojis in order, you need to test the longest sequences first
            //  as to not return the smaller sequences when it was actually a longer one
            Comparator<String> descendingLengthComparator = Comparator.comparingInt(String::length).reversed();
            for (List<String> surrogates : tmpSurrogatesByFirstCodepoints.values()) {
                surrogates.sort(descendingLengthComparator);
            }

            surrogateAliases = Collections.unmodifiableMap(tmpSurrogateAliases);
            aliasSurrogates = Collections.unmodifiableMap(tmpAliasSurrogates);
            surrogatesByFirstCodepoints = Collections.unmodifiableMap(tmpSurrogatesByFirstCodepoints);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static boolean isValidEmoji(String surrogates) {
        return surrogateAliases.containsKey(surrogates);
    }

    public static boolean isValidAlias(String alias) {
        if (alias.startsWith(":") && alias.endsWith(":"))
            alias = alias.substring(1, alias.length() - 1);
        return aliasSurrogates.containsKey(alias);
    }

    @Nullable
    public static String getEmojiByAlias(String alias) {
        if (alias.startsWith(":") && alias.endsWith(":"))
            alias = alias.substring(1, alias.length() - 1);
        return aliasSurrogates.get(alias);
    }

    @Nullable
    public static List<String> getAliasesByEmoji(String surrogates) {
        return surrogateAliases.get(surrogates);
    }

    public static List<IndexedEmoji> extractEmojiInOrder(String input) {
        List<IndexedEmoji> indexedEmojis = new ArrayList<>();
        int[] inputCodePoints = Utils.stringToCodePoints(input);

        int charIndex = 0;
        for (int inputCodePointIndex = 0; inputCodePointIndex < inputCodePoints.length; inputCodePointIndex++) {
            int inputCodePoint = inputCodePoints[inputCodePointIndex];
            List<String> candidates = surrogatesByFirstCodepoints.get(inputCodePoint);
            if (candidates == null) {
                charIndex += Character.charCount(inputCodePoint);
                continue;
            }

            // An emoji starts with this codepoint, try to find an emoji
            String foundEmoji = Utils.findSurrogate(candidates, inputCodePoints, inputCodePointIndex);
            if (foundEmoji == null) {
                charIndex += Character.charCount(inputCodePoint);
                continue;
            }

            indexedEmojis.add(new IndexedEmoji(foundEmoji, charIndex));
            // skip to end of emoji
            charIndex += foundEmoji.length();
            // skip to end of emoji, -1 to counter the loop increment
            inputCodePointIndex += Utils.getCodePointCount(foundEmoji) - 1;
        }

        return indexedEmojis;
    }

    public static class IndexedEmoji {
        private final String surrogates;
        private final int index;

        public IndexedEmoji(String surrogates, int index) {
            this.surrogates = surrogates;
            this.index = index;
        }

        public String getSurrogates() {
            return surrogates;
        }

        public int getIndex() {
            return index;
        }
    }
}
