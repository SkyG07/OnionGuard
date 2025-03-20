package com.example.onion

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream

class MainActivity : AppCompatActivity() {

    private lateinit var tflite: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        val cameraIcon = findViewById<ImageView>(R.id.camera_icon)
        val historyIcon = findViewById<ImageView>(R.id.history_icon)
        val homeIcon = findViewById<ImageView>(R.id.home_icon)

        // Load the TFLite model
        try {
            tflite = Interpreter(loadModelFile("best_float32.tflite"))
        } catch (e: Exception) {
            Log.e("TFLite", "Error loading model", e)
        }

        // Open camera for image capture
        cameraIcon.setOnClickListener {
            captureImage.launch(null)
        }

        historyIcon.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        homeIcon.setOnClickListener {
            startActivity(Intent(this, HomeScreenActivity::class.java))
        }
    }

    // CameraX activity result
    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let { processImage(it) }
    }

    // Load the TFLite model from assets
    private fun loadModelFile(modelName: String): MappedByteBuffer {
        val fileDescriptor = assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    // Process image with TensorFlow Lite
    private fun processImage(bitmap: Bitmap) {
        val image = TensorImage.fromBitmap(bitmap)
        val output = Array(1) { FloatArray(4) } // Adjust based on your model's output
        tflite.run(image.buffer, output)

        // Log the results
        Log.d("TFLite", "Model output: ${output.contentDeepToString()}")
    }

    fun openNewsPage(view: View) {
        startActivity(Intent(this, NewsActivity::class.java))
    }

    fun openPowderyMildewPage(view: View) {
        startActivity(Intent(this, PowderyMildewActivity::class.java))
    }

    fun openPurpleBlotchPage(view: View) {
        startActivity(Intent(this, PurpleBlotchActivity::class.java))
    }

    fun openLeafBlightPage(view: View) {
        startActivity(Intent(this, LeafBlightActivity::class.java))
    }

    fun openDownyMildewPage(view: View) {
        startActivity(Intent(this, DownyMildewActivity::class.java))
    }
}
