# StudyLife Phase 2 - Advanced Features Implementation Guide

## Implementation Status

### ✅ Foundation Layer - COMPLETED (20 Files)

#### Models (5 Files)
- `ChatMessage.java` - Message model with sender/receiver tracking
- `CommunityGroup.java` - Group model with role-based creation control
- `ExamSession.java` - Exam model with camera/recording flags
- `StudyNote.java` - Note model with AI generation tracking
- `UserProfile.java` - User profile with role verification
- `GroupMember.java` - Group membership tracking

#### Services (8 Files)
1. **ChatService** (Enhanced)
   - `saveOneOnOneMessage()` - DM functionality
   - `loadOneOnOneChat()` - Retrieve conversations
   - `saveGroupMessage()` - Community posting
   - `createCommunityGroup()` - **ENFORCES role validation (teacher/senior only)**
   - `getCommunityGroups()` - List groups

2. **NotesService**
   - `generateAINotes()` - Gemini integration ready
   - `saveNote()` - Persist notes
   - `getUserNotes()` - Retrieve personal notes
   - `toggleFavorite()` - Mark as favorites

3. **ExamService**
   - `createExamSession()` - Create exams
   - `startExamSession()` - Begin proctoring
   - `recordSuspiciousActivity()` - Track incidents with severity
   - `getMonitoringReport()` - Retrieve monitoring data

4. **TextToSpeechService**
   - `initialize()` - TTS engine setup
   - `speak()` - Read text aloud
   - `setSpeechRate()`, `setPitch()` - Customize voice
   - Supports English-India locale for education context

5. **AIAvatarService**
   - Expression states: neutral, happy, thinking, listening, concerned, encouraging
   - Auto-detect expression from message content
   - Emotion-aware responses to user moods

6. **GeminiAIService**
   - `generateStudyNotes()` - AI notes with formatting
   - `getCounsellingResponse()` - Empathetic AI responses
   - `getExamPreparationTips()` - Study guidance
   - `enhanceContent()` - Summarize/expand/simplify
   - `explainQuestion()` - Q&A analysis

7. **CameraMonitoringService**
   - Camera initialization with front-facing detection
   - `startMonitoring()` - Begin preview
   - `stopMonitoring()` - End session
   - TextureView integration ready for face detection

8. **ExamTimerService**
   - `startTimer()` - Countdown with callbacks
   - Auto-warnings at 5 minutes, 1 minute
   - `getRemainingTimePercentage()` - Progress tracking
   - `isTimeRunningOut()` - < 10% alert

#### Layouts (8 Files)
1. **activity_one_on_one_chat.xml** - DM interface (blue header, yellow input)
2. **activity_ai_notes_generator.xml** - Subject/topic/content inputs with AI output
3. **activity_exam_conductor.xml** - Camera preview, timer, monitoring status
4. **activity_community_groups.xml** - Tabbed group listing
5. **activity_ai_counsellor_premium.xml** - Avatar (120x120), mood selector, voice toggle
6. **activity_create_group.xml** - Group creation form (teacher/senior restricted)
7. **activity_group_chat.xml** - Group messaging interface
8. **item_chat_message.xml** - Message bubble template
9. **item_community_group.xml** - Group card template

#### Adapters (2 Files)
1. **ChatMessageAdapter** - Message RecyclerView with timestamp/read status
2. **CommunityGroupAdapter** - Group listing with member count, join button

---

## Next Steps - Activity Implementation

### **Priority 1: OneOnOneChatActivity.java** (40-60 lines)
```java
public class OneOnOneChatActivity extends AppCompatActivity {
    // Initialize
    - Get otherUserId from intent extras
    - Generate consistent chatRoomId using ChatService.getChatRoomId()
    - Initialize ChatService listener
    - Setup RecyclerView with ChatMessageAdapter
    - Load chat history with ChatService.loadOneOnOneChat()
    
    // User Actions
    - Send button -> ChatService.saveOneOnOneMessage()
    - Display messages with sender avatars
    - Mark messages as read when displayed
    - Show typing indicator (optional)
    
    // Lifecycle
    - onResume() -> Load latest messages
    - onDestroy() -> Clean up listeners
}
```

