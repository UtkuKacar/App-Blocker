package com.example.appblocker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerEmail = findViewById<EditText>(R.id.registerEmail)
        val registerPassword = findViewById<EditText>(R.id.registerPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val loginText = findViewById<TextView>(R.id.loginText)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        btnRegister.setOnClickListener {
            val email = registerEmail.text.toString().trim()
            val password = registerPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val savedEmail = sharedPreferences.getString("email", null)

            if (savedEmail == email) {
                Toast.makeText(this, "Bu e-posta adresi zaten kayıtlı! Lütfen giriş yapın.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val editor = sharedPreferences.edit()
            editor.putString("email", email)
            editor.putString("password", password)
            editor.apply()

            Toast.makeText(this, "Kayıt başarılı! Giriş yapabilirsiniz.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
