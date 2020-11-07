package com.example.comidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comidapp.copiaEOI.AddTaskActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Main3Activity : AppCompatActivity() {

    lateinit var mRecyclerView: RecyclerView

    var muestra: ArrayList<Comida> = ArrayList()

    val arrayDocs = arrayListOf<String>()
    val arrayCom = arrayListOf<Comida>()

    var mAdapter: Listado2Adapder = Listado2Adapder(arrayCom)


    val comidaRef = Firebase.firestore.collection("comida")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val a = Comida("a", "s", "", 1)
        muestra.add(a)

        Log.i("myapp", "hola")

        bajaComida2()
        //Log.i("myapp", muestra.toString())
        /*Handler().postDelayed({
            setUpRecyclerView(muestra)
        }, 9000)*/
        fab.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }
        fab2.setOnClickListener {
            bajaComida3()
            setUpRecyclerView(muestra)
        }
    }

    fun saveComida(comida: Comida) = CoroutineScope(Dispatchers.IO).launch {
        try {
            comidaRef.add(comida).await()
            withContext(Dispatchers.Main) {
                //Toast.makeText(this, "OK", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                //Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun bajaComida() = CoroutineScope(Dispatchers.IO).launch {
        val listado: ArrayList<Comida> = ArrayList()
        DataManager.db.collection("comida").document()
        try {
            val querySnapshot = comidaRef.get().await()
            val sb = StringBuilder()
            for (document in querySnapshot.documents) {
                val comidabajada = document.toObject<Comida>()
                Log.i("myapp", comidabajada.toString())
                //listado.add(comidabajada!!)
                val a1 = document.getField<Any>("id").toString()
                val a2 = document.getField<Any>("comida").toString()
                val a3 = document.getField<Any>("tipo").toString()
                val a4 = document.getField<Any>("tiempo").toString().toInt()
                val af = Comida(a1, a2, a3, a4)
                Log.i("myapp", af.toString())
                DataManager.db.collection("people").document(document.id).get()
                    .addOnSuccessListener { result ->
                        val v1 = result.get("id").toString()
                        val v2 = result.get("comida").toString()
                        val v3 = result.get("tipo").toString()
                        //val v4 = result.get("tiempo").toString().toInt()
                        val v4 = 0
                        Log.i("myapp", "$v1,$v2,$v3,$v4")
                        val vuelve1 = Comida(v1, v2, v3, v4)
                        //listado.add(vuelve1)
                    }.addOnFailureListener { exception ->
                        Log.w("myapp", "Error getting documents.", exception)
                    }

                sb.append("$comidabajada")
            }
            for (document in 0..querySnapshot.documents.size) {
                val a1 = querySnapshot.documents[document].get("id").toString()
                val a2 = querySnapshot.documents[document].get("comida").toString()
                val a3 = querySnapshot.documents[document].get("tipo").toString()
                val a4 = querySnapshot.documents[document].get("tiempo").toString().toInt()
                val af = Comida(a1, a2, a3, a4)
                Log.i("myapp", af.toString())
                listado.add(af)
            }

        } catch (e: Exception) {
            val querySnapshot = comidaRef.get().await()
            val a = Comida("a", "a", "a", 1)
            //listado.add(a)
        }


        muestra = listado
    }

    fun bajaComida2() = CoroutineScope(Dispatchers.IO).launch {
        arrayDocs.clear()
        arrayCom.clear()
        /*db.collection("comida").get().addOnSuccessListener {result ->
            Log.i("myapp", result.toString())
            for (documento in 0..result.size()){
                val a = db.collection("comida").document().id
                arrayDocs.add(a)
            }
        }*/
        val querySnapshot = comidaRef.get().await()
        for (document in querySnapshot.documents) {
            val a = document.id
            Log.i("myapp", a)
            arrayDocs.add(a)
        }
        Log.i("myapp", arrayDocs.toString())
        Log.i("myapp", arrayDocs.size.toString())
        Log.i("myapp", arrayDocs[0].toString())
        Log.i("myapp", arrayDocs[1].toString())
        //bajaComida3()

    }

    fun bajaComida3() {
        for (i in 0 until arrayDocs.size) {
            DataManager.db.collection("comida").document(arrayDocs[i]).get()
                .addOnSuccessListener { result ->
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
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        //mAdapter.RecyclerAdapter(lista, this)
        mRecyclerView.adapter = Listado2Adapder(lista)
    }
}

