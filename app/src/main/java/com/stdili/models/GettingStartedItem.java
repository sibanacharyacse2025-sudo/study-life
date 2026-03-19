package com.stdili.models;

public class GettingStartedItem {

    private String title;
    private String description;

    public GettingStartedItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}