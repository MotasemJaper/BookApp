package com.example.bookapp.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookapp.R
import com.example.bookapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var googleApiClient: GoogleApiClient
    companion object {
        private const val RC_SIGN_IN = 9001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        binding.loginBtn.setOnClickListener {
            validateData()
        }
        binding.forgotTv.setOnClickListener {
            startActivity(Intent(this@LoginActivity,ForgetPasswordActivity::class.java))
        }
        binding.skipBtn.setOnClickListener {
            startActivity(Intent(this, DashboredUserActivity::class.java))
            finish()
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        binding.btnSignWithGoogle.setOnClickListener {
            signIn()
        }

        binding.passwordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            @SuppressLint("ResourceAsColor")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val inputText = s?.toString()?.trim()


                if (inputText?.isNotEmpty() == true) {
                    binding.loginBtn.setBackgroundResource(R.drawable.btn_shape_dark)

                } else {
                    binding.loginBtn.setBackgroundResource(R.drawable.shape_button01)

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Toast.makeText(this, "Google Play Services error", Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result: GoogleSignInResult? = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            if (result?.isSuccess == true) {
                // Google Sign-In was successful, handle the authenticated user here.
                val account = result.signInAccount
                // Use the 'account' object to get user details like email, name, profile photo, etc.
                account!!.email?.let { account.id?.let { it1 -> createUserAccount(it, it1) } }
                account.email?.let { account.displayName?.let { it1 ->
                    account.idToken?.let { it2 ->
                        account.id?.let { it3 ->
                            updateUSerInfo(it,account.photoUrl,
                                it1, it2, it3
                            )
                        }
                    }
                } }
            } else {
                // Google Sign-In failed, handle the error.
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createUserAccount(email: String, passWord: String) {
        progressDialog.setMessage("Creating Account..")
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, passWord)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed creating account due to ${e.message} ", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUSerInfo(email:String, image: Uri?, name:String,idToken: String, passWord: String) {
        progressDialog.setMessage("Saving user info..")
        val timestamp = System.currentTimeMillis()
        val uId = firebaseAuth.uid
        var hashmap: HashMap<String, Any?> = HashMap()
        hashmap["uid"] = uId
        hashmap["email"] = email
        hashmap["name"] = name
        hashmap["passWord"] = passWord
        hashmap["idToken"] = idToken
        hashmap["profileImage"] = image
        hashmap["userType"] = "user"
        hashmap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uId!!)
            .setValue(hashmap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, DashboredUserActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed saving user due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private var email = ""
    private var password = ""

    private fun validateData() {
        email = binding.emailET.text.toString().trim()
        password = binding.passwordET.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address ..", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Enter Password..", Toast.LENGTH_SHORT).show()
        } else {
            loginUser()
        }
    }

    private fun loginUser() {
        progressDialog.setMessage("Logging In..")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUser()
            }
            .addOnFailureListener { e ->

                progressDialog.dismiss()
                Toast.makeText(this, "Login Failed due to ${e.message} ..", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking User...")
        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    val userType = snapshot.child("userType").value

                    if (userType == "user") {
                        startActivity(Intent(this@LoginActivity, DashboredUserActivity::class.java))
                        finish()
                    } else if (userType == "admin") {
                        startActivity(Intent(this@LoginActivity, DashboredAdminActivity::class.java))
                        finish()
                    }else if (userType == "SuperAdmin"){
                        startActivity(Intent(this@LoginActivity, Dashboard::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
