package com.example.bookapp.activities.Notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi

class OreoAndAboveNotification(base: Context?) : ContextWrapper(base) {
    private var notificationManager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manager!!.createNotificationChannel(channel)
    }

    val manager: NotificationManager?
        get() {
            if (notificationManager == null) {
                notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return notificationManager
        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getNotification(
        title: String?,
        body: String?,
        pIntent: PendingIntent?,
        soundUri: Uri?,
        icon: String
    ): Notification.Builder {
        return Notification.Builder(applicationContext, ID)
            .setContentIntent(pIntent)
            .setContentTitle(title)
            .setContentText(body)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setSmallIcon(icon.toInt())
    }

    companion object {
        private const val ID = "some_id"
        private const val NAME = "FirebaseApp"
    }
}