package com.junseok.bluebox

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.app.NotificationCompat
import android.content.Context
import com.squareup.okhttp.OkHttpClient

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private final val FCMTAG = "[FCM Service]"
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(FCMTAG,"Refresh Token : ${p0}")
    }
}