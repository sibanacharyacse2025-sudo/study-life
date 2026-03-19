# Firebase Configuration Guide for StudyLife App

## Overview
This app uses Firebase for:
- **Authentication**: Email/Password, OTP-based login
- **Firestore Database**: User profiles, study data, chat messages
- **Cloud Storage**: Profile pictures, study materials
- **Cloud Messaging**: Push notifications

## Setup Instructions

### Step 1: Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a new project"
3. Name it "stdili-app" (or any name you prefer)
4. Enable Google Analytics if desired
5. Create the project

### Step 2: Add Android App to Firebase Project
1. In Firebase Console, click "Add app" button
2. Select "Android"
3. Enter the following details:
   - **Package name**: `com.stdili`
   - **App nickname**: `StudyLife`
   - **SHA-1 Certificate**: [Get from your machine - see instructions below]

### Step 3: Get SHA-1 Certificate Fingerprint

#### On Windows:
```bash
cd /path/to/your/project
.\gradlew signingReport
```

Look for the output with "SHA1" - this is your SHA-1 fingerprint.

#### Alternative method:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### Step 4: Download google-services.json
1. After registering your app, click "Download google-services.json"
2. Save it to: `app/google-services.json` (Replace existing file)

### Step 5: Enable Firebase Services

In Firebase Console, go to each service and enable them:

#### Authentication:
1. Go to "Build" → "Authentication"
2. Click "Get started"
3. Enable "Email/Password" sign-in method
4. Enable "Phone" sign-in method (for OTP)

#### Firestore Database:
1. Go to "Build" → "Firestore Database"
2. Click "Create database"
3. Start in "Production mode" (you'll set security rules)
4. Choose region (preferably closest to your users)

#### Cloud Storage:
1. Go to "Build" → "Storage"
2. Click "Get started"
3. Choose the same region as your Firestore

#### Cloud Messaging:
1. Go to "Build" → "Cloud Messaging"
2. It should be enabled by default

### Step 6: Set Firestore Security Rules

Replace the default rules with these (in Firebase Console):

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Public read for users collection (metadata only)
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }

    // Messages collection for group chats
    match /groups/{groupId}/messages/{messageId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update, delete: if request.auth.uid == resource.data.senderId;
    }

    // Chat messages
    match /messages/{messageId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }

    // Study goals
    match /users/{userId}/goals/{goalId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Default deny
    match /{document=**} {
      allow read, write: if false;
    }
  }
}
```

### Step 7: Update Local Configuration

Make sure your `local.properties` file has:
```
sdk.dir=PATH_TO_ANDROID_SDK
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
```

### Step 8: Build and Run

```bash
./gradlew clean build
./gradlew installDebug
```

## Testing Firebase Connection

1. **Authentication Test**:
   - Launch app
   - Go to Sign Up
   - Create new account with email
   - Check Firebase Console under "Authentication" to verify user was created

2. **Firestore Test**:
   - After login, go to Profile
   - Update your profile
   - Check Firebase Console → "Firestore Database" → "users" collection

3. **Cloud Messaging Test**:
   - To test push notifications, generate a test token from Firebase Console and send a test message

## Troubleshooting

### "API Not Valid" Error
- **Cause**: google-services.json is missing or has invalid credentials
- **Solution**: Download latest google-services.json from Firebase Console

### Authentication Failed
- **Cause**: Firebase not initialized or incorrect configuration
- **Solution**: Ensure google-services.json is in `app/` directory and build again

### Firestore "Permission Denied"
- **Cause**: Security rules blocking access
- **Solution**: Check your security rules or temporarily use test mode during development

### Notifications Not Showing
- Make sure Cloud Messaging is enabled
- Check the FirebaseMessagingService implementation in the app
- Verify app has notification permissions (Android 13+)

## Important Notes

⚠️ **Security Warning**:
- Never commit google-services.json with real credentials to public repositories
- Use `.gitignore` to exclude it
- For CI/CD, use environment variables or secrets management

## API Keys

### Gemini API Key
Get from [Google AI Studio](https://makersuite.google.com/app/apikey):
1. Create a new API key
2. Enable the Generative AI API
3. Add to `local.properties`: `GEMINI_API_KEY=your_key`

## Support

For issues:
- Check [Firebase Documentation](https://firebase.google.com/docs)
- Review [Firebase Android Guide](https://firebase.google.com/docs/database/android/start)
