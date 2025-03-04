package com.example.appblocker

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class AppNotificationListener : NotificationListenerService() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("BlockedApps", Context.MODE_PRIVATE)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val blockedApps = sharedPreferences.getStringSet("blocked_apps", setOf()) ?: setOf()

        Log.d("AppNotificationListener", "Bildirim alındı: $packageName")

        if (packageName in blockedApps) {
            cancelNotification(sbn.key)
            Log.d("AppNotificationListener", "Bildirim engellendi: $packageName")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.d("AppNotificationListener", "Bildirim kaldırıldı: ${sbn.packageName}")
    }

    private fun getBlockedApps(): Set<String> {
        return sharedPreferences.getStringSet("blocked_apps", setOf()) ?: setOf()
    }

    fun updateBlockedApps(newBlockedApps: Set<String>) {
        sharedPreferences.edit()
            .putStringSet("blocked_apps", newBlockedApps)
            .apply()
    }
}
