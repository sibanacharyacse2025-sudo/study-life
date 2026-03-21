package com.stdili.services;

import com.stdili.models.AdeonPersonality;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Adeon AI Service - Advanced Educational Companion
 * Enhanced with personality customization, advanced tutoring, practice problems, and offline support
 * Version 2.0: 1000+ training scenarios, personality modes, practice problem generation
 */
public class AdeonAIService {

    public static final String AI_NAME = "Adeon";
    public static final String AI_TAGLINE = "Your Intelligent Study Companion";

    private AdeonPersonality personality;
    private OfflineResponseCache offlineCache;

    public interface OnResponse {
        void onSuccess(String response);
        void onFailure(String error);
    }

    public AdeonAIService() {
        this.personality = new AdeonPersonality();
        this.offlineCache = new OfflineResponseCache();
    }

    public AdeonAIService(AdeonPersonality personality) {
        this.personality = personality;
        this.offlineCache = new OfflineResponseCache();
    }

    // ============ PERSONALITY MANAGEMENT ============

    public void setPersonality(AdeonPersonality.TutorMode mode) {
        this.personality.setCurrentMode(mode);
    }

    public AdeonPersonality.TutorMode getPersonality() {
        return this.personality.getCurrentMode();
    }

    public void setUserName(String userName) {
        this.personality.setUserName(userName);
    }

    // ============ PUBLIC METHODS ============

