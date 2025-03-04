package com.example.appblocker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class LockScreenActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        val btnHome = findViewById<Button>(R.id.btnHome)
        val tvMessage = findViewById<TextView>(R.id.tvMessage)

        btnHome.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        // Geri tuşuna basıldığında hiçbir şey yapma
    }
}
