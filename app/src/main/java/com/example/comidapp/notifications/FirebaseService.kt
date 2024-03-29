package com.example.comidapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.comidapp.LoadActivity
import com.example.comidapp.MainActivity
import com.example.comidapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "maison_channel"

class FirebaseService: FirebaseMessagingService() {

    companion object{
        var sharedPref: SharedPreferences? = null

        var token: String?
        get(){
            return sharedPref?.getString("token","")
        }
        set(value){
            sharedPref?.edit()?.putString("token", value)?.apply()
        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val activity= if(message.data["type"] == "mensaje"){
            LoadActivity::class.java
        }else{
            MainActivity::class.java
        }
        val intent = Intent(this, activity)
        intent.putExtra("notificationR", true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        val contentView = RemoteViews(packageName, R.layout.notification_layout)
        contentView.setTextViewText(R.id.tvNotTitle, message.data["title"])
        contentView.setTextViewText(R.id.tvNotContent, message.data["message"])


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            //.setContentTitle(message.data["title"])
            //.setContentText(message.data["message"])
            .setContent(contentView)
            .setSmallIcon(R.drawable.ic_gorro_chef)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "chanelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName,IMPORTANCE_HIGH).apply{
            description = "Descripcion"
            enableLights(true)
            enableVibration(true)
            lightColor = Color.MAGENTA
        }
        notificationManager.createNotificationChannel(channel)
    }
}