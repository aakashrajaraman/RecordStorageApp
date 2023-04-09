package com.example.firstapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.firstapp.databinding.ActivityMainBinding
import com.google.firebase.storage.FirebaseStorage


open class MainActivity : AppCompatActivity() {

private lateinit var binding : ActivityMainBinding
    private lateinit var storage: FirebaseStorage
    private var filePickerLauncher: ActivityResultLauncher<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://firstapp-4269e.appspot.com/")

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("FirstApp", "Taken")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
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
        binding.createButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/*"
            intent.putExtra(Intent.EXTRA_TITLE, "example.txt")
            createFileActivityResultLauncher.launch(intent)
            }
        binding.fileButton.setOnClickListener {
            filePickerLauncher?.launch("application/pdf")
        }
        }
    private val createFileActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                val outputStream = contentResolver.openOutputStream(uri)
                outputStream?.use {
                    it.write("This is an example text file.".toByteArray())
                }
            }
        }
    }
    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, contentResolver.getType(uri))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MyApp", "Permission granted")
            } else {
                Log.d("MyApp", "Permission denied")
            }
        }
    }

    }



