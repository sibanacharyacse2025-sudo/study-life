package com.stdili.models;

public class QuickAction {

    private String title;
    private int icon;

    public QuickAction(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }
}