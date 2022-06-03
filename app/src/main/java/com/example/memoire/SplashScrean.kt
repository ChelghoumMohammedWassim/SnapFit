package com.example.memoire

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class SplashScrean : AppCompatActivity() {
    var checked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screan)
        //injecter l'annimation
        val splash: LottieAnimationView = findViewById(R.id.splach)
        //slectioner la vitesse
        splash.speed = (-1).toFloat()
        //starter l annimation
        splash.playAnimation()
        Handler().postDelayed({
            //lancer le main
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}