package com.example.appblocker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailField = findViewById<TextInputEditText>(R.id.email)
        val passwordField = findViewById<TextInputEditText>(R.id.password)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val registerText = findViewById<TextView>(R.id.registerText)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            if (isSessionExpired()) {
                forceLogout()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        btnLogin.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
            } else {
                val savedEmail = sharedPreferences.getString("email", null)
                val savedPassword = sharedPreferences.getString("password", null)

                if (email == savedEmail && password == savedPassword) {
                    Toast.makeText(this, "Giriş başarılı!", Toast.LENGTH_SHORT).show()

                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isLoggedIn", true)
                    editor.putLong("lastLoginTime", System.currentTimeMillis())
                    editor.apply()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Hatalı e-posta veya şifre!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isSessionExpired(): Boolean {
        val lastLoginTime = sharedPreferences.getLong("lastLoginTime", 0)
        val currentTime = System.currentTimeMillis()
        val thirtyDaysMillis = 30L * 24 * 60 * 60 * 1000

        return (currentTime - lastLoginTime) > thirtyDaysMillis
    }

    private fun forceLogout() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        Toast.makeText(this, "Oturum süreniz doldu, tekrar giriş yapmalısınız!", Toast.LENGTH_LONG).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
