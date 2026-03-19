# StudyLife App - Quick Deployment Guide (8 Days to Hackathon)

## Timeline: March 18 - March 26, 2026

### ⚡ READY TO DEPLOY - What You Have NOW:

✅ **4 Activities Created**:
- OneOnOneChatActivity (40 lines)
- AINoteGeneratorActivity (50 lines)
- ExamConductorActivity (120 lines)
- CommunityGroupActivity (60 lines)
- GroupChatActivity (40 lines)
- CreateGroupActivity (50 lines)

✅ **8 Core Services**:
- ChatService (Firebase integration)
- NotesService (AI notes management)
- ExamService (Proctoring with camera)
- TextToSpeechService (Voice for AI Counsellor)
- AIAvatarService (Expression management)
- GeminiAIService (AI responses via Gemini)
- CameraMonitoringService (ML Kit integration)
- ExamTimerService (Exam countdown)

✅ **6 Models**: ChatMessage, CommunityGroup, ExamSession, StudyNote, UserProfile, GroupMember

---

## IMMEDIATE NEXT STEPS (Today - Day 1)

### Step 1: Update AndroidManifest.xml (30 min)
Copy content from `MANIFEST_ADDITIONS.xml` and paste into your `AndroidManifest.xml` inside `<application>` tag

```xml
<!-- Copy these 6 activity declarations -->
<activity android:name=".activities.OneOnOneChatActivity" />
<activity android:name=".activities.AINoteGeneratorActivity" />
... etc

<!-- Copy these 5 permission declarations -->
<uses-permission android:name="android.permission.CAMERA" />
... etc
```

### Step 2: Update build.gradle (1 hour)
Copy content from `GRADLE_ADDITIONS.txt` and add dependencies

```gradle
dependencies {
    implementation 'com.google.mlkit:face-detection:16.1.5'
    implementation 'com.google.ai.client.generativeai:google-generativeai-android:0.3.0'
    ... rest
}
```

### Step 3: Get Gemini API Key (30 min)
1. Go to: https://ai.google.dev/
2. Click "Get API Key"
3. Create a new project (or use existing)
4. Copy API key
5. Add to build.gradle:
```gradle
buildConfigField "String", "GEMINI_API_KEY", "\"YOUR_KEY_HERE\""
```

### Step 4: Firebase Setup (1 hour)
1. Go to Firebase Console: console.firebase.google.com
2. Create new project or use existing
3. Enable Firestore Database
4. Enable Authentication (Anonymous)
5. Download `google-services.json`
6. Place in `app/` folder

### Step 5: Add Missing Listener Method Implementations (2 hours)
Use `SERVICE_INTERFACES_REFERENCE.txt` to add missing methods to:
- ChatService.java
- NotesService.java
- ExamService.java

---

## DAY 1-2: QUICK FIXES FOR COMPILATION

Your Activities reference methods that need to exist. Add these quickly:

### In ChatService.java, add:
```java
// After existing methods...

public static String getChatRoomId(String userId1, String userId2) {
    // Generate consistent room ID by sorting IDs
    if (userId1.compareTo(userId2) < 0) {
        return userId1 + "_" + userId2;
    } else {
        return userId2 + "_" + userId1;
    }
}

public void loadOneOnOneChat(String userId1, String userId2, OnMessagesLoadedListener listener) {
    String roomId = getChatRoomId(userId1, userId2);
    db.collection("chatRooms").document(roomId).collection("messages")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .limit(50)
        .get()
        .addOnSuccessListener(querySnapshot -> {
            List<ChatMessage> messages = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                ChatMessage msg = doc.toObject(ChatMessage.class);
                if (msg != null) messages.add(msg);
            }
            listener.onMessagesLoaded(messages);
        })
        .addOnFailureListener(e -> listener.onError(e.getMessage()));
}

public void getCommunityGroups(OnGroupsLoadedListener listener) {
    db.collection("communityGroups")
        .whereEqualTo("isActive", true)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener(querySnapshot -> {
            List<CommunityGroup> groups = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                CommunityGroup group = doc.toObject(CommunityGroup.class);
                if (group != null) groups.add(group);
            }
            listener.onGroupsLoaded(groups);
        })
        .addOnFailureListener(e -> listener.onError(e.getMessage()));
}
```

### In NotesService.java, add:
```java
public void saveNote(StudyNote note, OnNoteSavedListener listener) {
    note.setCreatedAt(System.currentTimeMillis());
    db.collection("notes")
        .add(note)
        .addOnSuccessListener(docRef -> listener.onNoteSaved(docRef.getId()))
        .addOnFailureListener(e -> listener.onError(e.getMessage()));
}
```

