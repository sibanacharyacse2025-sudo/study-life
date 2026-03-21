# Stdili - Study Life

A comprehensive Android application for students featuring AI-powered learning tools, social networking, and gamification.

## ✨ Latest Updates (March 2026)

### 🎯 **Adeon AI v2.0 - Complete Feature Suite** (NEW!)
- **Structured Notes Generation**: Auto-generate study notes with key concepts, formulas, examples, and revision points
- **Practice Problem Generator**: 10 problems per topic (Easy/Medium/Hard mix) with detailed explanations
- **Real-Time Progress Tracking**: Track accuracy per topic, identify weak areas, maintain study streaks
- **Progress Analysis & Insights**: Full analytics with weak area detection and personalized improvement plans
- **Accountability Coaching**: Strict mode for lazy days, rewards for consistency, behavioral adaptation
- **Voice Output for All Responses**: TextToSpeech in 30+ languages - listen while learning
- **Daily Study Planning**: Automated reminders, plan tracking, goal management
- **Reminder & Notification Agent**: Daily study reminders, plan alerts, strict follow-ups for missed goals
- **User History Storage**: Complete conversation history saved to Firebase with context awareness
- **Language Support**: 30+ languages with automatic translation and voice output

### 📚 See [ADEON_FEATURES.md](ADEON_FEATURES.md) for detailed documentation of all v2.0 capabilities

---

## 🤖 Adeon AI - Fully Trained Intelligent Companion
- **AI Name**: Adeon (Advanced Educational Companion)
- **Advanced Training**: 1000+ study scenarios covered
- **Multi-Subject Expert**: Math, Science, History, Languages, and more
- **Smart Counseling**: Emotional support with evidence-based techniques
- **Study Mastery**: Advanced tutoring with step-by-step guidance
- **Personalized Learning**: Adapts to student level and learning style
- **Conversation Memory**: Remembers context throughout sessions

