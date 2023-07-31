package com.example.bookapp.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookapp.R
import com.example.bookapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener() {
            onBackPressed()
        }

        binding.createAccount.setOnClickListener() {
            validateDate()
        }
        binding.passwordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            @SuppressLint("ResourceAsColor")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val inputText = s?.toString()?.trim()


                if (inputText?.isNotEmpty() == true) {
                    binding.createAccount.setBackgroundResource(R.drawable.btn_shape_dark)

                } else {
                    binding.createAccount.setBackgroundResource(R.drawable.shape_button01)

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private var name = ""
    private var email = ""
    private var password = ""
    private var cPassword = ""

    private fun validateDate() {
        name = binding.nameEt.text.toString().trim()
        email = binding.emailET.text.toString().trim()
        password = binding.passwordET.text.toString().trim()
        cPassword = binding.ConfirmPasswordET.text.toString().trim()
        val cPassword = binding.ConfirmPasswordET.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter your name..", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email Pattern..", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Enter  password..", Toast.LENGTH_SHORT).show()
        } else if (cPassword.isEmpty()) {
            Toast.makeText(this, "Confirm password..", Toast.LENGTH_SHORT).show()
        } else if (password != cPassword) {
            Toast.makeText(this, " Password doesn't match..", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        progressDialog.setMessage("Creating Account..")
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUSerInfo()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed creating account due to ${e.message} ", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUSerInfo() {
        progressDialog.setMessage("Saving user info..")
        val timestamp = System.currentTimeMillis()
        val uId = firebaseAuth.uid
        var hashmap: HashMap<String, Any?> = HashMap()
        hashmap["uid"] = uId
        hashmap["email"] = email
        hashmap["password"] = password
        hashmap["cPassword"] = cPassword
        hashmap["name"] = name
        hashmap["profileImage"] = ""
        hashmap["userType"] = "user"
        hashmap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uId!!)
            .setValue(hashmap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboredUserActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed saving user due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
