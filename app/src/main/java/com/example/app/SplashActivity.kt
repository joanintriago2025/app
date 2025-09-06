package com.example.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import com.google.firebase.auth.FirebaseAuth
import android.os.Handler
import android.os.Looper

class SplashActivity :
    AppCompatActivity() {
    private val splashDuration = 3000L // 3 seconds

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )
        enableEdgeToEdge()
        setContentView(
            R.layout.activity_splash
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
        // Play Lottie animation programmatically (optional)
        val animationView = findViewById<LottieAnimationView>(R.id.animationView)
        animationView.setAnimation(R.raw.welcome_animation)
        animationView.playAnimation()

        // Always sign out user on app launch
        FirebaseAuth.getInstance().signOut()
        // Splash delay and direct auth check
        Handler(Looper.getMainLooper()).postDelayed({
            val isAuthenticated = FirebaseAuth.getInstance().currentUser != null
            if (isAuthenticated) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }, 3000L)
    }
}