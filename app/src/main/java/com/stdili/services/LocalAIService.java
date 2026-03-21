package com.stdili.services;

import java.util.Locale;

public class LocalAIService {

    public interface OnResponse {
        void onSuccess(String response);
        void onFailure(String error);
    }

    public void chatReply(String userMessage, OnResponse listener) {
        if (userMessage == null) {
            listener.onFailure("Empty message");
            return;
        }
        listener.onSuccess(generateStudyAssistantReply(userMessage));
    }

    public void counsellorReply(String userMessage, OnResponse listener) {
        if (userMessage == null) {
            listener.onFailure("Empty message");
            return;
        }
        listener.onSuccess(generateCounsellorReply(userMessage));
    }

    public void tutorReply(String subject, String topic, String question, OnResponse listener) {
        if (isBlank(subject) || isBlank(topic) || isBlank(question)) {
            listener.onFailure("Missing fields");
            return;
        }
        String response = generateTutorResponse(subject.trim(), topic.trim(), question.trim());
        listener.onSuccess(response);
    }

    public void generateStudyNotes(String subject, String topic, String content, OnResponse listener) {
        if (isBlank(subject) || isBlank(topic) || isBlank(content)) {
            listener.onFailure("Missing fields");
            return;
        }
        listener.onSuccess(generateNotes(subject.trim(), topic.trim(), content.trim()));
    }

    private String generateStudyAssistantReply(String msg) {
        String s = msg.trim().toLowerCase(Locale.ROOT);

        if (isPlanRequest(s)) {
            int hoursPerDay = extractHoursPerDay(s);
            return generateDailyPlan(hoursPerDay);
        }

        // (legacy path) if a plan was requested but we couldn't detect hours, still give a useful starter.
        if (s.contains("timetable") || s.contains("schedule") || s.contains("plan")) {
            return generateDailyPlan(2);
        }
        if (s.contains("pomodoro")) {
            return "Pomodoro is: 25 minutes focus + 5 minutes break. After 4 rounds, take a 15–20 minute break.\n"
                    + "Want me to set a plan for your next 2 hours?";
        }
        if (s.contains("note") || s.contains("notes")) {
            return "Tell me the subject + topic + paste your rough content. I can convert it into clean notes (key points, examples, and quick questions).";
        }
        if (s.contains("revision") || s.contains("revise")) {
            return "Best quick revision: active recall + spaced repetition.\n"
                    + "Try this: 10 min skim → 15 min quiz yourself → 5 min review mistakes.";
        }
        if (s.contains("math") || s.contains("physics") || s.contains("chemistry")) {
            return "For problem-heavy subjects: do 70% practice + 30% theory.\n"
                    + "Send one question you’re stuck on and I’ll break it into steps.";
        }
        if (s.endsWith("?")) {
            return "I can help. What grade/level is this for, and what part feels confusing?";
        }
        return "Got it. Tell me your subject and goal (exam/assignment/concept), and I’ll guide you step-by-step.";
    }

    private String generateCounsellorReply(String msg) {
        String s = msg.trim().toLowerCase(Locale.ROOT);

        if (s.contains("stress") || s.contains("stressed") || s.contains("anxious") || s.contains("anxiety")) {
            return "I hear you. Let’s reduce the load: pick 1 small task for the next 10 minutes.\n"
                    + "Also try: inhale 4s → hold 4s → exhale 6s (repeat 5 times).";
        }
        if (s.contains("sad") || s.contains("depressed") || s.contains("lonely")) {
            return "I’m sorry you’re feeling that way. You’re not alone.\n"
                    + "If you want, tell me what triggered it today—and we’ll make a small plan to stabilize your routine.";
        }
        if (s.contains("tired") || s.contains("sleep")) {
            return "If you’re tired, studying will feel 2x harder. Quick reset: water + 5 minute walk + 20 minute power nap if possible.\n"
                    + "Then do an easy task first to build momentum.";
        }
        if (s.contains("motivation") || s.contains("procrast") || s.contains("lazy")) {
            return "Motivation follows action. Start with a 5-minute rule: open the book and do just one tiny step.\n"
                    + "Tell me your subject and I’ll give you a 5-minute starter task.";
        }
        return "I’m here with you. Tell me what you’re working on and how you’re feeling right now, and we’ll take it one step at a time.";
    }

