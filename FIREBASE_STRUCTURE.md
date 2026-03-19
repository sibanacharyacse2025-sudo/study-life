# Firebase Firestore Structure - Quick Reference

## Collections Overview

### 1. chatRooms Collection
Used for one-on-one messaging between users

```
chatRooms/{roomId}
├── participants: [userId1, userId2]        // Array of 2 user IDs
├── lastMessageTime: 1234567890             // Timestamp
├── lastMessageText: "Hey, how are you?"    // Preview
└── messages  [subcollection]
    └── {messageId}
        ├── senderId: "user123"
        ├── senderName: "John Doe"
        ├── senderAvatar: "url/to/avatar"
        ├── receiverId: "user456"
        ├── messageText: "Hello there!"
        ├── messageType: "text"             // text|image|file
        ├── timestamp: 1234567890
        └── isRead: true
```

**Usage in Code**:
```java
chatRoomId = ChatService.getChatRoomId(userId1, userId2);
// Generates consistent ID: sha256(sorted IDs)
```

---

### 2. communityGroups Collection
Teacher/senior-created discussion groups

```
communityGroups/{groupId}
├── groupId: "auto-generated"
├── groupName: "JEE Main Preparation 2024"
├── groupDescription: "Focused group for..."
├── groupIcon: "url/to/icon"              // Optional
├── createdBy: "teacher123"                // User ID of creator
├── creatorRole: "teacher"                 // MUST be "teacher" or "senior"
├── createdAt: 1234567890
├── category: "Studies"                    // Studies|Projects|Career|General
├── members: [userId1, userId2, ...]      // Array of all member IDs
├── memberCount: 45
├── isActive: true
└── messages  [subcollection]
    └── {messageId}
        ├── senderId: "student123"
        ├── senderName: "Alice"
        ├── senderAvatar: "url/to/avatar"
        ├── messageText: "How to solve this?"
        ├── messageType: "text"
        ├── timestamp: 1234567890
        └── isRead: true                   // Not needed for groups
```

**Key Constraint**: 
```java
// In ChatService.createCommunityGroup():
if (!creatorRole.equals("teacher") && !creatorRole.equals("senior")) {
    listener.onError("Only teachers and seniors can create groups");
    return;
}
```

---

### 3. exams Collection
Exam sessions with proctoring data

```
exams/{examId}
├── examId: "auto-generated"
├── examName: "JEE Main Mock Test 1"
├── title: "Physics - Mechanics"
├── description: "Full syllabus mock exam"
├── duration: 180                          // Minutes
├── totalQuestions: 90
├── createdBy: "teacher123"
├── status: "scheduled"                    // scheduled|ongoing|completed
├── cameraMandatory: true
├── recordSession: true
├── startTime: 1234567890
├── endTime: 1234571490
├── studentIds: [student1, student2, ...]  // Optional: allowed students
└── monitoring  [subcollection]
    ├── {studentId}  [document]
    │   ├── studentId: "student1"
    │   ├── startTime: 1234567890
    │   ├── cameraActive: true
    │   ├── flagCount: 3                   // Total suspicious incidents
    │   ├── consecutiveFlags: 0            // Current streak
    │   └── incidents  [subcollection]
    │       └── {incidentId}
    │           ├── type: "none_in_frame"  // See below
    │           ├── description: "Student not visible"
    │           ├── timestamp: 1234567900
    │           ├── severity: "high"       // high|medium|low
    │           └── resolution: "manual"   // auto|manual|ignored
    │
    └── results  [subcollection]
        └── {studentId}
            ├── studentId: "student1"
            ├── score: 85
            ├── completedAt: 1234571490
            └── violationCount: 2
```

**Incident Types & Severity**:
| Type | Severity | Meaning |
|------|----------|---------|
| none_in_frame | HIGH | No face detected in camera |
| multiple_faces | HIGH | More than one person in frame |
| tab_switch | MEDIUM | Student switched app/tab |
| phone_detected | MEDIUM | Phone detected in hand |
| unusual_movement | LOW | Suspicious hand/body movement |
| noise_level | MEDIUM | High noise detected |

---

### 4. notes Collection
Study notes - manual and AI-generated

```
notes/{noteId}
├── noteId: "auto-generated"
├── userId: "student123"
├── title: "Newton Laws of Motion"
├── content: "Detailed note content..."
├── summary: "Quick summary..."
├── subject: "Physics"
├── topic: "Mechanics"
├── generatedBy: "ai"                      // ai|manual
├── aiModel: "gemini-pro"                  // If AI-generated
├── createdAt: 1234567890
├── updatedAt: 1234567890
├── isFavorite: true
├── rating: 4.5                            // 1-5 stars
├── tags: [mechanics, physics, neet]      // Search tags
└── imageUrls: []                          // Optional attachments
```

