package com.stdili.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Practice question model for Adeon AI
 */
public class PracticeQuestion {

    public enum Difficulty {
        EASY(1, "Easy"),
        MEDIUM(2, "Medium"),
        HARD(3, "Hard");

        private final int level;
        private final String label;

        Difficulty(int level, String label) {
            this.level = level;
            this.label = label;
        }

        public String getLabel() { return label; }
        public int getLevel() { return level; }
    }

    private String questionNumber;
    private String question;
    private String topic;
    private Difficulty difficulty;
    private List<String> options;
    private int correctOptionIndex;
    private String explanation;
    private String concept;
    private boolean userAnswered;
    private int userAnswerIndex;
    private boolean isCorrect;

    public PracticeQuestion(String question, String topic, Difficulty difficulty) {
        this.question = question;
        this.topic = topic;
        this.difficulty = difficulty;
        this.options = new ArrayList<>();
        this.userAnswered = false;
    }

    // Getters and Setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getTopic() { return topic; }
    public String getDifficultyLabel() { return difficulty.getLabel(); }

    public List<String> getOptions() { return options; }
    public void addOption(String option) { options.add(option); }

    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public void setCorrectOptionIndex(int index) { this.correctOptionIndex = index; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getConcept() { return concept; }
    public void setConcept(String concept) { this.concept = concept; }

    public void recordAnswer(int userChoice) {
        this.userAnswered = true;
        this.userAnswerIndex = userChoice;
        this.isCorrect = (userChoice == correctOptionIndex);
    }

    public boolean isUserAnswered() { return userAnswered; }
    public int getUserAnswerIndex() { return userAnswerIndex; }
    public boolean isAnswerCorrect() { return isCorrect; }

    public String getQuestionNumber() { return questionNumber; }
    public void setQuestionNumber(String num) { this.questionNumber = num; }

    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n【").append(questionNumber).append("】 [").append(difficulty.getLabel()).append("]\n");
        sb.append(question).append("\n\n");

        for (int i = 0; i < options.size(); i++) {
            sb.append(String.format("%s) %s\n", (char)('A' + i), options.get(i)));
        }

        if (userAnswered) {
            sb.append("\n└─ Your Answer: ").append((char)('A' + userAnswerIndex)).append(" - ");
            sb.append(isCorrect ? "✅ CORRECT" : "❌ INCORRECT").append("\n");
            sb.append("   Explanation: ").append(explanation).append("\n");
        }

        return sb.toString();
    }
}
