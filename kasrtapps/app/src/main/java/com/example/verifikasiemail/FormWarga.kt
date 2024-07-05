package com.example.verifikasiemail

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class FormWarga: Activity() {

    private lateinit var imageView: ImageView
    private lateinit var selectImageButton: MaterialButton
    private lateinit var uploadImageButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var namaEditText: EditText
    private lateinit var namaLengkapEditText: EditText
    private lateinit var alamatEditText: EditText
    private lateinit var noHpEditText: EditText

    private val PICK_IMAGE_REQUEST = 100
    private val CAMERA_REQUEST_CODE = 200
    private var selectedImageUri: Uri? = null
    private var capturedImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formwarga)

        imageView = findViewById(R.id.imageView)
        selectImageButton = findViewById(R.id.selectImage)
        uploadImageButton = findViewById(R.id.uploadImage)
        progressBar = findViewById(R.id.progressBar)
        namaEditText = findViewById(R.id.Nama)
        namaLengkapEditText = findViewById(R.id.NamaL)
        alamatEditText = findViewById(R.id.Alamat)
        noHpEditText = findViewById(R.id.Nohp)

        storageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("reports")

        selectImageButton.setOnClickListener {
            showImagePickerDialog()
        }

        uploadImageButton.isEnabled = false
        uploadImageButton.setOnClickListener {
            uploadImageToFirebase()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Dari Galeri", "Dari Kamera")

        val builder = android.app.AlertDialog.Builder(this)
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> pickImageFromGallery()
                1 -> openCamera()
            }
        }
        builder.show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                imageView.setImageURI(uri)
                uploadImageButton.isEnabled = true
                capturedImageBitmap = null
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            capturedImageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(capturedImageBitmap)
            uploadImageButton.isEnabled = true
            selectedImageUri = null
        }
    }

    private fun uploadImageToFirebase() {
        progressBar.visibility = ProgressBar.VISIBLE
        uploadImageButton.isEnabled = false

        if (selectedImageUri != null) {
            val storageRef = storageReference.child("images/${UUID.randomUUID()}")
            val uploadTask = storageRef.putFile(selectedImageUri!!)

            uploadTask.addOnFailureListener {
                progressBar.visibility = ProgressBar.GONE
                uploadImageButton.isEnabled = true
                Log.e("FormwargaActivity", "Failed to upload image: ${it.message}")
                Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    saveReportToDatabase(uri.toString())
                }
            }
        } else if (capturedImageBitmap != null) {
            val baos = ByteArrayOutputStream()
            capturedImageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val storageRef = storageReference.child("images/${UUID.randomUUID()}")
            val uploadTask = storageRef.putBytes(data)

            uploadTask.addOnFailureListener {
                progressBar.visibility = ProgressBar.GONE
                uploadImageButton.isEnabled = true
                Log.e("FormwargaActivity", "Failed to upload image: ${it.message}")
                Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    saveReportToDatabase(uri.toString())
                }
            }
        }
    }

    private fun saveReportToDatabase(imageUrl: String) {
        val nama = namaEditText.text.toString()
        val namaLengkap = namaLengkapEditText.text.toString()
        val alamat = alamatEditText.text.toString()
        val noHp = noHpEditText.text.toString()
        val reportId = databaseReference.push().key ?: return

        val report = mapOf(
            "id" to reportId,
            "nama" to nama,
            "namaLengkap" to namaLengkap,
            "alamat" to alamat,
            "noHp" to noHp,
            "imageUrl" to imageUrl
        )

        databaseReference.child(reportId).setValue(report).addOnCompleteListener { task ->
            progressBar.visibility = ProgressBar.GONE
            uploadImageButton.isEnabled = true
            if (task.isSuccessful) {
                Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save report: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
