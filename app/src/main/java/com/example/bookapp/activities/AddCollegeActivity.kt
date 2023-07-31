package com.example.bookapp.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import com.example.bookapp.databinding.ActivityAddCollegeBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddCollegeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCollegeBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCollegeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.btnAddCollege.setOnClickListener {
            validateData()
        }
        binding.imgeCollege.setOnClickListener {
            showImageAttackMenu()
        }
    }

    private var college = ""

    private fun validateData() {

        college = binding.addCollegeName.text.toString().trim()
        if (college.isEmpty()) {

            Toast.makeText(this@AddCollegeActivity, "Enter College...", Toast.LENGTH_SHORT).show()

        } else {

            if (imageUri == null) {
                addCollageFirebase("")
            } else {
                uploadImageForCollege()
            }


        }
    }

    private fun addCollageFirebase(uri: String) {
        progressDialog.show()
        val timestamp = System.currentTimeMillis()
        val hasMap = HashMap<String, Any>()
        hasMap["id"] = "$timestamp"
        hasMap["college"] = college
        hasMap["imageUri"] = "$uri"
        hasMap["timestamp"] = "$timestamp"
        hasMap["uid"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Colleges")
        ref.child("$timestamp").setValue(hasMap)
            .addOnSuccessListener {

                progressDialog.dismiss()
                Toast.makeText(this@AddCollegeActivity, "Added successfully...", Toast.LENGTH_SHORT)
                    .show()
                binding.addCollegeName.text.clear()


            }.addOnFailureListener { e ->

                progressDialog.dismiss()
                Toast.makeText(this@AddCollegeActivity, "${e.message}.", Toast.LENGTH_SHORT).show()

            }

        val hashNoti = HashMap<String, Any>()
        hashNoti["nameCollege"] = college + " " + "has been added to the Colleges Go check.."
        hashNoti["id"] = "$timestamp"
        hashNoti["name"] = "$college"
        val refNotification = FirebaseDatabase.getInstance().getReference("Notifications")
        refNotification.child("$timestamp").setValue(hashNoti)
        createNotificationChannel()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id"
            val channelName = "$college" + " Added"
            val channelDescription = college + " " + "has been added to the Colleges Go check.."

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = channelDescription

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showImageAttackMenu() {
        val popupMenu = PopupMenu(this, binding.imgeCollege)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Camera")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->

            val id = item.itemId
            if (id == 0) {
                pickImageCamera()
            } else if (id == 1) {
                pickImageGallery()
            }

            true
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private fun pickImageCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Temp_Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->

            if (result.resultCode == RESULT_OK) {

                val data = result.data
                //  imageUri = data!!.data

                binding.imgeCollege.setImageURI(imageUri)
            } else {

                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        },
    )

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->

            if (result.resultCode == RESULT_OK) {

                val data = result.data
                imageUri = data!!.data

                binding.imgeCollege.setImageURI(imageUri)
            } else {

                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        },
    )

    private fun uploadImageForCollege() {
        progressDialog.setMessage("Uploading College..")
        progressDialog.show()

        val filepathName = "CollegeImages/" + firebaseAuth.uid

        val reference = FirebaseStorage.getInstance().getReference(filepathName)
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadImageUrl = "${uriTask.result}"
                addCollageFirebase(uploadImageUrl)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Failed to upload image due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


}