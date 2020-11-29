package com.example.comidapp.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.comidapp.LoadActivity
import com.example.comidapp.R
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random


private const val CHANNEL_ID = "maison_channel"
const val TOPIC = "/topics/Maison"

class AlarmReceiver : BroadcastReceiver() {

    val TAG = "myApp"

    override fun onReceive(context: Context, intent: Intent) {
        /*val service1 = Intent(context, FirebaseService::class.java)
        service1.data = Uri.parse("custom://" + System.currentTimeMillis())
        ContextCompat.startForegroundService(context, service1)*/


        val notificationID = Random.nextInt()
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager


        val intent = Intent(context, LoadActivity::class.java)
        intent.putExtra("notificationR", true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            //.setContentTitle(message.data["title"])
            //.setContentText(message.data["message"])
            //.setContent(contentView)
            .setSmallIcon(R.drawable.ic_gorro_chef)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        //notificationManager.notify(notificationID, notification)

        val title = "Maison"
        val message = "Sacar garbanzos"
        if(message.isNotEmpty()){
            PushNotification(
                NotificationData(title, message, "recordatorio"),
                TOPIC
            ).also {
                sendNotification(it)
            }
        }





    }
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            }else{
                Log.e(TAG, response.errorBody().toString())
            }
        }catch (e:Exception){
            Log.e(TAG, e.toString())
        }
    }

}