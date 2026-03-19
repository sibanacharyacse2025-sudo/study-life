package com.stdili.utils;

import java.util.Arrays;
import java.util.List;

public class ModerationUtils {

    private static final List<String> BAD_WORDS = Arrays.asList(
        "abuse", "harassment", "bully", "threat", "violence", "hate", "racist", "sexist",
        "inappropriate", "offensive", "spam", "scam", "fake", "phishing", "malware",
        "drugs", "alcohol", "weapon", "bomb", "kill", "die", "suicide", "self-harm"
    );

    private static final List<String> SEXUAL_CONTENT = Arrays.asList(
        "sex", "porn", "nude", "naked", "erotic", "adult", "xxx", "nsfw"
    );

    public static boolean containsBadWords(String message) {
        String lowerMessage = message.toLowerCase();
        for (String word : BAD_WORDS) {
            if (lowerMessage.contains(word)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsSexualContent(String message) {
        String lowerMessage = message.toLowerCase();
        for (String word : SEXUAL_CONTENT) {
            if (lowerMessage.contains(word)) {
                return true;
            }
        }
        return false;
    }

    public static String filterMessage(String message) {
        String filtered = message;
        for (String word : BAD_WORDS) {
            filtered = filtered.replaceAll("(?i)" + word, "***");
        }
        for (String word : SEXUAL_CONTENT) {
            filtered = filtered.replaceAll("(?i)" + word, "***");
        }
        return filtered;
    }

    public static ModerationResult checkMessage(String message) {
        boolean hasBadWords = containsBadWords(message);
        boolean hasSexualContent = containsSexualContent(message);
        String filteredMessage = filterMessage(message);

        return new ModerationResult(hasBadWords, hasSexualContent, filteredMessage);
    }

    public static class ModerationResult {
        public final boolean hasBadWords;
        public final boolean hasSexualContent;
        public final String filteredMessage;

        public ModerationResult(boolean hasBadWords, boolean hasSexualContent, String filteredMessage) {
            this.hasBadWords = hasBadWords;
            this.hasSexualContent = hasSexualContent;
            this.filteredMessage = filteredMessage;
        }

        public boolean shouldBlock() {
            return hasBadWords || hasSexualContent;
        }
    }
}