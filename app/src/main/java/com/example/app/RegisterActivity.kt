package com.example.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.app.viewmodel.MainViewModel

class RegisterActivity :
    AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )
        enableEdgeToEdge()
        setContentView(
            R.layout.activity_register
        )
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(
                R.id.main
            )
        ) { v, insets ->
            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<TextInputEditText>(R.id.confirmPasswordEditText)
        val registerButton = findViewById<MaterialButton>(R.id.registerButton)
        val goToLoginText = findViewById<TextView>(R.id.goToLoginText)
        val emailInputLayout = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.emailInputLayout)
        val passwordInputLayout = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.passwordInputLayout)
        val confirmPasswordInputLayout = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.confirmPasswordInputLayout)

        registerButton.setOnClickListener {
            val email = emailEditText.text?.toString() ?: ""
            val password = passwordEditText.text?.toString() ?: ""
            val confirmPassword = confirmPasswordEditText.text?.toString() ?: ""
            emailInputLayout.error = null
            passwordInputLayout.error = null
            confirmPasswordInputLayout.error = null
            mainViewModel.register(email, password, confirmPassword)
        }

        goToLoginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        mainViewModel.registerResult.observe(this, Observer { (success, error) ->
            if (success) {
                Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else if (error != null) {
                // Show input validation errors on TextInputLayout, Firebase errors in Toast
                when (error) {
                    "All fields are required." -> {
                        if (emailEditText.text.isNullOrBlank()) emailInputLayout.error = "Email required"
                        if (passwordEditText.text.isNullOrBlank()) passwordInputLayout.error = "Password required"
                        if (confirmPasswordEditText.text.isNullOrBlank()) confirmPasswordInputLayout.error = "Confirm password required"
                    }
                    "Please enter a valid email address." -> {
                        emailInputLayout.error = error
                    }
                    "Password must be at least 6 characters." -> {
                        passwordInputLayout.error = error
                    }
                    "Passwords do not match." -> {
                        confirmPasswordInputLayout.error = error
                    }
                    else -> {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}