package com.example.onion

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class HistoryActivity : AppCompatActivity() {
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var scanHistory: List<ScanHistory>  // A list to hold history data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history2)

        // Apply window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        // Sample data for ScanHistory
        scanHistory = listOf(
            ScanHistory("Powdery Mildew", "2025-01-26 10:00"),
            ScanHistory("Purple Blotch", "2025-01-25 14:30"),
            // Add more history items here
        )

        // Initialize adapter with scan history
        historyAdapter = HistoryAdapter(scanHistory)
        historyRecyclerView.adapter = historyAdapter
    }

    // ScanHistory data model
    data class ScanHistory(val disease: String, val timestamp: String)

    // HistoryAdapter to bind the data to RecyclerView
    class HistoryAdapter(private val historyList: List<ScanHistory>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
            return HistoryViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
            val currentItem = historyList[position]
            holder.diseaseName.text = currentItem.disease
            holder.timestamp.text = currentItem.timestamp
        }

        override fun getItemCount(): Int {
            return historyList.size
        }

        class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val diseaseName = itemView.findViewById<TextView>(R.id.diseaseName)
            val timestamp = itemView.findViewById<TextView>(R.id.timestamp)
        }
    }
}