### **Priority 2: AINoteGeneratorActivity.java** (50-70 lines)
```java
public class AINoteGeneratorActivity extends AppCompatActivity {
    // Initialize
    - Initialize GeminiAIService with API key
    - Setup EditText fields (subject, topic, content)
    - Setup RecyclerView for generated notes
    
    // Generate Button Action
    - Capture subject, topic, content from EditTexts
    - Show loading progress
    - Call GeminiAIService.generateStudyNotes()
    - Display generated notes in scrollable output
    
    // Save Button Action
    - Create StudyNote with generatedBy = "ai"
    - Call NotesService.saveNote()
    - Show success Toast
    
    // Lifecycle
    - onDestroy() -> GeminiAIService.shutdown()
}
```

### **Priority 3: ExamConductorActivity.java** (80-120 lines)
```java
public class ExamConductorActivity extends AppCompatActivity {
    // Initialize
    - Get examInfo from intent
    - Request camera permission
    - Initialize CameraMonitoringService with TextureView
    - Initialize ExamTimerService
    - Initialize ExamService
    
    // Exam Start
    - ExamService.startExamSession()
    - CameraMonitoringService.startMonitoring()
    - ExamTimerService.startTimer()
    - Show exam questions (from intent or Firebase)
    
    // Monitoring Loop (every 100ms)
    - Detect suspicious activity via camera frames
    - Call ExamService.recordSuspiciousActivity() if detected
    - Update warning panel visibility
    - ExamTimerService will auto-callback warnings
    
    // Exam End
    - Button click -> ExamService.endExamSession()
    - Stop camera: CameraMonitoringService.stopMonitoring()
    - Stop timer: ExamTimerService.stopTimer()
    - Save result with score
    - Show monitoring report
    
    // Lifecycle
    - onDestroy() -> Cleanup camera and timer
}
```

### **Priority 4: CommunityGroupActivity.java** (60-80 lines)
```java
public class CommunityGroupActivity extends AppCompatActivity {
    // Initialize
    - Initialize ChatService
    - Setup RecyclerView with CommunityGroupAdapter
    - Load groups with ChatService.getCommunityGroups()
    - Setup tab filtering buttons
    
    // Load Groups
    - ChatService.getCommunityGroups() -> populate adapter
    - Filter by category based on tab selection
    
    // Join Group Action
    - Add current user to group.members[]
    - Update group in Firestore
    - Start GroupChatActivity
    
    // Create Group Button (visible only if user.canCreateGroups())
    - Check user role (teacher/senior)
    - Launch activity_create_group.xml
    - Form validation:
      * groupName not empty
      * creatorRole = "teacher" or "senior"
      * category selected
    - Call ChatService.createCommunityGroup() with role validation
    - Add new group to top of RecyclerView
    
    // Lifecycle
    - Filter messages enforce SFW content (already in ChatService)
}
```

### **Bonus: GroupChatActivity.java** (70-90 lines)
```java
public class GroupChatActivity extends AppCompatActivity {
    // Similar to OneOnOneChat but:
    - Load groupId from intent
    - Use ChatService.loadGroupMessages(groupId)
    - Use ChatService.saveGroupMessage(groupId, senderId, message)
    - Show group name and member count in header
    - Display group category emoji
}
```

### **Bonus: AICounsellorActivity.java** (Enhance existing - 60-80 lines)
```java
public class AICounsellorActivity extends AppCompatActivity {
    // Add to existing AI Counsellor activity:
    
    // Initialize
    - Initialize TextToSpeechService
    - Initialize AIAvatarService
    - Initialize GeminiAIService
    
    // OnCreate
    - TextToSpeechService.initialize()
    - Setup mood buttons with click listeners
    - Setup voice toggle button
    
    // Send Message Action
    - Get user input and selected mood
    - Call GeminiAIService.getCounsellingResponse(input, mood)
    - Get suggested expression: AIAvatarService.getExpressionForResponse()
    - Update avatar: AIAvatarService.setAvatarState(expression)
    - If voice enabled: TextToSpeechService.speak(response)
    
    // Voice Button Action
    - Toggle: TextToSpeechService.isSpeaking()
    - Stop if speaking: TextToSpeechService.stop()
    
    // Mood Selection
    - Update mood display
    - Suggest expression: AIAvatarService.getExpressionForMood(mood)
    
    // Lifecycle
    - onDestroy() -> TextToSpeechService.shutdown()
    - onDestroy() -> GeminiAIService.shutdown()
}
```

---

## Firebase Security Rules Update

