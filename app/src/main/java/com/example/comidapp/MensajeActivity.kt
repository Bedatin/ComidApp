package com.example.comidapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.comidapp.notifications.AlarmReceiver
import com.example.comidapp.notifications.NotificationData
import com.example.comidapp.notifications.PushNotification
import com.example.comidapp.notifications.RetrofitInstance
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_mensaje.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*


const val TOPIC = "/topics/Maison"

class MensajeActivity : AppCompatActivity() {

    val TAG = "myApp"

    //Alarma
    private var notificationsTime: TextView? = null
    private val alarmID = 1
    private val settings: SharedPreferences? = null

    private var destinatario = TOPIC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensaje)

        //No subscribir a todo el mundo
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        destino()

        btnSend.setOnClickListener {
            val title = "Maison"
            val message = et_mensaje.text.toString()
            if(message.isNotEmpty()){
                PushNotification(
                    NotificationData(title, message, "mensaje"),
                    destinatario
                    ).also {
                    sendNotification(it)
                }
            }
            et_mensaje.text.clear()
        }


        btnAlarm.setOnClickListener {
            alarma2()
        }

        btnAlarm2.setOnClickListener {
            alarma3()
            et_time.text.clear()
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

    fun destino(){
        btnTodos.setOnClickListener {
            destinatario = TOPIC
        }
        btnRuth.setOnClickListener {
            DataManager.db.collection("people").document("ruth@garciacobo.com").get()
                .addOnSuccessListener { result ->
                    destinatario = result.get("token").toString()
                }
        }
        btnJavi.setOnClickListener {
            DataManager.db.collection("people").document("j.sanz.bedate@gmail.com").get()
                .addOnSuccessListener { result ->
                    destinatario = result.get("token").toString()
                }
        }
    }

    fun alarma2(){
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent: PendingIntent
        pendingIntent = PendingIntent.getBroadcast(
            this, 1000, alarmIntent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val hora = System.currentTimeMillis()
        Log.i("hora", hora.toString())
        val h = LocalDateTime.now().hour
        val m = LocalDateTime.now().minute
        val s =  LocalDateTime.now().second
        val t= h.toLong()*3600000 + m.toLong()*60000 + s*1000
        val tiempo = 20*3600000L + 39*60000L
        val tiempazo = 22*3600000L  - t
        Log.i("hora", "1 $tiempo")
        Log.i("hora", "2 $tiempazo")
        Log.i("hora", "3 " + SystemClock.setCurrentTimeMillis(200L).toString())
        Log.i("hora", "4 " + System.currentTimeMillis().toString())
        //alarmManager.set(AlarmManager.RTC_WAKEUP,tiempo,pendingIntent)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+tiempazo, pendingIntent)
    }

    fun alarma3(){
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent: PendingIntent
        pendingIntent = PendingIntent.getBroadcast(
            this, 1000, alarmIntent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val hora = System.currentTimeMillis()
        Log.i("hora", hora.toString())
        val et = et_time.text.toString().toCharArray()
        val hh = (et[0].toString()+ et[1]).toInt()
        val mm = (et[3].toString()+ et[4]).toInt()
        val ss = (et[6].toString()+ et[7]).toInt()
        val h =  LocalDateTime.now().hour
        val m =  LocalDateTime.now().minute
        val s =  LocalDateTime.now().second
        val t= (hh-h)*3600000 + (mm-m).toLong()*60000 + (ss-s)*1000
        Log.i("hora", "$hh, $mm,$ss")
        //alarmManager.set(AlarmManager.RTC_WAKEUP,tiempo,pendingIntent)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+t, pendingIntent)
    }
    fun alarma(){
        val alarmManager =
            this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent: PendingIntent
        pendingIntent = PendingIntent.getBroadcast(
            this, 20, alarmIntent,
            PendingIntent.FLAG_ONE_SHOT
        )
        alarmIntent.data = Uri.parse("custom://" + System.currentTimeMillis())
        alarmManager[AlarmManager.RTC_WAKEUP, 20] = pendingIntent


    }
}
