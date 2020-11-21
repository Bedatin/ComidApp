package com.example.comidapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import com.example.comidapp.toolbarActivities.CalendarActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bajaShared()
        btnCalendar.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnToque.setOnClickListener {
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
                fondo.setBackgroundResource(R.color.azulFondo)
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