```firestore
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Chat Rooms - only participants can read/write
    match /chatRooms/{roomId} {
      allow read, write: if request.auth.uid in resource.data.participants;
      match /messages/{messageId} {
        allow read, write: if request.auth.uid in get(/databases/$(database)/documents/chatRooms/$(roomId)).data.participants;
      }
    }
    
    // Community Groups - members can read, creator can manage
    match /communityGroups/{groupId} {
      allow read: if request.auth.uid in resource.data.members;
      allow write: if resource.data.createdBy == request.auth.uid;
      allow create: if request.resource.data.creatorRole in ["teacher", "senior"];
      
      match /messages/{messageId} {
        allow read: if request.auth.uid in get(/databases/$(database)/documents/communityGroups/$(groupId)).data.members;
        allow create: if request.auth.uid in get(/databases/$(database)/documents/communityGroups/$(groupId)).data.members;
      }
    }
    
    // Exams - teachers can create, students can take
    match /exams/{examId} {
      allow read, write: if resource.data.createdBy == request.auth.uid;
      allow read: if request.auth.uid in resource.data.studentIds;
      
      match /monitoring/{studentId} {
        allow write: if studentId == request.auth.uid;
        allow read: if studentId == request.auth.uid || resource.data.createdBy == request.auth.uid;
      }
      
      match /results/{studentId} {
        allow read, write: if studentId == request.auth.uid;
      }
    }
    
    // Notes - only owner can access
    match /notes/{noteId} {
      allow read, write: if resource.data.userId == request.auth.uid;
    }
  }
}
```

---

## AndroidManifest.xml Updates

Add these activity declarations:
```xml
<!-- Activities -->
<activity android:name=".activities.OneOnOneChatActivity" />
<activity android:name=".activities.GroupChatActivity" />
<activity android:name=".activities.AINoteGeneratorActivity" />
<activity android:name=".activities.ExamConductorActivity" />
<activity android:name=".activities.CommunityGroupActivity" />
<activity android:name=".activities.CreateGroupActivity" />

<!-- Permissions -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.RECORD_AUDIO" /> <!-- For voice -->
```

---

## Gradle Dependencies to Add

```gradle
// Gemini AI
implementation 'com.google.ai.client.generativeai:google-generativeai-android:0.2.0'

// ML Kit Face Detection
implementation 'com.google.mlkit:face-detection:16.1.5'

// Firebase (already should be there)
implementation 'com.google.firebase:firebase-firestore'
implementation 'com.google.firebase:firebase-auth'

// RecyclerView
implementation 'androidx.recyclerview:recyclerview:1.2.1'
```

---

## Testing Checklist

### ChatService Tests
- [ ] One-on-one message sends and loads correctly
- [ ] Group creation blocked for students
- [ ] Group creation succeeds for teachers/seniors
- [ ] SFW filter blocks inappropriate content
- [ ] Messages display with correct sender info

### ExamService Tests
- [ ] Exam creation with camera mandatory flag
- [ ] Monitoring incident recording with severity levels
- [ ] Timer auto-triggers warnings
- [ ] Multiple faces detection logs as "high" severity

### NotesService Tests
- [ ] AI-generated notes saved with correct timestamp
- [ ] Manual notes saved separately
- [ ] Favorites toggle works
- [ ] User can retrieve only their notes

### UI Tests
- [ ] Community groups visible only to members
- [ ] Avatar changes based on AI mood
- [ ] Voice button speaks generated text
- [ ] Timer counts down correctly

---

## Code Quality Standards

✅ All implemented:
- Listener interfaces for async operations
- Proper error handling with callbacks
- Firebase Firestore integration patterns
- SFW content filtering in ChatService
- Role-based validation (teacher/senior only)

---

## Implementation Order

1. **Week 1**: Core Activities (OneOnOneChat, AINoteGenerator)
2. **Week 2**: Exam System (ExamConductorActivity + ML Kit face detection)
3. **Week 3**: Community Groups + Enhanced AI Counsellor
4. **Week 4**: Firebase Rules + Testing + Optimization

---

**Total Implementation Estimate**: 4-6 weeks of development
**Code Size**: ~3000-4000 additional lines (activities, integration)
**Dependencies**: 3 major (Gemini, ML Kit, Firebase)

Last Updated: Phase 2 - Foundation Complete
Next Phase: Activity Implementation & Integration
