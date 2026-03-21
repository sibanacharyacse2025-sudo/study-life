package com.stdili.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks user learning progress, weak areas, study history, and goals
 */
public class UserProgress {

    private String userId;
    private String userName;
    private Map<String, TopicProgress> topicProgress;
    private List<StudySession> studySessions;
    private List<String> goals;
    private int streakDays;
    private Date lastStudyDate;
    private Map<String, Integer> accuracyByTopic;

    public UserProgress() {
        this.topicProgress = new HashMap<>();
        this.studySessions = new ArrayList<>();
        this.goals = new ArrayList<>();
        this.accuracyByTopic = new HashMap<>();
        this.streakDays = 0;
    }

    public UserProgress(String userId, String userName) {
        this();
        this.userId = userId;
        this.userName = userName;
    }

    // Progress tracking per topic
    public static class TopicProgress {
        public String topicName;
        public int totalQuestions;
        public int correctAnswers;
        public int incorrectAnswers;
        public List<String> mistakes;
        public Date lastStudied;
        public int difficulty;

        public TopicProgress(String topicName) {
            this.topicName = topicName;
            this.totalQuestions = 0;
            this.correctAnswers = 0;
            this.incorrectAnswers = 0;
            this.mistakes = new ArrayList<>();
            this.difficulty = 1;
        }

        public int getAccuracy() {
            if (totalQuestions == 0) return 0;
            return (correctAnswers * 100) / totalQuestions;
        }

        public void recordAttempt(boolean correct, String mistakeNote) {
            totalQuestions++;
            if (correct) {
                correctAnswers++;
            } else {
                incorrectAnswers++;
                if (mistakeNote != null && !mistakeNote.isEmpty()) {
                    mistakes.add(mistakeNote);
                }
            }
            this.lastStudied = new Date();
        }
    }

    // Study session tracking
    public static class StudySession {
        public String topic;
        public long durationMinutes;
        public Date date;
        public int questionsAttempted;
        public int questionsCorrect;
        public String notes;

        public StudySession(String topic, long durationMinutes, int attempted, int correct) {
            this.topic = topic;
            this.durationMinutes = durationMinutes;
            this.date = new Date();
            this.questionsAttempted = attempted;
            this.questionsCorrect = correct;
        }

        public int getAccuracy() {
            if (questionsAttempted == 0) return 0;
            return (questionsCorrect * 100) / questionsAttempted;
        }
    }

    // Getters and setters
    public void recordTopicAttempt(String topic, boolean correct, String mistake) {
        if (!topicProgress.containsKey(topic)) {
            topicProgress.put(topic, new TopicProgress(topic));
        }
        topicProgress.get(topic).recordAttempt(correct, mistake);
        accuracyByTopic.put(topic, topicProgress.get(topic).getAccuracy());
    }

    public void addStudySession(StudySession session) {
        studySessions.add(session);
        updateStreak();
    }

    private void updateStreak() {
        Date today = new Date();
        if (lastStudyDate == null || daysBetween(lastStudyDate, today) > 1) {
            streakDays = 1;
        } else if (daysBetween(lastStudyDate, today) == 1) {
            streakDays++;
        }
        lastStudyDate = today;
    }

    private long daysBetween(Date d1, Date d2) {
        return (d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000);
    }

    public List<String> getWeakAreas(int threshold) {
        List<String> weak = new ArrayList<>();
        for (Map.Entry<String, TopicProgress> entry : topicProgress.entrySet()) {
            if (entry.getValue().getAccuracy() < threshold) {
                weak.add(entry.getKey());
            }
        }
        return weak;
    }

    public List<String> getStrengthAreas(int threshold) {
        List<String> strong = new ArrayList<>();
        for (Map.Entry<String, TopicProgress> entry : topicProgress.entrySet()) {
            if (entry.getValue().getAccuracy() >= threshold) {
                strong.add(entry.getKey());
            }
        }
        return strong;
    }

    public int getOverallAccuracy() {
        if (topicProgress.isEmpty()) return 0;
        int total = 0;
        for (TopicProgress tp : topicProgress.values()) {
            total += tp.getAccuracy();
        }
        return total / topicProgress.size();
    }

    public long getTotalStudyTime() {
        long total = 0;
        for (StudySession session : studySessions) {
            total += session.durationMinutes;
        }
        return total / 60; // return in hours
    }

    // Getters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Map<String, TopicProgress> getTopicProgress() { return topicProgress; }
    public List<StudySession> getStudySessions() { return studySessions; }
    public List<String> getGoals() { return goals; }
    public void addGoal(String goal) { goals.add(goal); }

    public int getStreakDays() { return streakDays; }
    public Date getLastStudyDate() { return lastStudyDate; }
    public Map<String, Integer> getAccuracyByTopic() { return accuracyByTopic; }
    public TopicProgress getTopicProgress(String topic) { return topicProgress.get(topic); }
}
