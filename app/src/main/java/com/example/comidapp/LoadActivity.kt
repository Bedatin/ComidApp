package com.example.comidapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.comidapp.toolbarActivities.CalendarActivity
import com.facebook.internal.Utility.arrayList
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.*


class LoadActivity : AppCompatActivity() {

    val arrayDocsCom = arrayListOf<String>()
    val arrayCom = arrayListOf<Comida>()

    val arrayDocsDias = arrayListOf<String>()
    val arrayDias = arrayListOf<Dia>()

    val comidaRef = Firebase.firestore.collection("comida")
    val diaRef = Firebase.firestore.collection("Dia")

    var listadoDias: ArrayList<Dia> = arrayListOf()
    var listadoComida: ArrayList<Comida> = ArrayList()

    val semanaInglesa = arrayListOf<String>(
        "MONDAY",
        "TUESDAY",
        "WEDNESDAY",
        "THURSDAY",
        "FRIDAY",
        "SATURDAY",
        "SUNDAY"
    )

    val hoyLetra = LocalDate.now().dayOfWeek.toString()
    var lunes = LocalDate.now()
    var calendarioBajar: ArrayList<String> = arrayListOf()
    var calendario: ArrayList<Dia> = arrayListOf()


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_load)
        CoroutineScope(Dispatchers.IO).launch{
            bajaComida()
            bajaCalendario()
        }

    }

    fun bajaComida() = CoroutineScope(Dispatchers.IO).launch {
        arrayDocsCom.clear()
        arrayCom.clear()
        val querySnapshot = comidaRef.get().await()
        for (document in querySnapshot.documents) {
            arrayDocsCom.add(document.id)
        }
        bajaComida2()
    }

    fun bajaComida2() {
        listadoComida.clear()
        for (i in 0 until arrayDocsCom.size) {
            DataManager.db.collection("comida").document(arrayDocsCom[i]).get()
                .addOnSuccessListener { result ->
                    val a1 = result.get("id").toString()
                    val a2 = result.get("comida").toString()
                    val a3 = result.get("tipo").toString()
                    val a4 = result.get("tiempo").toString().toInt()
                    val af = Comida(a1, a2, a3, a4)
                    arrayCom.add(af)
                }
        }
        try {
            listadoComida = arrayCom
        } catch (e: Exception) {
        }
    }

    fun bajaDias() = CoroutineScope(Dispatchers.IO).launch {
        arrayDocsDias.clear()
        arrayDias.clear()
        val querySnapshot = diaRef.get().await()
        for (document in querySnapshot.documents) {
            arrayDocsDias.add(document.id)
        }
        bajaDias2()
    }

    fun bajaDias2() = CoroutineScope(Dispatchers.IO).launch {
        var ordena: List<Dia> = arrayListOf()
        listadoDias.clear()
        for (i in 0 until arrayDocsDias.size) {
            DataManager.db.collection("Dia").document(arrayDocsDias[i]).get()
                .addOnSuccessListener { result ->
                    val a1 = result.get("fecha").toString()
                    val a2 = result.get("dia").toString()
                    val a3 = result.get("comida").toString()
                    val a4 = result.get("cena").toString()
                    Log.i("dias", "dia $a2")
                    val af = Dia(a1, a2, a3, a4)
                    arrayDias.add(af)
                    Log.i("dias", "array1 ${af.dia}")

                }.await()
        }
        try {
            ordena = arrayDias.sortedBy { it.fecha }
            listadoDias.addAll(ordena)
            //Log.i("dias", "array2 ${listadoDias[0].dia}")
        } catch (e: Exception) {
        }
        enviaInfo()
        finish()
    }

    fun bajaCalendario() = CoroutineScope(Dispatchers.IO).launch{
        calendario.clear()
        val sumaDias: ArrayList<Dia> = arrayListOf()
        var ordena: List<Dia> = arrayListOf()
        for (i in 0..6){
            if(hoyLetra == semanaInglesa[i]){
                lunes = LocalDate.now().minusDays(i.toLong())
            }
        }
        for (i in 0..17){
            calendarioBajar.add(lunes.plusDays(i.toLong()).toString())
            Log.i("fala", "array: ${calendarioBajar[i]}")

        }
        for (i in 0 until calendarioBajar.size) {
            DataManager.db.collection("Dia").document(calendarioBajar[i]).get()
                .addOnSuccessListener { result ->
                    val a1 = result.get("fecha").toString()
                    val a2 = result.get("dia").toString()
                    val a3 = result.get("comida").toString()
                    val a4 = result.get("cena").toString()
                    if(a1 != "null"){
                        val af = Dia(a1, a2, a3, a4)
                        sumaDias.add(af)
                        Log.i("fala", "array1 ${af.dia}")
                    }
                    else{
                        val af = Dia(calendarioBajar[i], "", "", "")
                        sumaDias.add(af)
                        Log.i("fala", "array2 ${af.dia}")
                    }
                }.await()

            DataManager.db.collection("Dia").document(calendarioBajar[i]).get()
                .addOnFailureListener {
                    val af = Dia(calendarioBajar[i], "L", "", "")
                    sumaDias.add(af)
                    Log.i("fallo", "1")
                }.await()
        }
        try {
            ordena = sumaDias.sortedBy { it.fecha }
            calendario.addAll(ordena)
            //Log.i("dias", "array2 ${listadoDias[0].dia}")
        } catch (e: Exception) {
        }
        terminar()
    }

    fun terminar() {
        enviaInfo()
        finish()
    }


    fun enviaInfo(){
        val sharedPref= getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gson = Gson()
        val json1 = gson.toJson(listadoComida)
        val json2 = gson.toJson(calendario)
        editor.apply {
            putString("listadoComida", json1)
            putString("listadoDias", json2)
            apply()
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }
}
