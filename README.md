# Stdili - Study Life

A comprehensive Android application for students featuring AI-powered learning tools, social networking, and gamification.

## ✨ Latest Updates (March 2026)

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

### Firebase Backend Integration
- **Chat History**: Save and load conversations from Firestore
- **User Profiles**: Store and retrieve user data securely
- **Cloud Messaging**: Push notifications for important updates
- See [FIREBASE_SETUP.md](FIREBASE_SETUP.md) for detailed setup instructions

## Features

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
- **AICounsellorActivity**: Enhanced AI chat with better UI and responses
- **TeacherDashboardActivity**: Teacher-specific features
- **ChatService**: New Firebase service for saving conversations

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

- **v1.0** (Mar 2026): Initial release with UI improvements and Firebase integration
  - New beautiful logo design
  - Redesigned screens with yellow theme
  - Enhanced AI with better responses
  - Firebase conversation saving
  - Complete setup documentation

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
