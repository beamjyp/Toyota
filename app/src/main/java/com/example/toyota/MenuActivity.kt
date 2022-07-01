package com.example.toyota

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val walkie_click = findViewById<ImageView>(R.id.walkie_click)
        // set on-click listener

        walkie_click.setOnClickListener {
            val intent = Intent(this, MicToSpeakerActivity::class.java)
            startActivity(intent)
        }
    }
}