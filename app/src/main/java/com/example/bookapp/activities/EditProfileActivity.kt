package com.example.bookapp.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import com.example.bookapp.R
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.databinding.ActivityEditProfileBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private var imageUri: Uri? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.profileIv.setOnClickListener {
            showImageAttackMenu()
        }

        binding.updateBtn.setOnClickListener {
            validateData()
        }

    }

    var name = ""
    var password = ""
    var cPassword = ""


    private fun validateData() {
        name = binding.nameEditProfile.text.toString().trim()
        password = binding.passwordEditProfile.text.toString().trim()
        cPassword = binding.cPasswordEditProfile.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()

        } else {
            if (password.isNotEmpty()){
                if (cPassword.isNotEmpty()){
                    if (password.equals(cPassword)){
                        if (imageUri == null) {
                            updateProfile("")
                        } else {
                            uploadImage()
                        }
                    }else{
                        Toast.makeText(this, "Please Sure!! Confirm Password", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "Enter Confirm Password", Toast.LENGTH_SHORT).show()
                }
            }else if (imageUri == null){
                updateProfile("")
            }else{
                uploadImage()
            }
        }
    }
    private fun uploadImage() {
        progressDialog.setMessage("Uploading profile Image")
        progressDialog.show()

        val filepathName = "ProfileImages/" + firebaseAuth.uid

        val reference = FirebaseStorage.getInstance().getReference(filepathName)
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadImageUrl = "${uriTask.result}"
                updateProfile(uploadImageUrl)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to upload image due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun updateProfile(uploadedImageUrl: String) {
        progressDialog.setMessage("Uploading Profile...")

        val hasMap: HashMap<String, Any> = HashMap()
        hasMap["name"] = "$name"
        hasMap["password"] = "$password"
        hasMap["cPassword"] = "$cPassword"
        binding.passwordEditProfile.text!!.clear()
        binding.cPasswordEditProfile.text!!.clear()
        if (imageUri != null) {
            hasMap["profileImage"] = uploadedImageUrl
        }

        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(firebaseAuth.uid!!)
            .updateChildren(hasMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to update image due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun showImageAttackMenu() {
        val popupMenu = PopupMenu(this, binding.profileIv)
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

            if (result.resultCode == Activity.RESULT_OK) {

                val data = result.data
                //  imageUri = data!!.data

                binding.profileIv.setImageURI(imageUri)
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

                binding.profileIv.setImageURI(imageUri)
            } else {

                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        },
    )

    private fun loadUserInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("userType").value}"
                    val formattedDate = MyApplication.formatTimeStamp(timestamp.toLong())

                    binding.nameEditProfile.setText(name)
                    binding.emailEditProfile.text = email
                    try {
                        Glide.with(this@EditProfileActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.profilepic)
                            .into(binding.profileIv)
                    } catch (e: Exception) {
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

}