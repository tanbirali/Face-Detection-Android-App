package com.example.appwithml

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.widget.Button
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)


        button.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(intent.resolveActivity(packageManager) != null){
                startActivityForResult(intent, 123)
            }else{
                Toast.makeText(this, "Oops Something Went Wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123 && resultCode == RESULT_OK){
            val extras = data?.extras
            val bitmap = extras?.get("data") as? Bitmap
            if (bitmap != null) {
                detectFace(bitmap)
            }

        }
    }
    fun detectFace(bitmap: Bitmap){
        val resultTextView = findViewById<TextView>(R.id.outputText)
        val details = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        var detector = FaceDetection.getClient(details)
        val image = InputImage.fromBitmap(bitmap, 0)

        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully
                var i = 1
                var resultText = ""
                for(face in faces){
                    resultText = "Face number: $i" +
                                "\n Smile: ${face.smilingProbability?.times(100)}%" +
                                "\n Left Eye Open: ${face.leftEyeOpenProbability?.times(100)}%" +
                                "\n Right Eye Open: ${face.rightEyeOpenProbability?.times(100)}%"
                    i++;
                }
                if (faces.isEmpty()){
                    Toast.makeText(this, "No Face Detected", Toast.LENGTH_SHORT).show()
                }else{
                    resultTextView.text = resultText
                }

            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Toast.makeText(this, "Oops Something Went Wrong", Toast.LENGTH_SHORT).show()

            }
    }
}