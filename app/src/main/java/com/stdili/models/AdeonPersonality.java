package com.stdili.models;

/**
 * Adeon Personality Configuration
 * Defines personality modes for flexible tutoring and counseling styles
 */
public class AdeonPersonality {

    public enum TutorMode {
        FRIENDLY_COACH("Friendly Coach", "Encouraging and supportive, celebrates progress, uses emojis, builds confidence"),
        STRICT_TUTOR("Strict Tutor", "No-nonsense, focuses on accuracy, detailed feedback, high standards"),
        BALANCED("Balanced", "Professional and supportive, clear explanations with encouragement");

        private final String displayName;
        private final String description;

        TutorMode(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    private TutorMode currentMode;
    private String userName;

    public AdeonPersonality(TutorMode mode, String userName) {
        this.currentMode = mode;
        this.userName = userName;
    }

    public AdeonPersonality(TutorMode mode) {
        this(mode, "Student");
    }

    public AdeonPersonality() {
        this(TutorMode.FRIENDLY_COACH, "Student");
    }

    public TutorMode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(TutorMode mode) {
        this.currentMode = mode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Get personality-specific prefix for responses
     */
    public String getResponsePrefix() {
        switch (currentMode) {
            case FRIENDLY_COACH:
                return "🤗 Hey " + userName + "! ";
            case STRICT_TUTOR:
                return "📝 " + userName + ", ";
            case BALANCED:
            default:
                return "👋 Hello " + userName + ", ";
        }
    }

    /**
     * Get encouragement message based on personality
     */
    public String getEncouragementMessage() {
        switch (currentMode) {
            case FRIENDLY_COACH:
                return "You're doing great! Keep up the amazing work! 💪✨";
            case STRICT_TUTOR:
                return "Solid work. Continue to review and strengthen your understanding.";
            case BALANCED:
            default:
                return "Good effort. Keep practicing to master this concept.";
        }
    }

    /**
     * Get correction message based on personality
     */
    public String getCorrectionMessage() {
        switch (currentMode) {
            case FRIENDLY_COACH:
                return "No worries! Let me explain this in a simpler way. You've got this! 💡";
            case STRICT_TUTOR:
                return "Incorrect. Let me clarify the correct approach.";
            case BALANCED:
            default:
                return "That's not quite right. Let me show you the correct method.";
        }
    }

    /**
     * Get motivation style based on personality
     */
    public String getMotivationalBoost() {
        switch (currentMode) {
            case FRIENDLY_COACH:
                return "\n\n🌟 Remember: Every expert was once a beginner. You're making amazing progress! 🚀";
            case STRICT_TUTOR:
                return "\n\n📚 Success comes from consistent effort and mastery of fundamentals.";
            case BALANCED:
            default:
                return "\n\n🎯 Stay focused and consistently practice to improve.";
        }
    }

    @Override
    public String toString() {
        return currentMode.getDisplayName();
    }
}
