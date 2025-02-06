package com.example.onion

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class HomeScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        // Set up click listeners for the images
        val downyMildewImage = findViewById<ImageView>(R.id.imgDownyMildew)
        val powderyMildewImage = findViewById<ImageView>(R.id.imgPowderyMildew)
        val purpleBlotchImage = findViewById<ImageView>(R.id.imgPurpleBlotch)
        val leafBlightImage = findViewById<ImageView>(R.id.imgLeafBlight)
        val newsImage = findViewById<ImageView>(R.id.imgNews)
        val homeIcon = findViewById<ImageView>(R.id.home_icon)
        val scannerIcon = findViewById<ImageView>(R.id.camera_icon)
        val historyIcon = findViewById<ImageView>(R.id.history_icon)

        downyMildewImage.setOnClickListener { openDownyMildewPage() }
        powderyMildewImage.setOnClickListener { openPowderyMildewPage() }
        purpleBlotchImage.setOnClickListener { openPurpleBlotchPage() }
        leafBlightImage.setOnClickListener { openLeafBlightPage() }
        newsImage.setOnClickListener { openNewsPage() }
        homeIcon.setOnClickListener { openHomePage() }
        scannerIcon.setOnClickListener { openScannerPage() }
        historyIcon.setOnClickListener { openHistoryPage() }
    }

    private fun openHomePage() {
        val intent = Intent(this, HomeScreenActivity::class.java)
        startActivity(intent)
    }

    private fun openScannerPage() {
        val intent = Intent(this, ScannerActivity::class.java)
        startActivity(intent)
    }

    private fun openHistoryPage() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun openDownyMildewPage() {
        val intent = Intent(this, DownyMildewActivity::class.java)
        startActivity(intent)
    }

    private fun openPowderyMildewPage() {
        val intent = Intent(this, PowderyMildewActivity::class.java)
        startActivity(intent)
    }

    private fun openPurpleBlotchPage() {
        val intent = Intent(this, PurpleBlotchActivity::class.java)
        startActivity(intent)
    }

    private fun openLeafBlightPage() {
        val intent = Intent(this, LeafBlightActivity::class.java)
        startActivity(intent)
    }

    private fun openNewsPage() {
        val intent = Intent(this, NewsActivity::class.java)
        startActivity(intent)
    }
}
// comment test