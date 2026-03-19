package com.stdili.models;

import java.io.Serializable;

public class ExamSession implements Serializable {
    private String examId;
    private String examName;
    private String examDescription;
    private String title;
    private String duration; // in minutes
    private int totalQuestions;
    private String createdBy;
    private String status; // "scheduled", "ongoing", "completed"
    private boolean cameraMandatory;
    private boolean recordSession;
    private long startTime;
    private long endTime;

    public ExamSession() {}

    public ExamSession(String examName, String title, String createdBy) {
        this.examName = examName;
        this.title = title;
        this.createdBy = createdBy;
        this.cameraMandatory = true;
        this.recordSession = true;
        this.status = "scheduled";
    }

    // Getters and Setters
    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getExamDescription() { return examDescription; }
    public void setExamDescription(String examDescription) { this.examDescription = examDescription; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isCameraMandatory() { return cameraMandatory; }
    public void setCameraMandatory(boolean cameraMandatory) { this.cameraMandatory = cameraMandatory; }

    public boolean isRecordSession() { return recordSession; }
    public void setRecordSession(boolean recordSession) { this.recordSession = recordSession; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
}
