package com.stdili.models;

public class Message {

    private String text;
    private boolean isUser;
    private boolean rated;

    public Message(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
        this.rated = false;
    }

    public String getText() {
        return text;
    }

    public boolean isUser() {
        return isUser;
    }

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }
}