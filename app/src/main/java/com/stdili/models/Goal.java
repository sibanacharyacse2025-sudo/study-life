package com.stdili.models;

public class Goal {
    private String title;
    private boolean isCompleted;

    public Goal(String title) {
        this.title = title;
        this.isCompleted = false;
    }

    public String getTitle() { return title; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}