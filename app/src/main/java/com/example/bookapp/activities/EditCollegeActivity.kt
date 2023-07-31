package com.example.bookapp.activities

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
import com.example.bookapp.R
import com.example.bookapp.databinding.ActivityEditCollegeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditCollegeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditCollegeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    var id :String = ""
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditCollegeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        val intent = intent
        id = intent.getStringExtra("collegeId")!!
        loadCollegeName()
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.btnEditCollege.setOnClickListener {
            if (imageUri == null){
                updateCollege("")
            }else{
                updateCollege("$imageUri")
            }

        }

        binding.imgeCollege.setOnClickListener {
            showImageAttackMenu()
        }

    }

    private fun loadCollegeName(){
        val ref = FirebaseDatabase.getInstance().getReference("Colleges").child(id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val college = snapshot.child("college").value.toString()
                binding.addCollegeName.setText(college)

            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
    }

    private fun updateCollege(uri: String){
        progressDialog.show()
        val hasmap = HashMap<String,Any?>()
        hasmap["college"] = binding.addCollegeName.text.toString()
        hasmap["imageUri"] = "$uri"
        val ref = FirebaseDatabase.getInstance().getReference("Colleges").child(id)
        ref.updateChildren(hasmap).addOnCompleteListener { task->
            if (task.isSuccessful){
                progressDialog.dismiss()
                Toast.makeText(this@EditCollegeActivity, "Updated", Toast.LENGTH_SHORT).show()
            }
        }

            .addOnFailureListener { e->

                progressDialog.dismiss()
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
}