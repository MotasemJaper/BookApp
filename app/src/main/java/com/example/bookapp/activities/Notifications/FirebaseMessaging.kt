package com.example.bookapp.activities.Notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.example.bookapp.R
import com.example.bookapp.activities.Model.NotificationModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessaging : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        val user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        val tokenRefresh: String = FirebaseMessaging.getInstance().token.toString()
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

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val sp: SharedPreferences = getSharedPreferences("SP_USER", MODE_PRIVATE)
        val getCurrentUser: String? = sp.getString("Current_USERID", "None")
        Log.d("REMOTEMESSAGE", "onMessageReceived: " + remoteMessage.getData())
        val sent: String? = remoteMessage.getData().get("sent")
        val user: String? = remoteMessage.getData().get("user")
        Log.d("SENTNOTIFICATION", "onMessageReceived: $sent")
        Log.d("USERNOTIFICATION", "onMessageReceived: $user")
        showNotification(applicationContext, "title", "body", null)
        val fuser: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        if (fuser != null && sent == fuser.getUid()) {
            if (getCurrentUser != user) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendAndAboveNotification(remoteMessage)
                } else {
                    sendNormalNotification(remoteMessage)
                }
            }
        }
    }

    fun showNotification(context: Context, title: String?, body: String?, intent: Intent?) {
        val notificationManager: NotificationManager = context.getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
        val notificationId = 1
        val channelId = "channel-01"
        val channelName = "Channel Name"
        val importance: Int = NotificationManager.IMPORTANCE_HIGH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )
            notificationManager.createNotificationChannel(mChannel)
        }
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.sp)
            .setContentTitle(title)
            .setContentText(body)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(intent!!)
        val resultPendingIntent: PendingIntent? = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        notificationManager.notify(notificationId, mBuilder.build())
    }

    private fun sendNormalNotification(remoteMessage: RemoteMessage) {
        val user: String? = remoteMessage.getData().get("user")
        val title: String? = remoteMessage.getData().get("title")
        val icon: String? = remoteMessage.getData().get("icon")
        val body: String? = remoteMessage.getData().get("body")
        Log.d("BODYNOTIFACATION", "sendNormalNotification: $body")
        val notification: RemoteMessage.Notification = remoteMessage.getNotification()!!
        val i = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(applicationContext, NotificationModel::class.java)
        val bundle = Bundle()
        bundle.putString("userId", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pIntent: PendingIntent =
            PendingIntent.getActivity(
                this,
                i,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        val defSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this)
            .setSmallIcon(icon!!.toInt())
            .setContentText(body)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setSound(defSoundUri)
            .setContentIntent(pIntent)
        val manager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var j = 0
        if (i > 0) {
            j = i
        }
        manager.notify(j, builder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun sendAndAboveNotification(remoteMessage: RemoteMessage) {
        val user: String? = remoteMessage.getData().get("user")
        val title: String? = remoteMessage.getData().get("title")
        val icon: String? = remoteMessage.getData().get("icon")
        val body: String? = remoteMessage.getData().get("body")
        val notification: RemoteMessage.Notification = remoteMessage.getNotification()!!
        val i = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(applicationContext, NotificationModel::class.java)
        val bundle = Bundle()
        bundle.putString("userId", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pIntent: PendingIntent =
            PendingIntent.getActivity(
                this,
                i,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        val defSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notification1 = OreoAndAboveNotification(this)
        val builder = notification1.getNotification(title, body, pIntent, defSoundUri, icon!!)
        var j = 0
        if (i > 0) {
            j = i
        }
        notification1.manager!!.notify(j, builder.build())
    }
}