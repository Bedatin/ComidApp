package com.example.comidapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.comidapp.DataManager.db
import com.example.comidapp.copiaEOI.AddTaskActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_listado.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class Listado3 : Fragment() {

    lateinit var mRecyclerView: RecyclerView

    var muestra: ArrayList<Comida> = ArrayList()

    val arrayDocs = arrayListOf<String>()
    val arrayCom = arrayListOf<Comida>()

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

        bajaComida2()
        //Log.i("myapp", muestra.toString())
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
        fab3.setOnClickListener {
            bajaComida2()
        }

        btnComida.setOnClickListener {
            muestra.sortBy { it.comida }
        }
        btnTipo.setOnClickListener {
            muestra.sortByDescending { it.comida }
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

    fun bajaComida2() = CoroutineScope(Dispatchers.IO).launch {
        arrayDocs.clear()
        arrayCom.clear()
        val querySnapshot = comidaRef.get().await()
        for (document in querySnapshot.documents) {
            val a = document.id
            Log.i("myapp", a)
            arrayDocs.add(a)
        }
        bajaComida3()
    }

    fun bajaComida3() {
        for (i in 0 until arrayDocs.size) {
            db.collection("comida").document(arrayDocs[i]).get().addOnSuccessListener { result ->
                val a1 = result.get("id").toString()
                val a2 = result.get("comida").toString()
                val a3 = result.get("tipo").toString()
                val a4 = result.get("tiempo").toString().toInt()
                val af = Comida(a1, a2, a3, a4)
                Log.i("myapp", "$a1 $a2 $a3 $a4, ${af.id}")
                arrayCom.add(af)
            }

        }

        muestra = arrayCom
    }

    fun setUpRecyclerView(lista: ArrayList<Comida>) {
        mRecyclerView = rvComidas
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        //mAdapter.RecyclerAdapter(lista, this)
        mRecyclerView.adapter = Listado2Adapder(lista)
    }
}

