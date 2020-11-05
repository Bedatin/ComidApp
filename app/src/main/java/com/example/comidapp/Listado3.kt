package com.example.comidapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.comidapp.DataManager.db
import com.example.comidapp.copiaEOI.AddTaskActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_listado.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class Listado3 : Fragment() {

    lateinit var mRecyclerView: RecyclerView

    var muestra: ArrayList<Comida> = ArrayList()

    val comidaRef = Firebase.firestore.collection("comida")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listado, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bajaComida()
        /*Handler().postDelayed({
            setUpRecyclerView(muestra)
        }, 9000)*/
        fab.setOnClickListener {
            val intent = Intent(context, AddTaskActivity::class.java)
            startActivity(intent)
        }
        fab2.setOnClickListener {
            setUpRecyclerView(muestra)
        }
    }

    fun saveComida(comida: Comida) = CoroutineScope(Dispatchers.IO).launch {
        try {
            comidaRef.add(comida).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "OK", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun bajaComida() = CoroutineScope(Dispatchers.IO).launch {
        val listado: ArrayList<Comida> = ArrayList()
        try {
            val querySnapshot = comidaRef.get().await()
            val sb = StringBuilder()
            for (document in querySnapshot.documents) {
                val comidabajada = document.toObject<Comida>()
                listado.add(comidabajada!!)
                sb.append("$comidabajada")
            }

        } catch (e: Exception) {
            val querySnapshot = comidaRef.get().await()
            val a = Comida("a", "a", "a", 1)
            listado.add(a)
        }
        muestra=listado
    }

    fun setUpRecyclerView(lista: ArrayList<Comida>) {
        mRecyclerView = rvComidas
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        //mAdapter.RecyclerAdapter(lista, this)
        mRecyclerView.adapter = Listado2Adapder(lista)
    }
}

