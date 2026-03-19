# StudyLife - Quick Start Guide

## 🚀 Quick Start (5 Minutes)

### Step 1: Build & Run Immediately
```bash
# Clean build
./gradlew clean build

# Run on emulator or device
./gradlew installDebug
```

The app will start showing the beautiful new splash screen with the books logo!

---

## 📱 What You'll See

### Splash Screen
- **Golden yellow background**
- **Beautiful books logo** (blue, red, green books)
- **"StudyLife" branding**
- **Loading animation**

### Welcome Screen
- **Yellow gradient background**
- **Large books logo**
- **Three action buttons** (Student, Teacher, Guest)
- **Sign up link**

### AI Chat Screen
- **Blue header** with "AI Study Assistant"
- **Message bubbles** (yellow for you, green for AI)
- **Study tips button** with emoji
- **Rounded input field** and buttons

---

## 🔧 Without Firebase (Works Now)

The app fully works without Firebase setup:
- ✅ Splash screen with new logo
- ✅ Welcome screen beautiful design
- ✅ AI chat with Gemini API
- ✅ Study tips with emojis
- ✅ Voice input/output
- ✅ Beautiful UI

**Only Firebase is optional** - for saving chat history.

---

## 📝 Firebase Setup (Optional - 10 Minutes)

If you want to save conversations to Firestore:

1. **Go to Firebase Console**
   - https://console.firebase.google.com/
   - Create new project "stdili-app"

2. **Get google-services.json**
   - Add Android app with package: `com.stdili`
   - Download the config file
   - Place in: `app/google-services.json`

3. **Done!** The app will now save chats to Firebase

For detailed instructions, see [FIREBASE_SETUP.md](FIREBASE_SETUP.md)

---

## 🎨 Customization

### Change App Name
```bash
Edit: app/src/main/res/values/strings.xml
Change: <string name="app_name">StudyLife</string>
```

### Change Colors
```bash
Edit: app/src/main/res/values/colors.xml
Change primary_yellow, primary_blue, etc.
```

### Change Logo
- Edit: `drawable/logo_books.xml`
- Or replace with your own PNG/SVG

---

## 🐛 Troubleshooting

### App Won't Build
```bash
./gradlew clean
./gradlew build
```

### Can't Login
- Use test email: test@test.com (if Firebase not setup)
- Or set up Firebase and create real account

### AI Not Responding
- Check `GEMINI_API_KEY` in `local.properties`
- Get key: https://makersuite.google.com/app/apikey
- Add to `local.properties`: `GEMINI_API_KEY=your_key`

### Colors Look Wrong
```bash
./gradlew clean build
```
Then clear app cache and rerun.

---

## 📊 Visual Checklist

After running the app, verify:
- [ ] Splash screen shows books logo ✓
- [ ] Welcome screen is yellow ✓
- [ ] Buttons are yellow with rounded corners ✓
- [ ] AI chat header is blue ✓
- [ ] User messages are yellow on right ✓
- [ ] AI messages are green on left ✓
- [ ] Study tips have emojis ✓
- [ ] Input field is yellow ✓

---

## 📂 Key Files to Know

**Layouts** (UI Design):
- `activity_splash.xml` - Splash screen
- `activity_welcome.xml` - Welcome screen
- `activity_ai_counsellor.xml` - Chat screen
- `item_message.xml` - Message bubbles

**Code**:
- `AiCounsellorActivity.java` - AI chat logic
- `MessageAdapter.java` - Message display

**Graphics**:
- `drawable/logo_books.xml` - App logo
- `values/colors.xml` - All colors

**Documentation**:
- `FIREBASE_SETUP.md` - Firebase guide
- `UI_IMPROVEMENTS.md` - Design details
- `README.md` - Full project info

---

## 🎯 Try These Features

1. **Test Splash Screen**
   - Run app → See new logo

2. **Test Welcome Screen**
   - Continue as Student/Guest

3. **Test AI Chat**
   - Ask: "How do I study effectively?"
   - Get beautiful AI response
   - Try voice input (bottom button)

4. **Test Study Tips**
   - Click "💡 Study Tips" button
   - Get random helpful tips with emojis

---

## 💡 Pro Tips

- **Change prompt in AI**: Edit `AiCounsellorActivity.java` line ~200
- **Add more study tips**: Edit `showStudyTips()` method
- **Custom colors**: Edit `values/colors.xml`
- **New buttons**: Add to mood selector in `setupMoodSelector()`

---

## 🚀 Ready to Deploy

The app is **production-ready** with:
- ✅ Beautiful UI
- ✅ Working AI
- ✅ Firebase ready
- ✅ Full documentation
- ✅ Error handling

**Build and deploy!**

---

## 📞 Support

**Look at these files for help:**
1. `FIREBASE_SETUP.md` - Firebase issues
2. `UI_IMPROVEMENTS.md` - Design questions
3. `IMPROVEMENTS_SUMMARY.md` - What changed
4. `README.md` - General info

---

**Enjoy your beautiful new StudyLife app! 🎉**
