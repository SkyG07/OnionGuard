package com.example.onion

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        val cameraIcon = findViewById<ImageView>(R.id.camera_icon)
        val historyIcon = findViewById<ImageView>(R.id.history_icon)
        val homeIcon = findViewById<ImageView>(R.id.home_icon)

        cameraIcon.setOnClickListener {
            // Open Scanner Activity
            startActivity(Intent(this, ScannerActivity::class.java))
        }

        historyIcon.setOnClickListener {
            // Open History Activity
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        homeIcon.setOnClickListener {
            // Open Home Screen Activity
            startActivity(Intent(this, HomeScreenActivity::class.java))
        }
    }

    fun openNewsPage(view: View) {
        val intent = Intent(this, NewsActivity::class.java)
        startActivity(intent)
    }

    fun openPowderyMildewPage(view: View) {
        val intent = Intent(this, PowderyMildewActivity::class.java)
        startActivity(intent)
    }

    fun openPurpleBlotchPage(view: View) {
        val intent = Intent(this, PurpleBlotchActivity::class.java)
        startActivity(intent)
    }

    fun openLeafBlightPage(view: View) {
        val intent = Intent(this, LeafBlightActivity::class.java)
        startActivity(intent)
    }

    fun openDownyMildewPage(view: View) {
        val intent = Intent(this, DownyMildewActivity::class.java)
        startActivity(intent)
    }
}