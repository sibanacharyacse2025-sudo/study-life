package com.stdili.models;

import java.util.List;

public class Group {

    private String id;
    private String name;
    private String description;
    private List<String> members;

    public Group() {}

    public Group(String name, String description, List<String> members) {
        this.name = name;
        this.description = description;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getMembers() {
        return members;
    }
}