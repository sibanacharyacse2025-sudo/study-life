package com.stdili.models;

public class Peer {

    private String name;
    private String details;
    private String status;
    private boolean isOnline;

    public Peer(String name, String details, String status, boolean isOnline) {
        this.name = name;
        this.details = details;
        this.status = status;
        this.isOnline = isOnline;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOnline() {
        return isOnline;
    }
}