package com.example.bookapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.databinding.ActivityContactUsBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ContactUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactUsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var pdfUri: Uri? = null
    private var uploadedPdfUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.btnForSend.setOnClickListener {
            if (binding.emailUserForContact.text.isEmpty() &&
                binding.messgae.text.isEmpty()
            ) {

                Toast.makeText(
                    this@ContactUsActivity,
                    "Please Enter your Message !!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                uploadPdfToStorage()


            }

        }

        binding.uploadFilePdf.setOnClickListener {
            pdfPickIntent()
        }
    }

    private fun uploadMessage(url: String) {
        progressDialog.show()
        val timestamp = System.currentTimeMillis()
        val hasMap: HashMap<String, Any> = HashMap()
        hasMap["name"] = binding.nameUserForContact.text.toString().trim()
        hasMap["email"] = binding.emailUserForContact.text.toString().trim()
        hasMap["message"] = binding.messgae.text.toString().trim()
        hasMap["timestamp"] = "$timestamp"
        hasMap["id"] = "$timestamp"
        hasMap["Uid"] = "${firebaseAuth.uid}"
        hasMap["url"] = "$url"
        val ref = FirebaseDatabase.getInstance().getReference("ContactUs")
        ref.child("$timestamp").setValue(hasMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressDialog.dismiss()
                val r = FirebaseDatabase.getInstance().getReference("PdfForTowRelation")
                r.child("$timestamp").updateChildren(hasMap)
                Toast.makeText(this@ContactUsActivity, "Thank You For Send Message !!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this@ContactUsActivity, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

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
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


    private fun pdfPickIntent() {

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }

    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {

                pdfUri = result.data!!.data
            } else {
                Toast.makeText(this@ContactUsActivity, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )
    private fun uploadPdfToStorage() {
        if (pdfUri == null) {
            uploadMessage("")
        } else {
            progressDialog.setMessage("Uploading PDF...")
            progressDialog.show()
            val timestamp = System.currentTimeMillis()
            val filePathAndName = "Pdf/$timestamp"
            val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
            storageReference.putFile(pdfUri!!)
                .addOnSuccessListener { taskSnapshot ->

                    val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    uploadedPdfUri = "${uriTask.result}"
                    uploadMessage(uploadedPdfUri)
                    binding.emailUserForContact.text.clear()
                    binding.messgae.text.clear()
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this@ContactUsActivity, "Failed to upload due  to ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


}