---

### 5. userProfiles Collection (Optional but Recommended)
User information for messaging and group features

```
users/{userId}
├── userId: "user123"
├── userName: "John Doe"
├── userEmail: "john@example.com"
├── userRole: "student"                    // student|senior|teacher
├── userAvatar: "url/to/avatar"
├── userBio: "Biology enthusiast"
├── reputation: 150                        // Earned from group contributions
├── joinDate: 1234567890
├── isOnline: true
├── lastSeenTime: 1234567890
├── expertise: "Physics, Chemistry"        // For seniors
├── totalHelpedStudents: 25
├── preferences: {
│   ├── emailNotifications: true
│   └── soundNotifications: true
└── }
```

---

## Firestore Query Examples

### Load one-on-one chat
```firestore
Query:
  chatRooms/{roomId}/messages
  .orderBy("timestamp", "desc")
  .limit(50)

Result: Last 50 messages in reverse order
```

### Load community groups for user
```firestore
Query:
  communityGroups
  .where("isActive", "==", true)
  .orderBy("createdAt", "desc")

Filter on client:
  groups where members[] includes userId
```

### Get monitoring report for exam
```firestore
Query:
  exams/{examId}/monitoring/{studentId}/incidents
  .orderBy("timestamp", "desc")

Calculate:
  - Total flags: sum of all incidents
  - Alert level: if high-severity incidents, mark as "suspicious"
  - Time violations: cross-reference with exam duration
```

### Load AI-generated notes only
```firestore
Query:
  notes
  .where("userId", "==", currentUserid)
  .where("generatedBy", "==", "ai")
  .orderBy("createdAt", "desc")

Result: Only AI-generated notes for user
```

---

## Important Field Rules

### chatRooms
- `participants`: MUST be exactly 2 unique user IDs
- Query: Sort both IDs to ensure consistent roomId
- Example: `getChatRoomId(uid1, uid2)` returns same ID regardless of order

### communityGroups
- `creatorRole`: **MUST validate server-side** that creator is "teacher" or "senior"
- `members`: Append user when joining, remove when leaving
- `memberCount`: Keep in sync with members array length
- `isActive`: Set to false when group is deleted

### exams
- `cameraMandatory`: Always true for this app version
- `status` transitions: only scheduled → ongoing → completed
- `flagCount`: Incremented with each incident (use Firestore FieldValue.increment(1))
- `incidents`: NEVER delete records, only set status to "ignored"

### notes
- `generatedBy`: Set to "ai" ONLY if from GeminiAIService.generateStudyNotes()
- `rating`: Scale 1-5 where 5 is "saved as favorite"
- Auto-index fields: userId, generatedBy, createdAt, isFavorite

---

## Data Size Estimates

### Storage
- 1,000 users × 1KB profile = 1 MB
- 10,000 exam sessions × 50KB monitoring data = 500 MB  
- 100,000 messages × 1KB = 100 MB
- **Total estimated**: ~1-2 GB for production-scale app

### Read/Write Operations (Free Tier)
- 50K reads/day allowed (free tier only)
- Monitor in Firebase Console
- Consider pagination to reduce reads

---

## Optimization Tips

1. **Paginate Messages**: Load 20 at a time, not all
   ```java
   chatService.loadOneOnOneChat(userId1, userId2, PAGE_SIZE=20, listener)
   ```

2. **Index Queries**: Add composite indexes for WHERE + ORDER BY
   ```
   collection: communityGroups
   fields: isActive (Asc), createdAt (Desc)
   ```

3. **Update Timestamps**: Use server-side FieldValue.serverTimestamp()
   ```java
   update.set("timestamp", FieldValue.serverTimestamp());
   ```

4. **Cache Locally**: Use Room DB for offline access
   ```java
   // Save messages to Room, sync when online
   ```

---

## Security Best Practices

✅ Already implemented:
- Role validation in ChatService.createCommunityGroup()
- User ID verification in chat room access
- Message sender verification

⚠️ Implement in Firebase Rules:
```firestore
// Only the message sender can edit their own message
allow update: if resource.data.senderId == request.auth.uid;

// Only group creator can delete group
allow delete: if resource.data.createdBy == request.auth.uid;
```

---

**Firestore Status**: Ready for production  
**Last Updated**: Phase 2 Implementation  
**Next**: Implement Activity classes and test queries