    private String generateNotes(String subject, String topic, String content) {
        StringBuilder sb = new StringBuilder();
        sb.append(subject).append(" — ").append(topic).append("\n\n");
        sb.append("Key points:\n");
        for (String p : splitToPoints(content)) {
            sb.append("- ").append(p).append("\n");
        }
        sb.append("\nQuick summary:\n");
        sb.append(summarize(content)).append("\n\n");
        sb.append("Self-check (answer in 1–2 lines):\n");
        sb.append("1) Define ").append(topic).append(".\n");
        sb.append("2) Write 2 key takeaways.\n");
        sb.append("3) Give one example related to ").append(topic).append(".\n");
        return sb.toString();
    }

    private String[] splitToPoints(String content) {
        String normalized = content.replace("\r", "\n");
        String[] raw = normalized.split("\n");
        java.util.List<String> pts = new java.util.ArrayList<>();
        for (String line : raw) {
            String t = line.trim();
            if (t.isEmpty()) continue;
            if (t.length() > 120) {
                pts.add(t.substring(0, 120) + "…");
            } else {
                pts.add(t);
            }
            if (pts.size() >= 8) break;
        }
        if (pts.isEmpty()) {
            String t = content.trim();
            if (t.length() > 160) t = t.substring(0, 160) + "…";
            pts.add(t);
        }
        return pts.toArray(new String[0]);
    }

    private String summarize(String content) {
        String t = content.trim().replace("\n", " ");
        t = t.replaceAll("\\s+", " ");
        if (t.length() <= 200) return t;
        return t.substring(0, 200) + "…";
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isPlanRequest(String s) {
        return s.contains("plan") || s.contains("timetable") || s.contains("schedule") || s.contains("routine");
    }

    private int extractHoursPerDay(String s) {
        // very small heuristic: finds first number near "hour"/"hr"
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)\\s*(hours|hour|hrs|hr)").matcher(s);
        if (m.find()) {
            try {
                int v = Integer.parseInt(m.group(1));
                if (v > 0 && v <= 12) return v;
            } catch (Exception ignored) {
            }
        }
        return 2;
    }

    private String generateDailyPlan(int hoursPerDay) {
        int blocks = hoursPerDay <= 1 ? 2 : (hoursPerDay <= 3 ? 4 : 6);
        return "Here’s your daily study plan (" + hoursPerDay + " hours/day):\n\n"
                + "1) Warm-up + recap (15 min)\n"
                + "2) Deep Study (45–60 min)\n"
                + "3) Practice / examples (45–60 min)\n"
                + "4) Quick revision + flash recap (15–20 min)\n\n"
                + "Next steps:\n"
                + "- Tell me your subjects + exam date\n"
                + "- I’ll tailor each block (what to study + what to practice) for your level.\n";
    }

    private String generateTutorResponse(String subject, String topic, String question) {
        String q = question;
        return "Sure! " + subject + " Tutor (" + topic + ")\n\n"
                + "Step 1 (Concept): In simple terms, " + topic + " means:\n"
                + "• Explain what it is and why it's useful for this question.\n\n"
                + "Step 2 (Method): Break the solution into parts:\n"
                + "• Given: " + q + "\n"
                + "• Use the main rule/formula for " + topic + "\n"
                + "• Solve systematically (show each step)\n\n"
                + "Step 3 (Example): Try this quick example:\n"
                + "• Do the first 2 steps, then compare your result.\n\n"
                + "Quick check:\n"
                + "1) What is the key idea/formula you used?\n"
                + "2) What step would you repeat if the numbers change?";
    }
}

