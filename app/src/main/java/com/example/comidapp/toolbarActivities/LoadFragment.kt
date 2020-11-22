package com.example.comidapp.toolbarActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.comidapp.Comida
import com.example.comidapp.DataManager
import com.example.comidapp.Dia
import com.example.comidapp.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class LoadFragment() : DialogFragment() {

    val arrayDocsCom = arrayListOf<String>()
    val arrayCom = arrayListOf<Comida>()

    val arrayDocsDias = arrayListOf<String>()
    val arrayDias = arrayListOf<Dia>()

    val comidaRef = Firebase.firestore.collection("comida")
    val diaRef = Firebase.firestore.collection("Dia")

    var listadoDias: java.util.ArrayList<Dia> = arrayListOf()
    var listadoComida: java.util.ArrayList<Comida> = java.util.ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_load, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bajaComida()
        bajaDias()
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
        dismiss()
    }
}