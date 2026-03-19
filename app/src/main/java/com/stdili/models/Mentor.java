package com.stdili.models;

public class Mentor {

    private String name;
    private String subject;
    private boolean isOnline;

    public Mentor(String name, String subject, boolean isOnline) {
        this.name = name;
        this.subject = subject;
        this.isOnline = isOnline;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public boolean isOnline() {
        return isOnline;
    }
}