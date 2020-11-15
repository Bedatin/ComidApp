package com.example.comidapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.widget.RemoteViews
import com.example.comidapp.toolbarActivities.CalendarActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {
    private val TAG = "Topic"

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "package com.example.comidapp.toolbarActivities"
    private val description = "Test notification"

    private val channelIdGroup = "Maison"

    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, CalendarActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        val contentView = RemoteViews(packageName, R.layout.notification_layout)
        contentView.setTextViewText(R.id.tvNotTitle, "Comidas")
        contentView.setTextViewText(R.id.tvNotContent, "Alguien ha cambiado el calendario")


        notificationChannel =
            NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.MAGENTA
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)
        notificationManager.createNotificationChannelGroup(NotificationChannelGroup(channelIdGroup, "Maison"))

        builder = Notification.Builder(this, channelId)
            //.setContentTitle("ComidApp")
            //.setContentText("hola")
            .setContent(contentView)
            .setSmallIcon(R.drawable.btn_carta)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.btn_carta))
            .setContentIntent(pendingIntent)
            .setGroup("Maison")


        /*val topic = "highScores"

        val message: Message = Message.builder()
            .putData("score", "850")
            .putData("time", "2:45")
            .setTopic(topic)
            .build()*/

        notificationManager.notify(1234, builder.build())

        notification()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.notification != null) {
            Log.e(TAG, "Title: " + remoteMessage.notification?.title!!)
            Log.e(TAG, "Body: " + remoteMessage.notification?.body!!)
        }

        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "Data: " + remoteMessage.data)
        }
    }

    fun notification(){
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener{
            it.result?.token?.let{
                Log.i("fire", it)
            }
        }
    }
}