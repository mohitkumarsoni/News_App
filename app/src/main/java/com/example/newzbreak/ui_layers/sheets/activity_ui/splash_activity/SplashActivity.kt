package com.example.newzbreak.ui_layers.sheets.activity_ui.splash_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.newzbreak.R
import com.example.newzbreak.ui_layers.sheets.activity_ui.news_activity.NewsActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            startActivity(Intent(this@SplashActivity, NewsActivity::class.java))
        },1000)


    }
}