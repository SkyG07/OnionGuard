package com.example.onion

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ScannerActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var galleryIcon: ImageView
    private lateinit var predictButton: Button
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private val IMAGE_SIZE = 640  // YOLO expects 640x640 images
    private val MODEL_PATH = "best_float32.tflite"

    private var selectedImageUri: Uri? = null  // Store selected image URI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner2)

        imageView = findViewById(R.id.imageView)
        resultTextView = findViewById(R.id.resultText)
        galleryIcon = findViewById(R.id.galleryIcon)
        predictButton = findViewById(R.id.predictButton)

        try {
            interpreter = Interpreter(loadModelFile())
            Log.d("YOLO", "Model loaded successfully")
        } catch (e: IOException) {
            Log.e("YOLO", "Error loading model", e)
        }

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedImageUri = result.data!!.data
                showSelectedImage()
            }
        }

        galleryIcon.setOnClickListener { openGallery() }
        predictButton.setOnClickListener { runModelOnSelectedImage() }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun showSelectedImage() {
        if (selectedImageUri != null) {
            imageView.setImageURI(selectedImageUri)
            resultTextView.text = ""
        }
    }

    private fun runModelOnSelectedImage() {
        if (selectedImageUri == null) {
            Log.e("YOLO", "No image selected!")
            return
        }

        try {
            val inputStream = contentResolver.openInputStream(selectedImageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (bitmap != null) {
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)
                val detectedDiseases = detectLeafBlight(resizedBitmap)
                drawBoundingBoxes(resizedBitmap, detectedDiseases)
            } else {
                Log.e("YOLO", "Bitmap is null")
            }
        } catch (e: Exception) {
            Log.e("YOLO", "Error processing image", e)
        }
    }

    private fun detectLeafBlight(bitmap: Bitmap): List<DiseaseDetectionResult> {
        val inputBuffer = convertBitmapToByteBuffer(bitmap)
        val outputArray = Array(1) { Array(7) { FloatArray(8400) } }  // Adjust based on YOLO model's expected shape

        interpreter.run(inputBuffer, outputArray)  // Run inference

        val detectedDiseases = mutableListOf<DiseaseDetectionResult>()

        for (i in outputArray[0].indices) {
            val detection = outputArray[0][i]
            if (detection.size < 7) continue  // Prevent out-of-bounds access

            val x = detection[0] * IMAGE_SIZE
            val y = detection[1] * IMAGE_SIZE
            val width = detection[2] * IMAGE_SIZE
            val height = detection[3] * IMAGE_SIZE
            val confidence = detection[4]

            // Print the full detection array to check correct indices
            Log.d("YOLO", "Raw detection data: ${detection.joinToString()}")
            Log.d("YOLO", "Confidence: $confidence")
            // ✅ Class index should be the one after confidence
            val classIndex = detection[5].toInt().coerceIn(0, 3)  // Assuming 4 classes (0-3)
            val label = getLabelForIndex(classIndex)

            // 🔴 Log all detections before filtering
            Log.d("YOLO", "Detection $i: x=$x, y=$y, width=$width, height=$height, confidence=$confidence, classIndex=$classIndex, label=$label")

            // ✅ Only add if confidence is high enough
            if (confidence > 0.5) {
                detectedDiseases.add(DiseaseDetectionResult(label, RectF(x, y, x + width, y + height)))
            }
        }

        Log.d("YOLO", "Total detected diseases: ${detectedDiseases.size}")

        return detectedDiseases
    }

    // ✅ Add this function to map class index to disease labels
    private fun getLabelForIndex(index: Int): String {
        val labels = listOf("Healthy Onion", "Powdery Mildew", "Purple Blotch", "Leaf Blight", "Downy Mildew")
        return if (index in labels.indices) labels[index] else "Unknown"
    }

    private fun drawBoundingBoxes(bitmap: Bitmap, detectedDiseases: List<DiseaseDetectionResult>) {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 40f
            typeface = Typeface.DEFAULT_BOLD
        }

        if (detectedDiseases.isNotEmpty()) {
            detectedDiseases.forEach { disease ->
                canvas.drawRect(disease.boundingBox, paint)
                canvas.drawText(disease.name, disease.boundingBox.left, disease.boundingBox.top - 10, textPaint)
            }
            resultTextView.text = "Detected: ${detectedDiseases.joinToString { it.name }}"
        } else {
            resultTextView.text = "No Disease Detected"
        }

        imageView.setImageBitmap(mutableBitmap)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val inputBuffer = ByteBuffer.allocateDirect(1 * IMAGE_SIZE * IMAGE_SIZE * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)
        bitmap.getPixels(intValues, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE)

        for (pixelValue in intValues) {
            val r = (pixelValue shr 16 and 0xFF) / 255.0f
            val g = (pixelValue shr 8 and 0xFF) / 255.0f
            val b = (pixelValue and 0xFF) / 255.0f
            inputBuffer.putFloat(r)
            inputBuffer.putFloat(g)
            inputBuffer.putFloat(b)
        }
        return inputBuffer
    }

    @Throws(IOException::class)
    private fun loadModelFile(): ByteBuffer {
        val fileDescriptor = assets.openFd(MODEL_PATH)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    data class DiseaseDetectionResult(val name: String, val boundingBox: RectF)
}
