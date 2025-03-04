![igor-freitas-mesa](https://github.com/user-attachments/assets/461f46ba-f0b8-4d61-b376-ff958d85cf69)


📱 App Blocker - Focus Enhancement Tool

App Blocker is a productivity application designed to help users minimize distractions by temporarily blocking selected apps on their devices. Whether you're studying, working, or simply want a break from social media and games, this app allows you to block specific applications for a set duration, helping you stay focused and productive.


✨ Features

✅ App Blocking: Select and temporarily block distracting apps (e.g., games, social media, etc.).

⏳ Time-Based Restrictions: Set a custom duration for how long the selected apps should remain blocked.

🔔 Notification Control: Prevent notifications from blocked apps to avoid distractions.

🔒 Lock Screen Protection: When a blocked app is opened, the user is redirected to a lock screen.

🚀 Persistent Blocking: Even after restarting the device, blocked apps remain inaccessible until the timer expires.

🛠 Permission Handling: Guides users to grant necessary permissions for optimal functionality.

👤 User Authentication: Users must log in to manage their blocked apps, ensuring personalized settings.

📊 Automatic Session Handling: Tracks login sessions and auto-logs out users after a set duration.


🛠 Technologies Used

Kotlin - Main programming language

Android SDK - Core framework for Android app development

Android Jetpack (ViewModel, LiveData, SharedPreferences) - State management and data persistence

Accessibility Services - Detects and blocks app launches

SharedPreferences - Stores user preferences and blocked apps list

Material Design Components - UI enhancements for better user experience

Notification Manager - Controls app notifications

Android Permissions API - Manages required system permissions

Firebase (optional) - Can be used for authentication and data storage in future updates


📦 Installation

Clone the repository:

git clone https://github.com/yourusername/AppBlocker.git

Open the project in Android Studio.

Sync Gradle dependencies.

Run the application on an emulator or a physical device.


📝 Permissions Required

To ensure full functionality, the following permissions are required:

Usage Access Permission: Tracks app usage to determine which app is currently active.

Notification Access Permission: Blocks notifications from restricted apps.

Accessibility Service Permission: Detects when a blocked app is launched and redirects the user.

Storage & Shared Preferences: Saves user preferences and blocked apps list.

Users are prompted to grant these permissions upon the first app launch.


🚀 How It Works

Select Apps to Block: Choose which apps you want to block from the provided list.

Set a Timer: Define the duration for which the apps should remain blocked.

Stay Focused: If you try to open a blocked app, you will be redirected to a lock screen.

Unlock Automatically: The app unlocks itself after the set time expires.

Re-login & Manage Apps: Users can log in and manage their blocked apps anytime.


🛠 Troubleshooting

If the app does not block applications as expected, ensure that Usage Access, Notification Access, and Accessibility Permissions are granted.

If notifications are still appearing, verify that Notification Access is enabled in settings.

If the app crashes on launch, clear the cache and restart the device.


📜 License

This project is licensed under the MIT License - feel free to use, modify, and distribute it as needed.
