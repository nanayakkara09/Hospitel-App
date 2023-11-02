package com.example.labexam03

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton: Button = findViewById(R.id.loginButton)
        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username == "admin" && password == "admin1234") {
                // Correct username and password
                showSuccessMessage()
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            } else {

                showErrorMessage()
            }
        }
    }

    private fun showSuccessMessage() {
        val successMessage = "Login Successful"
        Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMessage() {
        val errorMessage = "Incorrect username or password"
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}
