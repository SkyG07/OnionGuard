package com.example.onion

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ScannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scanner2)

        // Find the button and text view by ID
        val scanButton: Button = findViewById(R.id.scan_button)  // Make sure this matches your button ID in XML
        val statusTextView: TextView = findViewById(R.id.status_text)  // Make sure this matches your TextView ID in XML

        // Set up a click listener for the scan button
        scanButton.setOnClickListener {
            // Start scanning logic here
            statusTextView.text = "Scanning..." // Show status while scanning
            // You can add your scanning logic here
        }

        // Handle window insets for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
