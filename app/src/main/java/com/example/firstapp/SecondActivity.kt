package com.example.firstapp

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.firstapp.databinding.ActivityMainBinding
import com.example.firstapp.databinding.SecondActivityBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage

class SecondActivity: AppCompatActivity() {
    private lateinit var binding2 : ActivityMainBinding
    private lateinit var storage: FirebaseStorage
    private var filePickerLauncher: ActivityResultLauncher<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)

        super.onCreate(savedInstanceState)
        binding2 = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding2.root)
        storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://firstapp-4269e.appspot.com/")



        filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {

                val fileRef = storageRef.child("pdfs/${uri.lastPathSegment}")
                val uploadTask = fileRef.putFile(uri)
                uploadTask.addOnSuccessListener {
                    Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()

                }.addOnFailureListener {
                    Toast.makeText(this, "File not uploaded", Toast.LENGTH_SHORT).show()

                }
            }
        }
        binding2.fileButton.setOnClickListener {
            filePickerLauncher?.launch("application/pdf")
        }


    }



}