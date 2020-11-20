package com.example.comidapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.comidapp.notifications.NotificationData
import com.example.comidapp.notifications.PushNotification
import com.example.comidapp.notifications.RetrofitInstance
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_mensaje.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

const val TOPIC = "/topics/Maison"

class MensajeActivity : AppCompatActivity() {

    val TAG = "MensajeActivity"

    private var destinatario = TOPIC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensaje)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        destino()

        btnSend.setOnClickListener {
            val title = "Maison"
            val message = et_mensaje.text.toString()
            if(message.isNotEmpty()){
                PushNotification(
                    NotificationData(title, message),
                    destinatario
                    ).also {
                    sendNotification(it)
                }
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
}