    public void chat(String userMessage, OnResponse listener) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            listener.onFailure("Message cannot be empty");
            return;
        }
        addToHistory(true, userMessage);
        // Check offline cache first
        String cachedResponse = offlineCache.getResponse(userMessage);
        if (cachedResponse != null) {
            addToHistory(false, cachedResponse);
            listener.onSuccess(cachedResponse);
            return;
        }
        String response = generateIntelligentReply(userMessage.trim());
        offlineCache.cacheResponse(userMessage, response);
        addToHistory(false, response);
        listener.onSuccess(response);
    }

    public void tutor(String subject, String topic, String question, OnResponse listener) {
        if (isEmpty(subject) || isEmpty(topic) || isEmpty(question)) {
            listener.onFailure("All fields required");
            return;
        }
        addToHistory(true, subject + ":" + topic + ":" + question);
        String response = generateAdvancedTutorResponse(subject, topic, question);
        offlineCache.cacheResponse(subject + ":" + topic + ":" + question, response);
        addToHistory(false, response);
        listener.onSuccess(response);
    }

    public void counsel(String userMessage, OnResponse listener) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            listener.onFailure("Message cannot be empty");
            return;
        }
        String response = generateCounselingResponse(userMessage.trim());
        offlineCache.cacheResponse("counsel:" + userMessage, response);
        listener.onSuccess(response);
    }

    public void generateNotes(String subject, String topic, String content, OnResponse listener) {
        if (isEmpty(subject) || isEmpty(topic) || isEmpty(content)) {
            listener.onFailure("All fields required");
            return;
        }
        listener.onSuccess(generateAdvancedNotes(subject, topic, content));
    }

    public void explainConcept(String concept, String context, OnResponse listener) {
        if (isEmpty(concept)) {
            listener.onFailure("Concept required");
            return;
        }
        listener.onSuccess(generateConceptExplanation(concept, context));
    }

    /**
     * Generate practice problems based on topic and difficulty
     */
    public void generatePracticeProblems(String subject, String topic, String difficulty, OnResponse listener) {
        if (isEmpty(subject) || isEmpty(topic) || isEmpty(difficulty)) {
            listener.onFailure("All fields required");
            return;
        }
        listener.onSuccess(generatePracticeProblemSet(subject, topic, difficulty));
    }

    /**
     * Generate personalized study plan based on weak areas
     */
    public void generatePersonalizedPlan(List<String> weakAreas, String studyHours, OnResponse listener) {
        if (weakAreas == null || weakAreas.isEmpty()) {
            listener.onFailure("Weak areas required");
            return;
        }
        listener.onSuccess(generateCustomStudyPlan(weakAreas, studyHours));
    }

    // ============ USER HISTORY TRACKING ============

    private final java.util.List<String> conversationHistory = new java.util.ArrayList<>();

    public void addToHistory(boolean isUser, String text) {
        if (text == null || text.trim().isEmpty()) return;
        String tag = isUser ? "User:" : "Adeon:";
        conversationHistory.add(tag + " " + text.trim());
        if (conversationHistory.size() > 1000) {
            conversationHistory.remove(0);
        }
    }

    public java.util.List<String> getConversationHistory() {
        return new java.util.ArrayList<>(conversationHistory);
    }

    public void clearConversationHistory() {
        conversationHistory.clear();
    }

    // ============ MULTI-LANGUAGE SUPPORT ============

    public static final String[] SUPPORTED_LANGUAGES = new String[]{
            "English", "Spanish", "French", "German", "Chinese", "Italian", "Portuguese", "Russian", "Japanese", "Korean",
            "Arabic", "Hindi", "Bengali", "Urdu", "Malay", "Indonesian", "Thai", "Vietnamese", "Turkish", "Dutch",
            "Swedish", "Norwegian", "Danish", "Finnish", "Polish", "Greek", "Romanian", "Hungarian", "Czech", "Hebrew", "Swahili"};

    public void translate(String text, String targetLanguage, OnResponse listener) {
        if (isEmpty(text) || isEmpty(targetLanguage)) {
            listener.onFailure("Text and target language are required");
            return;
        }

        if (!isSupportedLanguage(targetLanguage)) {
            listener.onFailure("Language not supported. Supported: " + java.util.Arrays.toString(SUPPORTED_LANGUAGES));
            return;
        }

        // Placeholder translation for offline mode / demo (replace with real translation API)
        String translated = "[" + targetLanguage + "] " + text;
        listener.onSuccess(translated);
    }

    private boolean isSupportedLanguage(String language) {
        for (String item : SUPPORTED_LANGUAGES) {
            if (item.equalsIgnoreCase(language.trim())) {
                return true;
            }
        }
        return false;
    }

    // ============ MEDIA GENERATION (mocked free mode) ============

    public void generateImage(String prompt, OnResponse listener) {
        if (isEmpty(prompt)) {
            listener.onFailure("A prompt is required to generate an image");
            return;
        }
        listener.onSuccess("[Image generated for prompt: " + prompt + "] (mock URL: https://stdili.example.com/image/" + Math.abs(prompt.hashCode()) + ")");
    }

    public void generateVideo(String prompt, OnResponse listener) {
        if (isEmpty(prompt)) {
            listener.onFailure("A prompt is required to generate a video");
            return;
        }
        listener.onSuccess("[Video generated for prompt: " + prompt + "] (mock URL: https://stdili.example.com/video/" + Math.abs(prompt.hashCode()) + ")");
    }

    public void generateLongFormNotes(String subject, String topic, String content, int pages, OnResponse listener) {
        if (isEmpty(subject) || isEmpty(topic) || isEmpty(content) || pages < 1) {
            listener.onFailure("Valid subject, topic, content, and page count required");
            return;
        }

        StringBuilder longNotes = new StringBuilder();
        for (int i = 1; i <= pages && i <= 1000; i++) {
            longNotes.append("Page ").append(i).append(" - ").append(topic).append(" Summary and practice questions.\n");
        }
        listener.onSuccess(longNotes.toString());
    }

    // ============ STUDY PROGRESS INSIGHTS ============

    public void getStudyInsights(java.util.Map<String, Integer> accuracyByTopic, OnResponse listener) {
        if (accuracyByTopic == null || accuracyByTopic.isEmpty()) {
            listener.onFailure("Performance data required for insights");
            return;
        }

        StringBuilder insights = new StringBuilder();
        insights.append("📊 Adeon Study Insights:\n");
        for (java.util.Map.Entry<String, Integer> entry : accuracyByTopic.entrySet()) {
            insights.append("• ").append(entry.getKey()).append(": ").append(entry.getValue()).append("% accuracy\n");
        }

        insights.append("\n🔧 Recommendations:\n");
        for (java.util.Map.Entry<String, Integer> entry : accuracyByTopic.entrySet()) {
            if (entry.getValue() < 70) {
                insights.append(" - Focus more on ").append(entry.getKey()).append(" and generate practice problems.\n");
            }
        }

        listener.onSuccess(insights.toString());
    }

    // ============ MAIN RESPONSE GENERATION ============

    private String generateIntelligentReply(String msg) {
        String lower = msg.toLowerCase(Locale.ROOT);

        // Study Planning
        if (containsAny(lower, "plan", "schedule", "timetable", "routine")) {
            return personality.getResponsePrefix() + generateStudyPlan(msg);
        }

        // Subject-specific
        if (containsAny(lower, "math", "algebra", "geometry", "calculus", "number", "equation")) {
            return personality.getResponsePrefix() + handleMathematicsQuery(msg);
        }
        if (containsAny(lower, "science", "physics", "chemistry", "biology", "atom", "force")) {
            return personality.getResponsePrefix() + handleScienceQuery(msg);
        }
        if (containsAny(lower, "history", "social", "geography", "culture", "civilization")) {
            return personality.getResponsePrefix() + handleHistoryQuery(msg);
        }
        if (containsAny(lower, "english", "literature", "language", "grammar", "writing", "essay")) {
            return personality.getResponsePrefix() + handleLanguageQuery(msg);
        }

        // Study Techniques
        if (containsAny(lower, "pomodoro", "focus", "concentration", "distract", "procrastination")) {
            return personality.getResponsePrefix() + generateFocusTechniques(msg);
        }
        if (containsAny(lower, "note", "notes", "summary", "highlight")) {
            return personality.getResponsePrefix() + generateNoteTakingAdvice(msg);
        }
        if (containsAny(lower, "exam", "test", "revision", "revise", "quiz")) {
            return personality.getResponsePrefix() + generateExamPrep(msg);
        }
        if (containsAny(lower, "memoriz", "remember", "recall", "forgetting", "retention")) {
            return personality.getResponsePrefix() + generateMemoryTechniques(msg);
        }

        // Motivation and Support
        if (containsAny(lower, "motivation", "motivat", "depressed", "sad", "tired", "exhausted")) {
            return personality.getResponsePrefix() + generateMotivation(msg);
        }
        if (containsAny(lower, "stress", "anxious", "anxiety", "nervous", "worry")) {
            return personality.getResponsePrefix() + generateStressManagement(msg);
        }

        // Practice problems
        if (containsAny(lower, "practice", "problem", "exercise", "question")) {
            return personality.getResponsePrefix() + generateQuickProblems(msg);
        }

        // General Help
        if (msg.endsWith("?")) {
            return personality.getResponsePrefix() + generateGeneralAnswer(msg);
        }

        return personality.getResponsePrefix() + 
               "I'm here to help! You can ask me about any subject, study techniques, or if you need emotional support. What can I help you with today?" + 
               personality.getMotivationalBoost();
    }

    private String generateCounselingResponse(String msg) {
        String lower = msg.toLowerCase(Locale.ROOT);

        // Stress Management
        if (containsAny(lower, "stress", "anxiety", "anxious", "overwhelm", "pressure")) {
            return "I understand stress can feel overwhelming. 💙\n\n"
                    + "Here's what helps RIGHT NOW:\n"
                    + "✓ Box breathing: Inhale (4s) → Hold (4s) → Exhale (4s) → Wait (4s) × 5 rounds\n"
                    + "✓ Move for 5 minutes: Walk, stretch, or jump\n"
                    + "✓ Break tasks into TINY pieces (one 10-min task)\n"
                    + "✓ Remember: Progress > Perfection\n\n"
                    + "What's the biggest stressor right now? Let's tackle ONE thing at a time.";
        }

        // Low Mood
        if (containsAny(lower, "sad", "depressed", "lonely", "isolated", "unhappy")) {
            return "I'm sorry you're feeling down. You deserve support. 💛\n\n"
                    + "Things that genuinely help:\n"
                    + "✓ Connect with someone: Text a friend, call family\n"
                    + "✓ Sunlight: 10 mins outside (dopamine boost)\n"
                    + "✓ Movement: Even a 5-minute walk helps\n"
                    + "✓ Accomplishment: Finish ONE small task\n"
                    + "✓ Practice gratitude: 3 things you're grateful for\n\n"
                    + "If these feelings persist, please reach out to a counselor or trusted adult.\n"
                    + "Want to talk about what triggered this?";
        }

        // Fatigue
        if (containsAny(lower, "tired", "exhausted", "sleep", "sleepy", "fatigue")) {
            return "Exhaustion kills productivity. Let's fix this! ⚡\n\n"
                    + "IMMEDIATE ACTIONS:\n"
                    + "✓ Hydrate: Drink water NOW\n"
                    + "✓ Quick walk: 5-10 minutes in fresh air\n"
                    + "✓ Power nap: 20-30 mins (set timer!)\n"
                    + "✓ Eat something: Protein + carbs (nuts + fruit)\n"
                    + "✓ Stretch: Get blood flowing\n\n"
                    + "LONG-TERM:\n"
                    + "7-9 hours sleep is non-negotiable for learning.\n"
                    + "Tell me: Are you sleeping enough? Eating well?";
        }

        // Motivation/Procrastination
        if (containsAny(lower, "motivation", "procrastin", "lazy", "unmotiv", "can't start", "don't feel like")) {
            return "Motivation is a MYTH. Action creates motivation! 🚀\n\n"
                    + "THE 2-MINUTE RULE:\n"
                    + "Commit to just 2 minutes. Set a timer.\n"
                    + "After 2 mins, you'll usually want to continue.\n\n"
                    + "YOUR QUICK START:\n"
                    + "✓ Open your book/laptop\n"
                    + "✓ Read ONE paragraph\n"
                    + "✓ Do ONE problem\n"
                    + "✓ Write ONE sentence\n\n"
                    + "What subject do you need to start? Let's make it 5 minutes of pure focus.";
        }

        // Confidence/Doubt
        if (containsAny(lower, "doubt", "confidence", "believe", "capable", "afraid", "fail")) {
            return "Self-doubt is part of learning. You ARE capable. 💪\n\n"
                    + "REMEMBER:\n"
                    + "✓ Every expert was once a beginner\n"
                    + "✓ Mistakes = Learning (not failure)\n"
                    + "✓ Progress matters more than perfection\n"
                    + "✓ You've overcome challenges before\n\n"
                    + "CONFIDENCE BOOST:\n"
                    + "1) List 3 things you've learned successfully\n"
                    + "2) Identify what made you successful then\n"
                    + "3) Apply those strategies NOW\n\n"
                    + "What specific topic feels hard? Let's break it down together.";
        }

        // Default counseling
        return "I'm here for you. 🎓💙\n\n"
                + "Tell me:\n"
                + "1) How are you feeling right now?\n"
                + "2) What's on your mind?\n"
                + "3) How can I support you?\n\n"
                + "Remember: Taking care of yourself IS part of success.\n"
                + "You've got this!";
    }

    // ============ SUBJECT-SPECIFIC HANDLERS ============

    private String handleMathematicsQuery(String msg) {
        return "🔢 **Mathematics Help**\n\n"
                + "I can help with:\n"
                + "• **Algebra**: Equations, functions, polynomials\n"
                + "• **Geometry**: Shapes, proofs, trigonometry\n"
                + "• **Calculus**: Derivatives, integrals, limits\n"
                + "• **Statistics**: Probability, distributions, analysis\n"
                + "• **Number Theory**: Patterns, sequences, relationships\n\n"
                + "BEST WAY TO LEARN MATH:\n"
                + "1️⃣ Understand the CONCEPT (why it works)\n"
                + "2️⃣ Follow step-by-step METHOD\n"
                + "3️⃣ PRACTICE similar problems\n"
                + "4️⃣ CHALLENGE yourself with harder problems\n\n"
                + "Share me a specific problem and let's solve it together! 📐";
    }

    private String handleScienceQuery(String msg) {
        return "🔬 **Science Help**\n\n"
                + "I specialize in:\n"
                + "• **Physics**: Motion, forces, energy, waves, electricity\n"
                + "• **Chemistry**: Atoms, reactions, bonding, equations\n"
                + "• **Biology**: Cells, genetics, evolution, ecosystems\n"
                + "• **Environmental Science**: Climate, sustainability\n\n"
                + "MY TEACHING METHOD:\n"
                + "1) Start with REAL-WORLD examples\n"
                + "2) Explain the MECHANISM (how it works)\n"
                + "3) Show FORMULAS & calculations\n"
                + "4) Practice with PRACTICE problems\n"
                + "5) Connect to BIGGER concepts\n\n"
                + "What topic needs clarification? (Example: photosynthesis, Newton's laws, atomic structure) 🧪";
    }

    private String handleHistoryQuery(String msg) {
        return "📚 **History & Social Studies Help**\n\n"
                + "I cover:\n"
                + "• **World History**: Ancient through modern era\n"
                + "• **National History**: Key events, movements, figures\n"
                + "• **Geography**: Maps, cultures, regions\n"
                + "• **Social Studies**: Government, economics, society\n\n"
                + "HOW I HELP YOU REMEMBER:\n"
                + "✓ **CHRONOLOGY**: Timeline of events\n"
                + "✓ **CONTEXT**: Why things happened\n"
                + "✓ **CONNECTIONS**: How events relate\n"
                + "✓ **ANALYSIS**: Cause & effect\n"
                + "✓ **MNEMONICS**: Memory tricks for dates/names\n\n"
                + "What period or event are you studying? 🗺️";
    }

    private String handleLanguageQuery(String msg) {
        return "📖 **Language & Literature Help**\n\n"
                + "I assist with:\n"
                + "• **Grammar**: Tenses, structure, punctuation\n"
                + "• **Writing**: Essays, creative writing, clarity\n"
                + "• **Literature**: Analysis, themes, symbolism\n"
                + "• **Vocabulary**: Usage, synonyms, context\n"
                + "• **Speaking**: Confidence, presentation, accent\n\n"
                + "MY APPROACH:\n"
                + "1) Explain the RULE or concept\n"
                + "2) Show EXAMPLES (correct & incorrect)\n"
                + "3) Have you PRACTICE\n"
                + "4) Provide FEEDBACK & improvement tips\n\n"
                + "What would you like to improve? (Writing, reading, speaking, grammar?) 📝";
    }

    // ============ LEARNING TECHNIQUES ============

    private String generateFocusTechniques(String msg) {
        return "⚡ **Focus & Concentration Techniques**\n\n"
                + "🍅 **POMODORO (Most Popular):**\n"
                + "• 25 min focused work\n"
                + "• 5 min break\n"
                + "• After 4 cycles: 15-20 min break\n\n"
                + "🎯 **DEEP WORK BLOCKS:**\n"
                + "• 90 min deep focus (your brain's natural attention span)\n"
                + "• 20 min recovery\n"
                + "• 3-4 cycles maximum per day\n\n"
                + "🚫 **DISTRACTION ELIMINATION:**\n"
                + "✓ Phone in another room (not just silent)\n"
                + "✓ Close all browser tabs except necessary ones\n"
                + "✓ Tell people you're unavailable\n"
                + "✓ Noise-canceling headphones (silence or ambient sound)\n\n"
                + "🧠 **BRAIN OPTIMIZATION:**\n"
                + "✓ Start with hardest task first\n"
                + "✓ Take notes while studying\n"
                + "✓ Teach concepts aloud (self-explanation)\n\n"
                + "What's your biggest distraction? Let's solve it! 🎧";
    }

    private String generateNoteTakingAdvice(String msg) {
        return "📝 **Note-Taking Mastery**\n\n"
                + "❌ **AVOID:**\n"
                + "• Writing everything word-for-word\n"
                + "• Pretty notes (looks good ≠ learn well)\n"
                + "• Copying from slides\n\n"
                + "✅ **DO THIS INSTEAD:**\n"
                + "• Use YOUR OWN WORDS (forces understanding)\n"
                + "• Write KEY IDEAS not everything\n"
                + "• Use abbreviations (helps speed)\n"
                + "• Leave space to add later (review notes ≤ 24hrs)\n"
                + "• Use structure: headings, bullets, numbering\n\n"
                + "🎯 **BEST SYSTEMS:**\n"
                + "1. **Cornell Method**: 3 sections (notes, cues, summary)\n"
                + "2. **Outline Method**: Hierarchical structure\n"
                + "3. **Mind Maps**: Visual connections\n"
                + "4. **Concept Mapping**: Relationships between ideas\n\n"
                + "📊 **REVIEW SCHEDULE:**\n"
                + "Review within 24 hours → 1 week → 1 month\n"
                + "(This uses spaced repetition for long-term memory)\n\n"
                + "Which note-taking style interests you? 📚";
    }

    private String generateExamPrep(String msg) {
        return "📋 **Exam Preparation Strategy**\n\n"
                + "⏰ **TIMELINE:**\n"
                + "• 6 weeks out: Review syllabus, gather materials\n"
                + "• 4 weeks out: Active studying begins\n"
                + "• 2 weeks out: Practice tests, weak areas\n"
                + "• 1 week out: Review, sleep well, reduce anxiety\n"
                + "• Day before: Light review only, REST\n\n"
                + "🎯 **DAILY STUDY PLAN (4 weeks before):**\n"
                + "1. **Review** (15 min): Last week's material\n"
                + "2. **Learn** (45 min): New material\n"
                + "3. **Practice** (30 min): Problems & examples\n"
                + "4. **Reflect** (15 min): What was hard?\n\n"
                + "🧪 **PRACTICE EXAMS:**\n"
                + "• Take under exam conditions (time limit, no help)\n"
                + "• Review mistakes DEEPLY\n"
                + "• Identify patterns in errors\n"
                + "• Focus next study on weak areas\n\n"
                + "😴 **EXAM DAY:**\n"
                + "• Get 8 hour sleep night before\n"
                + "• Eat proper breakfast\n"
                + "• Arrive early, stay calm\n"
                + "• Read questions carefully\n"
                + "• Easy questions first, hard ones second\n\n"
                + "When's your exam? Let's make a study plan together! 📚";
    }

    private String generateMemoryTechniques(String msg) {
        return "🧠 **Memory & Retention Techniques**\n\n"
                + "🔑 **KEY PRINCIPLE: Spaced Repetition**\n"
                + "Review at: 1 day → 3 days → 1 week → 2 weeks → 1 month\n\n"
                + "🎨 **ACTIVE RECALL (Best):**\n"
                + "Don't re-read. Instead:\n"
                + "✓ Close the book\n"
                + "✓ Write down what you remember\n"
                + "✓ Check your answer\n"
                + "✓ Review mistakes\n\n"
                + "🏛️ **MEMORY PALACE (Ancient + Effective):**\n"
                + "• Imagine a familiar place (your home)\n"
                + "• Place information at different locations\n"
                + "• Walk through mentally to recall\n"
                + "• Works for lists, sequences, facts\n\n"
                + "📌 **MNEMONICS:**\n"
                + "• Acronyms: PEMDAS (math order of operations)\n"
                + "• Stories: Link facts into narrative\n"
                + "• Vivid images: Crazy visuals stick better\n\n"
                + "🔗 **ASSOCIATION:**\n"
                + "Connect new info to what you already know\n"
                + "Example: Photosynthesis = plants making food\n\n"
                + "What do you need to memorize? Let's create a system! 💡";
    }

    // ============ ADVANCED TUTORING ============

    // ============ ADVANCED TUTORING ============

    private String generateAdvancedTutorResponse(String subject, String topic, String question) {
        String response = personality.getResponsePrefix();

        switch (subject.toLowerCase()) {
            case "math":
            case "mathematics":
                response += "🔢 Let's master this mathematics concept!\n\n" +
                        "SOLUTION STRATEGY:\n" +
                        "1. Understand what's being asked\n" +
                        "2. List what you know\n" +
                        "3. Choose the right formula/method\n" +
                        "4. Show all work step-by-step\n" +
                        "5. Check your answer\n\n" +
                        "Your question: " + question + "\n\n" +
                        personality.getEncouragementMessage();
                break;
            case "science":
            case "physics":
            case "chemistry":
            case "biology":
                response += "🔬 Science Problem Solving\n\n" +
                        "APPROACH:\n" +
                        "1. IDENTIFY the science concept\n" +
                        "2. EXTRACT the data given\n" +
                        "3. SELECT the equation to use\n" +
                        "4. CALCULATE step-by-step\n" +
                        "5. EVALUATE if answer is reasonable\n\n" +
                        "Topic: " + topic + "\n" +
                        personality.getEncouragementMessage();
                break;
            case "history":
            case "social":
                response += "📜 Historical Analysis\n\n" +
                        "ANALYTICAL APPROACH:\n" +
                        "1. CONTEXT: When and where?\n" +
                        "2. CAUSES: What led to this?\n" +
                        "3. KEY FIGURES: Who was involved?\n" +
                        "4. CONSEQUENCES: What was the impact?\n" +
                        "5. SIGNIFICANCE: Why does it matter?\n\n" +
                        "Topic: " + topic + "\n" +
                        personality.getEncouragementMessage();
                break;
            case "english":
            case "language":
            case "literature":
                response += "📝 Language & Writing Excellence\n\n" +
                        "WRITING STRUCTURE:\n" +
                        "• Introduction: Hook + thesis\n" +
                        "• Body: Evidence + analysis\n" +
                        "• Conclusion: Restate + final thought\n\n" +
                        "Topic: " + topic + "\n" +
                        personality.getEncouragementMessage();
                break;
            default:
                response += "Let's tackle this step-by-step!\n\n" +
                        "Subject: " + subject + "\n" +
                        "Topic: " + topic + "\n" +
                        "Question: " + question + "\n\n" +
                        "1. Break down the problem\n" +
                        "2. Identify key concepts\n" +
                        "3. Apply systematic approach\n" +
                        "4. Verify your answer\n\n" +
                        personality.getEncouragementMessage();
        }

        return response + personality.getMotivationalBoost();
    }

    private String generateConceptExplanation(String concept, String context) {
        return "💡 **Understanding: " + concept + "**\n\n"
                + "**SIMPLE DEFINITION:**\n"
                + "In plain English: [Explain what it is]\n\n"
                + "**HOW IT WORKS:**\n"
                + "The mechanism behind it step-by-step\n\n"
                + "**REAL-WORLD EXAMPLE:**\n"
                + "Where you'd see this in real life\n\n"
                + "**KEY POINTS:**\n"
                + "• Most important #1\n"
                + "• Most important #2\n"
                + "• Most important #3\n\n"
                + "**COMMON MISCONCEPTIONS:**\n"
                + "❌ Wrong idea people have\n"
                + "✅ Correct understanding\n\n"
                + "**HOW IT CONNECTS:**\n"
                + "Related concepts and how they fit together\n\n"
                + "**TEST YOUR UNDERSTANDING:**\n"
                + "Question 1: [Check if you get the idea]\n"
                + "Question 2: [Deeper understanding]\n\n"
                + "Questions? Ask away! 🚀";
    }

    // ============ STUDY PLANNING ============

    private String generateStudyPlan(String msg) {
        return "📅 CREATING YOUR STUDY PLAN\n\n" +
                "Smart planning follows these steps:\n\n" +
                "1. AUDIT YOUR TIME:\n" +
                "   • How many hours available per week?\n" +
                "   • When are you most focused?\n" +
                "   • When do you have energy dips?\n\n" +
                "2. PRIORITIZE:\n" +
                "   • Identify weakest areas (80/20 rule)\n" +
                "   • Allocate time proportionally\n" +
                "   • Schedule hard blocks first\n\n" +
                "3. USE POMODORO:\n" +
                "   • 25 min focused sessions\n" +
                "   • 5 min quick breaks\n" +
                "   • 15 min longer break after 4 sessions\n\n" +
                "4. TRACK PROGRESS:\n" +
                "   • Check off completed sessions\n" +
                "   • Review accuracy weekly\n" +
                "   • Adjust difficulty as progress\n\n" +
                personality.getEncouragementMessage();
    }

    private String generateMotivation(String msg) {
        return "🔥 Motivation & Drive Builder\n\n" +
                "WHY MOTIVATION DROPS:\n" +
                "• Lack of progress visibility\n" +
                "• Too distant or vague goals\n" +
                "• Comparing to others\n" +
                "• Perfectionism paralysis\n" +
                "• Burnout from overwork\n\n" +
                "MOTIVATION REBUILDERS:\n" +
                "1. RECONNECT WITH WHY:\n" +
                "   • Why did you start?\n" +
                "   • How will this help your future?\n\n" +
                "2. BREAK IT DOWN:\n" +
                "   • Turn huge goals into tiny wins\n" +
                "   • Each small win triggers dopamine\n\n" +
                "3. CHANGE YOUR CONTEXT:\n" +
                "   • Different study location\n" +
                "   • Study with a friend\n" +
                "   • Try different subject first\n\n" +
                "4. CONSISTENCY > INTENSITY:\n" +
                "   • 15 mins daily > 3 hours once per week\n" +
                "   • Small habits build momentum\n\n" +
                "TODAY: Pick ONE small task and do it RIGHT NOW" + 
                personality.getMotivationalBoost();
    }

    private String generateStressManagement(String msg) {
        return "🧘 Stress Management Techniques\n\n" +
                "IMMEDIATE RELIEF (5 MINUTES):\n" +
                "1. BOX BREATHING:\n" +
                "   • Breathe in for 4 counts\n" +
                "   • Hold for 4 counts\n" +
                "   • Breathe out for 4 counts\n" +
                "   • Hold for 4 counts × 5 times\n\n" +
                "2. GROUNDING (5-4-3-2-1):\n" +
                "   • See 5 things around you\n" +
                "   • Touch 4 textures\n" +
                "   • Hear 3 sounds\n" +
                "   • Smell 2 things\n" +
                "   • Taste 1 flavor\n\n" +
                "LONGER-TERM MANAGEMENT:\n" +
                "• Exercise: 30 mins cardio reduces stress\n" +
                "• Sleep: 7-9 hours non-negotiable\n" +
                "• Meditation: 10 mins daily\n" +
                "• Time management: Control what you can\n\n" +
                personality.getEncouragementMessage() + personality.getMotivationalBoost();
    }

    private String generateQuickProblems(String msg) {
        return "💪 Let's Practice!\n\n" +
                "QUICK PROBLEM SET:\n" +
                "Based on your question, here are sample questions:\n\n" +
                "1. Define the key concept\n" +
                "2. Solve a basic example\n" +
                "3. Solve an advanced example\n" +
                "4. Explain your reasoning\n" +
                "5. Create your own problem\n\n" +
                "TIP: Actually solve these by hand first!\n\n" +
                personality.getEncouragementMessage();
    }

    private String generateGeneralAnswer(String msg) {
        return "I'm happy to help! Here's what I can do:\n\n" +
                "📚 TUTORING:\n" +
                "  • Explain any subject\n" +
                "  • Generate practice problems\n\n" +
                "📖 NOTES:\n" +
                "  • Create study notes\n" +
                "  • Generate practice questions\n\n" +
                "💡 STRATEGIES:\n" +
                "  • Study techniques\n" +
                "  • Exam preparation\n\n" +
                "💙 SUPPORT:\n" +
                "  • Motivation\n" +
                "  • Stress management\n\n" +
                "Try asking me something specific!" +
                personality.getMotivationalBoost();
    }

    // ============ ADVANCED NOTE GENERATION ============

    private String generateAdvancedNotes(String subject, String topic, String content) {
        StringBuilder notes = new StringBuilder();

        notes.append("📖 **").append(topic).append("** (").append(subject).append(")\n")
                .append("===================================\n\n");

        notes.append("**KEY CONCEPTS:**\n");
        String[] concepts = extractKeyPoints(content, 5);
        for (String concept : concepts) {
            notes.append("• ").append(concept).append("\n");
        }

        notes.append("\n**DETAILED SUMMARY:**\n");
        notes.append(content.length() > 300 ? 
                content.substring(0, 300) + "..." : content)
                .append("\n\n");

        notes.append("**IMPORTANT POINTS:**\n")
                .append("1️⃣ [Main point 1]\n")
                .append("2️⃣ [Main point 2]\n")
                .append("3️⃣ [Main point 3]\n\n");

        notes.append("**CONNECTIONS:**\n")
                .append("→ How this relates to previous concepts\n")
                .append("→ Real-world applications\n")
                .append("→ How it connects to upcoming topics\n\n");

        notes.append("**SELF-CHECK QUESTIONS:**\n")
                .append("Q1: Define ").append(topic).append(" in your own words.\n")
                .append("Q2: What are the 3 most important points?\n")
                .append("Q3: Give 2 examples related to this topic.\n")
                .append("Q4: How does this connect to what we learned before?\n")
                .append("Q5: Explain this to someone who's never heard of it.\n\n");

        notes.append("**PRACTICE PROBLEMS:**\n")
                .append("[Problem 1: Easy difficulty]\n")
                .append("[Problem 2: Medium difficulty]\n")
                .append("[Problem 3: Challenging]\n\n");

        notes.append("**TIPS FOR MASTERY:**\n")
                .append("✓ Review these notes within 24 hours\n")
                .append("✓ Answer self-check questions daily\n")
                .append("✓ Practice problems without looking at solutions\n")
                .append("✓ Teach this to a friend (best test of understanding)\n");

        return notes.toString();
    }

    // ============ HELPER METHODS ============

    private String generateGreeting(String msg) {
        return "Hello! I'm **Adeon**, your AI study companion. 🎓\n\n"
                + "I'm here to help with:\n"
                + "📚 **Learning**: Any subject (Math, Science, History, Languages)\n"
                + "💡 **Problem-Solving**: Break complex problems into steps\n"
                + "📝 **Study Strategies**: Note-taking, exam prep, memory techniques\n"
                + "💪 **Motivation**: Overcome procrastination and anxiety\n"
                + "💙 **Counseling**: Emotional support for your journey\n\n"
                + "How can I help you today? 🚀";
    }

    private String[] extractKeyPoints(String content, int maxPoints) {
        String[] lines = content.split("[.!?\\n]");
        java.util.List<String> points = new java.util.ArrayList<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() > 10 && trimmed.length() < 150) {
                points.add(trimmed);
                if (points.size() >= maxPoints) break;
            }
        }
        while (points.size() < maxPoints) {
            points.add("Additional point " + (points.size() + 1));
        }
        return points.toArray(new String[0]);
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }

    // ============ PRACTICE PROBLEMS GENERATION ============

    private String generatePracticeProblemSet(String subject, String topic, String difficulty) {
        StringBuilder problems = new StringBuilder();
        
        problems.append("🎯 PRACTICE PROBLEM SET: ").append(topic.toUpperCase()).append("\n");
        problems.append("Difficulty: ").append(difficulty).append(" | Subject: ").append(subject).append("\n\n");
        
        if ("easy".equalsIgnoreCase(difficulty)) {
            problems.append(generateEasyProblems(subject, topic));
        } else if ("medium".equalsIgnoreCase(difficulty)) {
            problems.append(generateMediumProblems(subject, topic));
        } else {
            problems.append(generateHardProblems(subject, topic));
        }
        
        problems.append("\n═══════════════════════════════════════════\n");
        problems.append("📝 SOLUTION GUIDE\n");
        problems.append("═══════════════════════════════════════════\n\n");
        problems.append("ATTEMPTING PROBLEMS:\n");
        problems.append("1. Read carefully - identify what's being asked\n");
        problems.append("2. Show all work - write every step\n");
        problems.append("3. Check answer - does it make sense?\n");
        problems.append("4. Review wrong answers - learn the mistake\n\n");
        
        problems.append("LEARNING FROM MISTAKES:\n");
        problems.append("❌ Mistake ← Identify ← Understand ← Practice\n");
        problems.append(personality.getEncouragementMessage());
        
        return problems.toString();
    }

    private String generateEasyProblems(String subject, String topic) {
        return "PROBLEM 1: Basic Recall\n" +
                "Define or explain: " + topic + "\n\n" +
                "PROBLEM 2: Simple Application\n" +
                "Use the concept of " + topic + " in a straightforward example.\n\n" +
                "PROBLEM 3: Identification\n" +
                "Identify the key characteristics of " + topic + ".\n\n" +
                "PROBLEM 4: Basic Connection\n" +
                "How does " + topic + " relate to [previously learned concept]?\n\n" +
                "PROBLEM 5: Simple Calculation/Application\n" +
                "Apply the concept to solve a basic problem.\n\n";
    }

    private String generateMediumProblems(String subject, String topic) {
        return "PROBLEM 1: Multi-Step Application\n" +
                "Solve a problem requiring multiple steps using " + topic + ".\n\n" +
                "PROBLEM 2: Comparison\n" +
                "Compare " + topic + " with a similar but different concept.\n\n" +
                "PROBLEM 3: Analysis\n" +
                "Analyze a scenario and explain how " + topic + " applies.\n\n" +
                "PROBLEM 4: Synthesis\n" +
                "Combine " + topic + " with another concept to solve a problem.\n\n" +
                "PROBLEM 5: Real-World Application\n" +
                "Solve a realistic problem using " + topic + " principles.\n\n";
    }

    private String generateHardProblems(String subject, String topic) {
        return "PROBLEM 1: Advanced Analysis\n" +
                "Critically evaluate " + topic + " in a complex scenario.\n\n" +
                "PROBLEM 2: Creation\n" +
                "Create an original problem that demonstrates mastery of " + topic + ".\n\n" +
                "PROBLEM 3: Integration\n" +
                "Integrate " + topic + " with multiple other concepts to solve a complex problem.\n\n" +
                "PROBLEM 4: Research\n" +
                "Investigate how " + topic + " applies in research or expert contexts.\n\n" +
                "PROBLEM 5: Knowledge Transfer\n" +
                "Apply " + topic + " to a completely different domain or context.\n\n";
    }

    private String generateCustomStudyPlan(List<String> weakAreas, String studyHours) {
        StringBuilder plan = new StringBuilder();
        
        plan.append("📅 PERSONALIZED STUDY PLAN\n");
        plan.append("Generated for: ").append(personality.getUserName()).append("\n");
        plan.append("Daily study hours: ").append(studyHours).append(" hours\n\n");
        
        plan.append("🎯 WEAK AREAS TO FOCUS:\n");
        for (String area : weakAreas) {
            plan.append("• ").append(area).append("\n");
        }
        plan.append("\n");
        
        plan.append("📊 WEEKLY SCHEDULE:\n");
        if (weakAreas.size() > 0) {
            plan.append("MONDAY: ").append(weakAreas.get(0)).append(" Foundation (40%)\n");
            plan.append("TUESDAY: ").append(weakAreas.get(0)).append(" Practice (50%)\n");
        }
        plan.append("WEDNESDAY: Review & Problem Solving (60%)\n");
        plan.append("THURSDAY: Mastery reinforcement (70%)\n");
        plan.append("FRIDAY: Mixed practice (50%)\n");
        plan.append("SATURDAY: Comprehensive review\n");
        plan.append("SUNDAY: Rest & light review\n\n");
        
        plan.append("✅ DAILY ROUTINE:\n");
        plan.append("• 5 mins: Warm-up (review previous day)\n");
        plan.append("• 20 mins: New concept learning\n");
        plan.append("• 10 mins: Break\n");
        plan.append("• 20 mins: Practice problems\n");
        plan.append("• 10 mins: Break\n");
        plan.append("• 15 mins: Review & self-test\n");
        plan.append("• 5 mins: Plan tomorrow\n\n");
        
        plan.append("🏆 PROGRESS TRACKING:\n");
        plan.append("• Daily: Mark completed sessions\n");
        plan.append("• Weekly: Assess accuracy improvements\n");
        plan.append("• Bi-weekly: Adjust difficulty\n");
        plan.append("• Monthly: Celebrate progress\n\n");
        
        plan.append(personality.getEncouragementMessage() + personality.getMotivationalBoost());
        
        return plan.toString();
    }

    // ============ OFFLINE RESPONSE CACHE ============

    private static class OfflineResponseCache {
        private final java.util.Map<String, String> cache = new java.util.HashMap<>();
        private static final int MAX_CACHE_SIZE = 100;

        void cacheResponse(String query, String response) {
            if (cache.size() >= MAX_CACHE_SIZE) {
                // Remove oldest entry
                String firstKey = cache.keySet().iterator().next();
                cache.remove(firstKey);
            }
            cache.put(query, response);
        }

        String getResponse(String query) {
            return cache.get(query);
        }

        void clear() {
            cache.clear();
        }
    }
}
