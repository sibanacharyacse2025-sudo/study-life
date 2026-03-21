package com.stdili.services;

import android.content.Context;
import android.util.Log;
import com.stdili.models.PracticeQuestion;
import com.stdili.models.StudyNotes;
import com.stdili.models.UserProgress;
import com.stdili.utils.NotificationHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced Adeon Service - Notes generation, practice problems, progress tracking
 * Agentic behavior with reminder system and accountability coaching
 */
public class EnhancedAdeonService {

    private static final String TAG = "EnhancedAdeonService";
    private Context context;
    private UserProgress userProgress;
    private Map<String, List<PracticeQuestion>> practiceQuestionBank;
    private NotificationHandler notificationHandler;

    public interface OnNotesGenerated {
        void onSuccess(StudyNotes notes);
        void onFailure(String error);
    }

    public interface OnPracticeGenerated {
        void onSuccess(List<PracticeQuestion> questions);
        void onFailure(String error);
    }

    public interface OnProgressAnalyzed {
        void onSuccess(String analysis);
        void onFailure(String error);
    }

    public EnhancedAdeonService(Context context) {
        this.context = context;
        this.userProgress = new UserProgress();
        this.practiceQuestionBank = new HashMap<>();
        this.notificationHandler = new NotificationHandler(context);
    }

    // ============ STRUCTURED NOTES GENERATION ============

    public void generateStructuredNotes(String subject, String topic, String content, 
                                       OnNotesGenerated listener) {
        if (isEmpty(subject) || isEmpty(topic) || isEmpty(content)) {
            listener.onFailure("Subject, topic, and content required");
            return;
        }

        try {
            StudyNotes notes = new StudyNotes(topic + " - Complete Guide", subject, topic);

            // Extract key concepts
            String[] concepts = extractConcepts(content, 5);
            for (String concept : concepts) {
                notes.addKeyConcept(concept);
            }

            // Short explanation
            notes.setShortExplanation(generateExplanation(content, subject, topic));

            // Important formulas (for Math/Science)
            if (subject.toLowerCase().contains("math") || subject.toLowerCase().contains("science")) {
                notes.addFormula(topic + " Formula 1: [To be filled based on content]");
                notes.addFormula(topic + " Formula 2: [To be filled based on content]");
            }

            // Examples
            notes.addExample("Example 1: Practical application of " + topic);
            notes.addExample("Example 2: Real-world use case");
            notes.addExample("Example 3: Advanced scenario");

            // Quick revision points
            notes.addRevisionPoint("Define " + topic + " in simple terms");
            notes.addRevisionPoint("State the 3 most important characteristics");
            notes.addRevisionPoint("List 2 real-life applications");
            notes.addRevisionPoint("Explain why this concept matters");

            listener.onSuccess(notes);
        } catch (Exception e) {
            Log.e(TAG, "Error generating notes", e);
            listener.onFailure("Failed to generate notes: " + e.getMessage());
        }
    }

    // ============ PRACTICE QUESTION GENERATION ============

    public void generatePracticeQuestions(String subject, String topic, 
                                         OnPracticeGenerated listener) {
        if (isEmpty(subject) || isEmpty(topic)) {
            listener.onFailure("Subject and topic required");
            return;
        }

        try {
            List<PracticeQuestion> questions = new ArrayList<>();

            // Generate mix of easy (3), medium (4), hard (3) = 10 questions
            int easyCount = 0, mediumCount = 0, hardCount = 0;

            for (int i = 1; i <= 10; i++) {
                PracticeQuestion q;

                if (easyCount < 3) {
                    q = createQuestion(i, subject, topic, PracticeQuestion.Difficulty.EASY);
                    easyCount++;
                } else if (mediumCount < 4) {
                    q = createQuestion(i, subject, topic, PracticeQuestion.Difficulty.MEDIUM);
                    mediumCount++;
                } else {
                    q = createQuestion(i, subject, topic, PracticeQuestion.Difficulty.HARD);
                    hardCount++;
                }

                questions.add(q);
            }

            listener.onSuccess(questions);
        } catch (Exception e) {
            Log.e(TAG, "Error generating practice questions", e);
            listener.onFailure("Failed to generate questions: " + e.getMessage());
        }
    }

