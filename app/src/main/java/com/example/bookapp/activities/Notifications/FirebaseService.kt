package com.example.bookapp.activities.Notifications

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseService : FirebaseMessagingService() {
    var fuser: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        val user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        val tokenRefresh: String = FirebaseMessaging.getInstance().getToken().toString()
        if (user != null) {
            upDateToken(tokenRefresh)
        }
    }

    private fun upDateToken(tokenRefresh: String) {
        val user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token = Token(tokenRefresh)
        reference.child(user!!.getUid()).setValue(token)

    }
}