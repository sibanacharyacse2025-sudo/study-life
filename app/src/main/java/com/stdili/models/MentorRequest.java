package com.stdili.models;

public class MentorRequest {

    private String id;
    private String juniorId;
    private String seniorId;
    private String juniorName;
    private String seniorName;
    private String subject;
    private String status; // pending, accepted, rejected
    private long timestamp;

    public MentorRequest() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJuniorId() {
        return juniorId;
    }

    public void setJuniorId(String juniorId) {
        this.juniorId = juniorId;
    }

    public String getSeniorId() {
        return seniorId;
    }

    public void setSeniorId(String seniorId) {
        this.seniorId = seniorId;
    }

    public String getJuniorName() {
        return juniorName;
    }

    public void setJuniorName(String juniorName) {
        this.juniorName = juniorName;
    }

    public String getSeniorName() {
        return seniorName;
    }

    public void setSeniorName(String seniorName) {
        this.seniorName = seniorName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}