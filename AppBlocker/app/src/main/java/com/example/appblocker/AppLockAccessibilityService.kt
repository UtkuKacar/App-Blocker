package com.example.appblocker

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class AppLockAccessibilityService : AccessibilityService() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onServiceConnected() {
        super.onServiceConnected()
        sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()

            if (packageName.isNullOrEmpty()) return

            val blockedApps = sharedPreferences.getStringSet("blocked_apps", setOf()) ?: setOf()

            if (blockedApps.contains(packageName)) {
                Log.d("AppLockService", "Engellenen uygulama açılmaya çalışıldı: $packageName")

                val lockIntent = Intent(this, LockScreenActivity::class.java)
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(lockIntent)
            }
        }
    }

    override fun onInterrupt() {}
}