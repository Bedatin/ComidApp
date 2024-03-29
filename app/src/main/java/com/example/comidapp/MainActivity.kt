package com.example.comidapp

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.comidapp.toolbarActivities.CalendarActivity
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime

const val TOPIC = "/topics/Maison"

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //No subscribir a todo el mundo
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)


        cuadrado.setInAnimation(this, android.R.anim.fade_in)
        cuadrado.setOutAnimation(this, android.R.anim.fade_out)

        cuadrado.setOnClickListener {
            cuadrado.showNext()
        }

        /*for (i in 0..10){
            Handler().postDelayed({cuadrado.showNext()}, (i*4000).toLong())
        }*/

        bajaShared()
        btnCalendar.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnMensaje.setOnClickListener {
            val intent = Intent(this, MensajeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        //Shared
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        var emailId = ""

        try{
            emailId = sharedPref.getString("emailId", "")!!
            if (emailId == "") {
                fondo.setBackgroundResource(R.color.colorAccent)
            } else {
                fondo.setBackgroundResource(R.drawable.fondo_degradado)
            }
        }catch (e:Exception){}
    }

    fun bajaShared(){
        //Shared
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        var emailId = ""

        emailId = sharedPref.getString("emailId", "")!!
        if (emailId == "") {
            fondo.setBackgroundResource(R.color.colorAccent)
        } else {
            fondo.setBackgroundResource(R.color.azulFondo)
        }
    }
}