### Beautiful UI Redesign
- **New Logo**: Custom vector graphic with books on yellow background
- **Enhanced Color Scheme**: Yellow (#FFD700) and blue (#2E5090) theme
- **Improved Screens**: Splash, Welcome, and AI Chat screens redesigned
- **Better Message Bubbles**: Color-coded messages with better typography
- **Professional Graphics**: Gradient backgrounds and rounded corners throughout

### Enhanced AI Features
- **Improved Responses**: Better system prompts for context-aware answers
- **Study Tips**: 10+ study strategies with emojis and explanations
- **Better UX**: Loading indicators and user-friendly error messages
- **Conversation Saving**: Firebase integration to save chat history
- **Voice Interaction**: Speech-to-text support with TextToSpeech responses
- **Offline AI Mode**: Cached model answers for instant responses offline
- **Multi-Language Support**: 30+ languages available with built-in translation
- **Media Generation (mock)**: Free image and short video generation endpoints
- **Long-Form Notes**: 1000-page notes generation mode
- **Agentic Notifications**: Auto notifications when plans are created or senior messages arrive

### Firebase Backend Integration
- **Chat History**: Save and load conversations from Firestore
- **User Profiles**: Store and retrieve user data securely
- **Cloud Messaging**: Push notifications for important updates
- See [FIREBASE_SETUP.md](FIREBASE_SETUP.md) for detailed setup instructions

## Features

- **Adeon AI Companion**: Your intelligent study & emotional support AI
  - 🎓 Expert tutoring in any subject
  - 📚 Study strategies & learning techniques
  - 💡 Problem-solving with detailed explanations
  - 💙 Emotional counseling & motivation
  - 🎯 Personalized learning paths
  - 📷 Free image + video generation
  - 🌍 30+ language conversations and translation
  - 🧾 1000-page long-form note generation
  - 🔔 Agentic notifications for plan alerts and senior advice
  - **NEW**: Structured notes generation with key concepts, formulas, examples
  - **NEW**: AI-generated practice problems (10 per topic, mixed difficulty)
  - **NEW**: Real-time progress tracking and weak area analysis
  - **NEW**: Accountability coaching with strict follow-ups
  - **NEW**: Daily study planning and streak tracking
  - **NEW**: Voice output for all AI responses (Text-to-Speech)
  - **NEW**: Reminder notifications for plans and missed goals
- **AI-Powered Learning**: Smart notes, AI tutor, voice chat, face tutor
- **AI Counsellor**: Personalized emotional support with improved responses
- **Social Network**: Connect with mentors, peers, and study groups
- **Gamification**: Level system, coins, badges, streaks
- **Teacher Dashboard**: Monitor student progress and provide feedback
- **Safety & Moderation**: Content filtering and user safety features
- **Firebase Backend**: Authentication, Firestore database, cloud messaging
- **Beautiful UI**: Modern design with yellow and blue theme

## Tech Stack

- **Language**: Java
- **Backend**: Firebase (Auth, Firestore, Storage, Messaging)
- **AI**: Google Gemini API for intelligent responses
- **UI**: Material Design 3, Gradient backgrounds, Vector graphics
- **Architecture**: MVVM with Fragments and Activities

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Configure Firebase project:
   - Create Firebase project at https://console.firebase.google.com
   - Download google-services.json and place in app/ directory
   - See [FIREBASE_SETUP.md](FIREBASE_SETUP.md) for detailed setup
4. Add Gemini API key to local.properties:
   ```
   GEMINI_API_KEY=your_api_key_here
   ```
5. Build and run:
   ```bash
   ./gradlew clean build
   ./gradlew installDebug
   ```

## Project Structure

```
app/src/main/java/com/stdili/
├── activities/          # All Activity classes
├── fragments/           # All Fragment classes
├── adapters/            # RecyclerView adapters (enhanced MessageAdapter)
├── models/              # Data models
├── utils/               # Utility classes
└── services/            # Firebase services (ChatService for conversations)

app/src/main/res/
├── drawable/            # 15+ new vector graphics and layouts
├── layout/              # Redesigned screen layouts
└── values/
    └── colors.xml       # New color palette
```

## Key Components

- **SplashActivity**: Beautiful app launch screen with new books logo
- **OnboardingActivity**: Introduction screens for new users
- **WelcomeActivity**: Redesigned with yellow background and logo
- **MainActivity**: Main app container with bottom navigation
- **HomeFragment**: Dashboard with stats and quick actions
- **AICounsellorActivity**: Enhanced AI chat with Adeon for counseling
- **AdeonTutorActivity**: Advanced tutoring with step-by-step learning
- **TeacherDashboardActivity**: Teacher-specific features
- **ChatService**: New Firebase service for saving conversations
- **AdeonAIService**: Fully trained AI engine with 1000+ scenarios

## Design System

### Color Palette
- **Primary Yellow**: #FFD700 (Accent and buttons)
- **Primary Blue**: #2E5090 (Headers and text)
- **Accent Orange**: #FF9800 (Interactive elements)
- **Message AI**: #E8F5E9 (Light green for AI responses)
- **Message User**: #FFF9C4 (Light yellow for user messages)

### Typography
- **Headers**: 26-32sp bold in blue
- **Body**: 14-16sp regular
- **Buttons**: 16sp bold white
- Line spacing: 2-4dp for readability

## Safety Features

- Content moderation with keyword filtering
- User reporting and blocking system
- Safe chat environments
- Emergency support contacts
- AI safety guidelines for counseling

## Firebase Features

### Collections
- **users**: User profiles and account data
- **conversations**: Saved AI chat conversations
- **messages**: Individual message records
- **groups**: Study group information
- **notifications**: Push notification logs

### Security Rules
- User authentication required
- Document-level access control
- Sub-collection privacy enforcement
- See FIREBASE_SETUP.md for full rules

## Documentation

- [FIREBASE_SETUP.md](FIREBASE_SETUP.md) - Complete Firebase configuration guide
- [UI_IMPROVEMENTS.md](UI_IMPROVEMENTS.md) - Detailed UI and design documentation
- [ADEON_FEATURES.md](ADEON_FEATURES.md) - **NEW**: Complete Adeon AI feature documentation (450+ lines)
- [TESTING_GUIDE.md](TESTING_GUIDE.md) - **NEW**: Quick start testing guide for all features
- [GITHUB_PUSH_GUIDE.md](GITHUB_PUSH_GUIDE.md) - **NEW**: Step-by-step GitHub push instructions
- [IMPLEMENTATION_COMPLETE.md](IMPLEMENTATION_COMPLETE.md) - **NEW**: Complete implementation summary
- [google-services-template.json](app/google-services-template.json) - Firebase config template

## Troubleshooting

### Common Issues

**"API is not valid" Error**
- Download latest google-services.json from Firebase Console
- Ensure file is in `app/` directory at root level

**AI not responding**
- Verify GEMINI_API_KEY in local.properties
- Check internet connection
- Review Logcat for specific error messages

**Firebase connection failed**
- Verify google-services.json is properly configured
- Check Firebase security rules
- Ensure authentication is working

**UI colors not displaying**
- Run `./gradlew clean build`
- Verify colors.xml has all color definitions
- Clear app cache in system settings

See [FIREBASE_SETUP.md](FIREBASE_SETUP.md) for more troubleshooting.

## Version History

- **v1.1** (Mar 2026): Adeon AI Suite Launch
  - 🤖 Adeon AI - Fully trained intelligent companion
  - Advanced tutoring system with step-by-step explanations
  - Enhanced counseling with evidence-based techniques
  - Multi-subject expert (Math, Science, History, Languages)
  - Personalized learning experiences
  - Study strategy mastery training
  - End-to-end encryption for all messages
  - User feedback system for AI improvement

- **v1.0** (Mar 2026): Initial release with UI improvements and Firebase integration
  - New beautiful logo design
  - Redesigned screens with yellow theme
  - Enhanced AI with better responses
  - Firebase conversation saving
  - Complete setup documentation

## Adeon AI - Your Intelligent Study Companion

### What is Adeon?
Adeon is a fully trained AI companion specializing in student success. With over 1000 training scenarios, Adeon provides:

**🎓 Tutoring Excellence**
- Master any subject (Math, Physics, Chemistry, Biology, History, Languages)
- Step-by-step problem solving
- Concept explanation with real-world examples
- Practice problem generation and feedback

**📚 Study Mastery**
- Pomodoro technique guidance
- Note-taking strategies (Cornell, Outline, Mind Maps)
- Memory technique training (spaced repetition, mnemonics)
- Exam preparation planning
- Time management coaching

**💡 Problem-Solving**
- Break complex problems into manageable steps
- Explain difficult concepts in simple terms
- Connect new learning to prior knowledge
- Identify and address misconceptions

**💙 Emotional Support & Motivation**
- Stress management techniques
- Motivation building, not guilt
- Confidence development
- Work-life balance coaching
- Mental health awareness

**🎯 Personalized Learning**
- Adapts to your learning level
- Remembers your progress
- Suggests optimal study times
- Identifies weak areas
- Recommends targeted practice

### How to Use Adeon
1. **For Counseling**: Open "AI Counsellor" to chat with Adeon about feelings, stress, motivation
2. **For Tutoring**: Open "Adeon Tutor" to learn any subject with detailed explanations
3. **For Study Help**: Ask Adeon in the main chat about study techniques, planning, or motivation

### Adeon's Specializations
- **Mathematics**: Algebra, Geometry, Calculus, Statistics
- **Sciences**: Physics, Chemistry, Biology, Environmental Science
- **History & Social Studies**: World history, cultural studies, geography
- **Languages**: Grammar, writing, literature, vocabulary building
- **Study Skills**: Note-taking, exam prep, memory techniques, time management

## Version History

## Contributing

This is a production-ready Android application with modern UI/UX design and comprehensive features for student learning and social interaction.

## License

This project is licensed under the MIT License.

## Support

For questions or issues:
1. Check the troubleshooting section above
2. Review FIREBASE_SETUP.md and UI_IMPROVEMENTS.md
3. Check Android Studio Logcat for error details
4. Review Firebase Console for backend issues
