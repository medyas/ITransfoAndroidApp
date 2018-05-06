package com.medyas.itransfoapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.graphics.drawable.GradientDrawable
import android.graphics.Color.parseColor
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeModule


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Iconify.with( FontAwesomeModule())
        setContentView(R.layout.activity_splash)

        val strokeWidth = 5 // 5px not dp
        val roundRadius = 15 // 15px not dp
        val strokeColor = Color.parseColor("#2E3135")
        val fillColor = Color.parseColor("#DFDFE0")

        val gd = GradientDrawable()
        gd.setColor(fillColor)
        gd.cornerRadius = roundRadius.toFloat()
        gd.setStroke(strokeWidth, strokeColor)

        val secondsDelayed:Long = 1
        Handler().postDelayed(Runnable {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }, secondsDelayed * 1000)
    }

    override fun onPause() {
        super.onPause()
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

}