### In ExamService.java, add:
```java
public void startExamSession(String examId, String studentId, OnExamStartedListener listener) {
    db.collection("exams").document(examId)
        .update("status", "ongoing")
        .addOnSuccessListener(aVoid -> listener.onExamStarted())
        .addOnFailureListener(e -> listener.onError(e.getMessage()));
}

public void endExamSession(String examId, String studentId, int score, OnExamEndedListener listener) {
    String reportId = System.currentTimeMillis() + "_" + studentId;
    db.collection("exams").document(examId)
        .collection("results").document(studentId)
        .set(new HashMap<String, Object>() {{
            put("score", score);
            put("completedAt", System.currentTimeMillis());
        }})
        .addOnSuccessListener(v -> listener.onExamEnded(reportId))
        .addOnFailureListener(e -> listener.onError(e.getMessage()));
}

public void recordSuspiciousActivity(String examId, String studentId, String activityType, String description, OnActivityRecordedListener listener) {
    db.collection("exams").document(examId)
        .collection("monitoring").document(studentId)
        .collection("incidents")
        .add(new HashMap<String, Object>() {{
            put("type", activityType);
            put("description", description);
            put("timestamp", System.currentTimeMillis());
            put("severity", calculateSeverity(activityType));
        }})
        .addOnSuccessListener(v -> listener.onActivityRecorded())
        .addOnFailureListener(e -> listener.onError(e.getMessage()));
}

private String calculateSeverity(String activityType) {
    if ("none_in_frame".equals(activityType) || "multiple_faces".equals(activityType)) {
        return "high";
    } else if ("tab_switch".equals(activityType) || "phone_detected".equals(activityType)) {
        return "medium";
    }
    return "low";
}
```

---

## DAY 2-3: GET IT RUNNING

### Setup Emulator or Device
```bash
# If using Android Emulator, create one with Google Play Services
# OR connect physical device via USB

# Build
./gradlew build

# Install
./gradlew installDebug
```

### Test Each Feature
1. **One-on-One Chat**
   - Open app
   - Navigate to chat section
   - Send a message
   - Should appear immediately

2. **Community Groups**
   - As teacher/senior, create a group
   - Join as student
   - Send group message

3. **AI Notes**
   - Enter subject/topic
   - Click "Generate"
   - Should call Gemini API
   - Display generated notes

4. **Exam**
   - Enter exam section
   - Click "Start Exam"
   - Camera should initialize
   - Timer should count down

---

## DAY 3-4: FIREBASE RULES

Create these Firestore Security Rules:

```firestore
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /chatRooms/{roomId} {
      allow read, write: if request.auth.uid in resource.data.participants;
      match /messages/{messageId} {
        allow read, write: if request.auth.uid in get(/databases/$(database)/documents/chatRooms/$(roomId)).data.participants;
      }
    }
    
    match /communityGroups/{groupId} {
      allow read: if request.auth.uid in resource.data.members;
      allow create: if request.resource.data.creatorRole in ["teacher", "senior"];
      match /messages/{messageId} {
        allow read: if request.auth.uid in get(/databases/$(database)/documents/communityGroups/$(groupId)).data.members;
        allow create: if request.auth.uid in get(/databases/$(database)/documents/communityGroups/$(groupId)).data.members;
      }
    }
    
    match /exams/{examId} {
      allow read, write: if resource.data.createdBy == request.auth.uid;
      match /monitoring/{studentId} {
        allow write: if studentId == request.auth.uid;
        allow read: if studentId == request.auth.uid || resource.data.createdBy == request.auth.uid;
      }
    }
    
    match /notes/{noteId} {
      allow read, write: if resource.data.userId == request.auth.uid;
    }
    
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }
  }
}
```

---

## CRITICAL FIXES / ADDITIONAL CODE

### Make sure you have strings.xml entry:
```xml
<!-- In res/values/strings.xml -->
<string name="gemini_api_key">YOUR_API_KEY</string>
```

### For Activities to find views, ensure layouts exist:
```
✅ activity_one_on_one_chat.xml
✅ activity_ai_notes_generator.xml
✅ activity_exam_conductor.xml
✅ activity_community_groups.xml
✅ activity_group_chat.xml
✅ activity_create_group.xml
```
All layouts are already created!

---

## TROUBLESHOOTING

**If build fails**:
```bash
./gradlew clean
./gradlew build
```

**If compilation errors**:
- Check package names match
- Ensure all imports are correct
- Verify R references exist

**If Firebase not connecting**:
- Check google-services.json in app/ folder
- Verify Firebase project ID in google-services.json
- Check AndroidManifest.xml has Firebase meta-data

**If Camera errors**:
- Permissions must be granted at runtime in Android 6+
- Check AndroidManifest.xml has camera permission
- Test on actual device (emulator camera can be finicky)

---

## DEPLOYMENT CHECKLIST

- [ ] update AndroidManifest.xml (30 min)
- [ ] Add dependencies to build.gradle (1 hour)
- [ ] Get Gemini API key (30 min)
- [ ] Setup Firebase project (1 hour)
- [ ] Add missing methods to services (2 hours)
- [ ] Build and test (1 hour)
- [ ] Deploy Firebase rules (30 min)
- [ ] Final testing on device (1 hour)

**Total Time**: ~8 hours across Days 1-3

---

## DEMO READY

After above steps, your app will have:
✅ One-on-one messaging (tested)
✅ Community groups (created/joined/chat)
✅ AI-generated notes (via Gemini)
✅ Exam proctoring (camera + timer)
✅ Premium UI (blue headers, yellow buttons)

**Ready for Hackathon Demo!**

---

**Next**: Implement 10 Feature Upgrades (Days 4-8)
See "10_HACKATHON_FEATURES.md" for suggested additions

---

*Deployment Guide Created: March 18, 2026*
*Estimated Readiness: March 21, 2026*
*Hackathon Date: March 26, 2026*
