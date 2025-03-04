package com.example.appblocker

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.app.NotificationManager
import android.os.Build
import android.Manifest
import androidx.appcompat.widget.Toolbar
import android.app.AppOpsManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    private lateinit var appListView: ListView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var selectedApps: MutableSet<String>
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var installedApps: List<Pair<String, String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        installedApps = getInstalledApps()

        sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        try {
            checkPermissions()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        checkExpiredBlocks()
        checkNotificationAccessPermission()
        checkDndPermission()

        val btnGrantPermissions = findViewById<Button>(R.id.btnRequestPermissions)

        btnGrantPermissions.setOnClickListener {
            requestMissingPermissions()
        }

        appListView = findViewById(R.id.appListView)
        val btnBlockApps = findViewById<Button>(R.id.btnBlockApps)
        val searchBar = findViewById<EditText>(R.id.search_bar)

        selectedApps = sharedPreferences.getStringSet("blocked_apps", mutableSetOf()) ?: mutableSetOf()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "App Blocker"

        installedApps = getInstalledApps()
        updateAppList()

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
        })

        btnBlockApps.setOnClickListener {
            val selectedItems = mutableSetOf<String>()
            for (i in installedApps.indices) {
                if (appListView.isItemChecked(i)) {
                    selectedItems.add(installedApps[i].first)
                }
            }

            if (selectedItems.isNotEmpty()) {
                showTimePickerDialog(selectedItems)
            } else {
                Toast.makeText(this, "Lütfen en az bir uygulama seçin!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateAppList()
    }

    private fun checkNotificationAccessPermission() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
    }

    private fun checkDndPermission() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }
    }

    private fun updateAppList() {
        val installedAppNames = installedApps.map { it.second }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, installedAppNames)
        appListView.adapter = adapter

        for (i in installedApps.indices) {
            if (selectedApps.contains(installedApps[i].first)) {
                appListView.setItemChecked(i, true)
            }
        }
    }

    private fun filterList(query: String) {
        val filteredList = installedApps.filter { it.second.contains(query, ignoreCase = true) }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, filteredList.map { it.second })
        appListView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_account -> {
                val intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_logout -> {
                logoutAndGoToLogin()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logoutAndGoToLogin() {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showTimePickerDialog(selectedApps: Set<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Engelleme Süresi Seç")

        val input = NumberPicker(this)
        input.minValue = 1
        input.maxValue = 120
        builder.setView(input)

        builder.setPositiveButton("Tamam") { _, _ ->
            val duration = input.value * 60 * 1000L
            blockApps(selectedApps, duration)
        }

        builder.setNegativeButton("İptal") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun blockApps(selectedApps: Set<String>, duration: Long) {
        selectedApps.forEach { packageName ->
            saveBlockTime(packageName, duration)
            startBlockTimer(packageName, duration)
        }

        sharedPreferences.edit().putStringSet("blocked_apps", selectedApps).apply()
        updateAppList()

        val durationMinutes = duration / (60 * 1000)
        Toast.makeText(this, "Seçili uygulamalar $durationMinutes dakika engellendi!", Toast.LENGTH_SHORT).show()
    }

    private fun saveBlockTime(packageName: String, duration: Long) {
        sharedPreferences.edit().putLong("block_time_$packageName", System.currentTimeMillis() + duration).apply()
    }

    private fun startBlockTimer(packageName: String, duration: Long) {
        handler.postDelayed({
            removeBlockedApp(packageName)
        }, duration)
    }

    private fun removeBlockedApp(packageName: String) {
        val blockedApps = sharedPreferences.getStringSet("blocked_apps", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        if (blockedApps.contains(packageName)) {
            blockedApps.remove(packageName)
            sharedPreferences.edit().putStringSet("blocked_apps", blockedApps).apply()
            updateAppList()

            Toast.makeText(this, "$packageName artık serbest!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkExpiredBlocks() {
        val blockedApps = sharedPreferences.getStringSet("blocked_apps", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        val appsToRemove = mutableSetOf<String>()
        val currentTime = System.currentTimeMillis()

        blockedApps.forEach { packageName ->
            if (currentTime >= sharedPreferences.getLong("block_time_$packageName", 0L)) {
                appsToRemove.add(packageName)
            }
        }

        if (appsToRemove.isNotEmpty()) {
            blockedApps.removeAll(appsToRemove)
            sharedPreferences.edit().putStringSet("blocked_apps", blockedApps).apply()
            updateAppList()

            Toast.makeText(this, "Engelleme süresi doldu, uygulamalar açıldı!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getInstalledApps(): List<Pair<String, String>> {
        val pm: PackageManager = packageManager
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .map { it.packageName to pm.getApplicationLabel(it).toString() }
    }

    private fun checkPermissions() {
        if (!hasUsageAccessPermission()) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        if (!hasNotificationAccessPermission()) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        if (!isAccessibilityServiceEnabled()) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    private fun hasUsageAccessPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

        val mode = if (Build.VERSION.SDK_INT >= 29) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        } else {
            try {
                val checkMethod = AppOpsManager::class.java.getMethod(
                    "checkOpNoThrow",
                    String::class.java,
                    Int::class.javaPrimitiveType,
                    String::class.java
                )
                checkMethod.invoke(
                    appOps,
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    packageName
                ) as Int
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun hasNotificationAccessPermission(): Boolean {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains("${packageName}/com.example.appblocker.AppLockAccessibilityService") == true
    }

    private fun requestMissingPermissions() {
        var redirected = false

        if (!hasUsageAccessPermission()) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            redirected = true
        }

        if (!hasNotificationAccessPermission()) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            redirected = true
        }

        if (!isAccessibilityServiceEnabled()) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            redirected = true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            redirected = true
        }

        if (!redirected) {
            Toast.makeText(this, "Tüm izinler zaten verilmiş.", Toast.LENGTH_SHORT).show()
        }
    }
}
