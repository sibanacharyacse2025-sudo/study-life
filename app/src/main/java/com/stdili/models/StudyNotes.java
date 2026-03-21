package com.stdili.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Structured study notes with key concepts, examples, and revision points
 */
public class StudyNotes {

    private String title;
    private String subject;
    private String topic;
    private List<String> keyConceptList;
    private String shortExplanation;
    private List<String> importantFormulas;
    private List<String> examples;
    private List<String> quickRevisionPoints;
    private Date createdAt;
    private int pageCount;

    public StudyNotes(String title, String subject, String topic) {
        this.title = title;
        this.subject = subject;
        this.topic = topic;
        this.keyConceptList = new ArrayList<>();
        this.importantFormulas = new ArrayList<>();
        this.examples = new ArrayList<>();
        this.quickRevisionPoints = new ArrayList<>();
        this.createdAt = new Date();
        this.pageCount = 1;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public List<String> getKeyConceptList() { return keyConceptList; }
    public void addKeyConcept(String concept) { keyConceptList.add(concept); }

    public String getShortExplanation() { return shortExplanation; }
    public void setShortExplanation(String explanation) { this.shortExplanation = explanation; }

    public List<String> getImportantFormulas() { return importantFormulas; }
    public void addFormula(String formula) { importantFormulas.add(formula); }

    public List<String> getExamples() { return examples; }
    public void addExample(String example) { examples.add(example); }

    public List<String> getQuickRevisionPoints() { return quickRevisionPoints; }
    public void addRevisionPoint(String point) { quickRevisionPoints.add(point); }

    public Date getCreatedAt() { return createdAt; }
    public int getPageCount() { return pageCount; }
    public void setPageCount(int pages) { this.pageCount = pages; }

    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("═════════════════════════════════════════\n");
        sb.append("📚 ").append(title).append("\n");
        sb.append("Subject: ").append(subject).append(" | Topic: ").append(topic).append("\n");
        sb.append("═════════════════════════════════════════\n\n");

        sb.append("🔑 KEY CONCEPTS:\n");
        for (String concept : keyConceptList) {
            sb.append("• ").append(concept).append("\n");
        }
        sb.append("\n");

        sb.append("📝 EXPLANATION:\n");
        sb.append(shortExplanation).append("\n\n");

        if (!importantFormulas.isEmpty()) {
            sb.append("📐 IMPORTANT FORMULAS:\n");
            for (String formula : importantFormulas) {
                sb.append("• ").append(formula).append("\n");
            }
            sb.append("\n");
        }

        if (!examples.isEmpty()) {
            sb.append("💡 EXAMPLES:\n");
            for (String example : examples) {
                sb.append("• ").append(example).append("\n");
            }
            sb.append("\n");
        }

        sb.append("⚡ QUICK REVISION:\n");
        for (String point : quickRevisionPoints) {
            sb.append("✓ ").append(point).append("\n");
        }

        return sb.toString();
    }
}