    private PracticeQuestion createQuestion(int num, String subject, String topic, 
                                           PracticeQuestion.Difficulty difficulty) {
        String question = String.format("Question about %s in %s (Difficulty: %s)", 
                topic, subject, difficulty.getLabel());
        
        PracticeQuestion q = new PracticeQuestion(question, topic, difficulty);
        q.setQuestionNumber(String.valueOf(num));

        // Add options
        q.addOption("Option A - Correct response");
        q.addOption("Option B - Common misconception");
        q.addOption("Option C - Partially correct");
        q.addOption("Option D - Incorrect response");

        q.setCorrectOptionIndex(0);
        q.setExplanation("This is the correct answer because: [detailed explanation based on " + topic + "]");
        q.setConcept(topic);

        return q;
    }

    // ============ PROGRESS ANALYSIS ============

    public void analyzeProgress(OnProgressAnalyzed listener) {
        try {
            StringBuilder analysis = new StringBuilder();
            analysis.append("📊 YOUR LEARNING PROGRESS ANALYSIS\n");
            analysis.append("═══════════════════════════════════════════\n\n");

            // Overall accuracy
            int overallAccuracy = userProgress.getOverallAccuracy();
            analysis.append("📈 OVERALL PERFORMANCE: ").append(overallAccuracy).append("%\n");
            analysis.append(getPerformanceRating(overallAccuracy)).append("\n\n");

            // Weak areas (< 70%)
            List<String> weakAreas = userProgress.getWeakAreas(70);
            if (!weakAreas.isEmpty()) {
                analysis.append("⚠️  WEAK AREAS (Need Improvement):\n");
                for (String area : weakAreas) {
                    UserProgress.TopicProgress tp = userProgress.getTopicProgress(area);
                    if (tp != null) {
                        analysis.append("  • ").append(area).append(" - ").append(tp.getAccuracy()).append("% accuracy\n");
                    }
                }
                analysis.append("\n");
            }

            // Strong areas (≥ 80%)
            List<String> strongAreas = userProgress.getStrengthAreas(80);
            if (!strongAreas.isEmpty()) {
                analysis.append("✅ STRONG AREAS (Well Mastered):\n");
                for (String area : strongAreas) {
                    UserProgress.TopicProgress tp = userProgress.getTopicProgress(area);
                    if (tp != null) {
                        analysis.append("  • ").append(area).append(" - ").append(tp.getAccuracy()).append("% accuracy\n");
                    }
                }
                analysis.append("\n");
            }

            // Study streak
            analysis.append("🔥 STUDY STREAK: ").append(userProgress.getStreakDays()).append(" days\n");
            analysis.append("⏱️  TOTAL STUDY TIME: ").append(userProgress.getTotalStudyTime()).append(" hours\n\n");

            // Recommendations
            analysis.append("💡 IMPROVEMENT PLAN:\n");
            if (!weakAreas.isEmpty()) {
                analysis.append("1. PRIORITY: Focus on ").append(weakAreas.get(0)).append(" - Generate 10 practice problems\n");
                analysis.append("2. Daily target: Score ≥75% on ").append(weakAreas.get(0)).append(" practice set\n");
                analysis.append("3. Allocate 40% of study time to weak areas\n");
            } else {
                analysis.append("1. You're doing great! Focus on maintaining consistency\n");
                analysis.append("2. Challenge yourself with harder problems\n");
                analysis.append("3. Help others with your strong areas\n");
            }

            analysis.append("\n📅 DAILY TARGET: Study for 2 hours, attempt 20 problems, achieve 75% accuracy\n");

            listener.onSuccess(analysis.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error analyzing progress", e);
            listener.onFailure("Failed to analyze progress: " + e.getMessage());
        }
    }

    private String getPerformanceRating(int accuracy) {
        if (accuracy >= 90) return "🌟 EXCELLENT - Outstanding performance!";
        if (accuracy >= 80) return "⭐ VERY GOOD - Great work, keep it up!";
        if (accuracy >= 70) return "👍 GOOD - You're on track, improve weak areas";
        if (accuracy >= 60) return "📚 AVERAGE - More focus needed on concepts";
        return "⚠️  NEEDS IMPROVEMENT - Let's rebuild the basics";
    }

    // ============ REMINDER & NOTIFICATION AGENT ============

    public void createPlan(String userId, String planTitle, List<String> topics, 
                          int dailyHours, String dueDate) {
        Log.d(TAG, "Plan created: " + planTitle);
        
        // Send initial notification
        notificationHandler.notifyUser(userId, 
                "📅 New Plan Created: " + planTitle + "\n" +
                "Daily target: " + dailyHours + " hours\n" +
                "Topics: " + String.join(", ", topics));
    }

    public void sendDailyReminder(String userId, String userName) {
        Log.d(TAG, "Daily reminder sent to " + userName);
        notificationHandler.notifyUser(userId,
                "⏰ Time to study! 30 minutes from now.\n" +
                "Your goal today: Complete 10 practice problems\n" +
                "Keep your streak alive! 🔥");
    }

    public void sendStrictFollowUp(String userId, String userName, String missedTask) {
        Log.d(TAG, "Strict follow-up sent to " + userName);
        notificationHandler.notifyUser(userId,
                "⛔ MISSED GOAL: " + missedTask + "\n" +
                "This breaks your streak! ❌\n" +
                "Open the app NOW and complete this session.\n" +
                "No excuses - you've got this! 💪");
    }

    public void sendMotivationalPush(String userId, String userName, int streakDays) {
        Log.d(TAG, "Motivational push sent to " + userName);
        notificationHandler.notifyUser(userId,
                "🎉 AMAZING! You've maintained a " + streakDays + "-day streak!\n" +
                "Consistency is your superpower. Keep it up! 🚀\n" +
                "Next milestone: " + (streakDays + 5) + " days");
    }

    // ============ ACCOUNTABILITY COACH BEHAVIOR ============

    public String getAccountabilityMessage(UserProgress progress, boolean isLazy, boolean isConsistent) {
        StringBuilder message = new StringBuilder();

        if (isConsistent) {
            message.append("🌟 CONSISTENCY BONUS! You're crushing it!\n");
            message.append("Keep this momentum: Your dedication is paying off.\n");
            message.append("Reward: Unlock hard difficulty problems\n");
        } else if (isLazy) {
            message.append("⚠️  WAKE UP! You're slowing down.\n");
            message.append("No procrastination: Start with just 5 minutes NOW.\n");
            message.append("Remember: Action creates motivation, not the other way around.\n");
        } else {
            message.append("📖 Steady progress is still progress.\n");
            message.append("You're on the right track. Push a little harder.\n");
        }

        return message.toString();
    }

    public String getWeakAreaTutorial(String weakArea) {
        StringBuilder tutorial = new StringBuilder();
        tutorial.append("🔧 REBUILDING BASICS: ").append(weakArea).append("\n\n");
        tutorial.append("STEP 1: Understand the concept\n");
        tutorial.append("  → Read simple definition\n");
        tutorial.append("  → Watch 1 video explanation\n");
        tutorial.append("  → Note down key ideas\n\n");
        
        tutorial.append("STEP 2: Learn with examples\n");
        tutorial.append("  → Solve 5 beginner problems\n");
        tutorial.append("  → Check answers\n");
        tutorial.append("  → Understand mistakes\n\n");
        
        tutorial.append("STEP 3: Practice & master\n");
        tutorial.append("  → Solve 10 intermediate problems\n");
        tutorial.append("  → Achieve 80%+ accuracy\n");
        tutorial.append("  → Move to next difficulty\n\n");
        
        tutorial.append("TIMELINE: 3-5 days with 2 hours daily = Master ").append(weakArea).append("\n");
        
        return tutorial.toString();
    }

    // ============ HELPER METHODS ============

    private String[] extractConcepts(String content, int maxConcepts) {
        String[] lines = content.split("[.!?\\n]");
        List<String> concepts = new ArrayList<>();
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() > 10 && trimmed.length() < 150) {
                concepts.add(trimmed);
                if (concepts.size() >= maxConcepts) break;
            }
        }
        
        while (concepts.size() < maxConcepts) {
            concepts.add("Concept " + (concepts.size() + 1));
        }
        
        return concepts.toArray(new String[0]);
    }

    private String generateExplanation(String content, String subject, String topic) {
        String explanation = content.length() > 200 ? 
                content.substring(0, 200) + "..." : content;
        return "In " + subject + ", " + topic + " refers to: " + explanation;
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    // ============ PROGRESS TRACKING METHODS ============

    public void recordTopicAttempt(String topic, boolean correct, String mistake) {
        userProgress.recordTopicAttempt(topic, correct, mistake);
    }

    public void addStudySession(String topic, long durationMinutes, int attempted, int correct) {
        UserProgress.StudySession session = new UserProgress.StudySession(topic, durationMinutes, attempted, correct);
        userProgress.addStudySession(session);
    }

    public UserProgress getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(UserProgress progress) {
        this.userProgress = progress;
    }
}
