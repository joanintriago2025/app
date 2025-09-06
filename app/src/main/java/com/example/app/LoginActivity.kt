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

class LoginActivity :
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
            R.layout.activity_login
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
        val loginButton = findViewById<MaterialButton>(R.id.loginButton)
        val goToRegisterText = findViewById<TextView>(R.id.goToRegisterText)
        val emailInputLayout = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.emailInputLayout)
        val passwordInputLayout = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.passwordInputLayout)

        loginButton.setOnClickListener {
            val email = emailEditText.text?.toString() ?: ""
            val password = passwordEditText.text?.toString() ?: ""
            emailInputLayout.error = null
            passwordInputLayout.error = null
            mainViewModel.login(email, password)
        }

        goToRegisterText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        mainViewModel.loginResult.observe(this, Observer { (success, error) ->
            if (success) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else if (error != null) {
                // Show input validation errors on TextInputLayout, Firebase errors in Toast
                when (error) {
                    "Email and password must not be empty." -> {
                        if (emailEditText.text.isNullOrBlank()) emailInputLayout.error = "Email required"
                        if (passwordEditText.text.isNullOrBlank()) passwordInputLayout.error = "Password required"
                    }
                    "Please enter a valid email address." -> {
                        emailInputLayout.error = error
                    }
                    else -> {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}