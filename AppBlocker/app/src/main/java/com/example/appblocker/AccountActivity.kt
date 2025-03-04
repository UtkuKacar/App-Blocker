package com.example.appblocker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AccountActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnShowUpdatePassword = findViewById<Button>(R.id.btnShowUpdatePassword)
        val passwordUpdateLayout = findViewById<LinearLayout>(R.id.passwordUpdateLayout)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmNewPassword = findViewById<EditText>(R.id.etConfirmNewPassword)
        val btnChangePassword = findViewById<Button>(R.id.btnChangePassword)

        etEmail.setText(sharedPreferences.getString("email", ""))
        etPassword.setText(sharedPreferences.getString("password", ""))

        btnShowUpdatePassword.setOnClickListener {
            passwordUpdateLayout.visibility = View.VISIBLE
        }

        btnChangePassword.setOnClickListener {
            val newPassword = etNewPassword.text.toString().trim()
            val confirmNewPassword = etConfirmNewPassword.text.toString().trim()

            if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmNewPassword) {
                Toast.makeText(this, "Yeni şifreler eşleşmiyor!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val editor = sharedPreferences.edit()
            editor.putString("password", newPassword)
            editor.apply()

            Toast.makeText(this, "Şifre başarıyla değiştirildi!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
