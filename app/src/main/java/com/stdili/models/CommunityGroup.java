package com.stdili.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CommunityGroup implements Serializable {
    private String groupId;
    private String groupName;
    private String groupDescription;
    private String groupIcon;
    private String createdBy; // Only teacher/senior can create
    private String creatorRole; // "teacher" or "senior"
    private Date createdAt;
    private List<String> members;
    private int memberCount;
    private String category; // "Studies", "Projects", "Career", etc.
    private boolean isActive;

    public CommunityGroup() {}

    public CommunityGroup(String groupName, String createdBy, String creatorRole) {
        this.groupName = groupName;
        this.createdBy = createdBy;
        this.creatorRole = creatorRole;
        this.createdAt = new Date();
        this.isActive = true;
    }

    // Getters and Setters
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    
    public String getName() { return groupName; }
    public void setName(String name) { this.groupName = name; }

    public String getGroupDescription() { return groupDescription; }
    public void setGroupDescription(String groupDescription) { this.groupDescription = groupDescription; }
    
    public String getDescription() { return groupDescription; }
    public void setDescription(String description) { this.groupDescription = description; }

    public String getGroupIcon() { return groupIcon; }
    public void setGroupIcon(String groupIcon) { this.groupIcon = groupIcon; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public void setCreatorId(String creatorId) { this.createdBy = creatorId; }

    public String getCreatorRole() { return creatorRole; }
    public void setCreatorRole(String creatorRole) { this.creatorRole = creatorRole; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
