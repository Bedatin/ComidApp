package com.example.comidapp.toolbarActivities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comidapp.*
import com.example.comidapp.copiaEOI.AddTaskActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_listado.*
import kotlinx.android.synthetic.main.activity_listado.tvTitulo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.reflect.Type

class ListadoActivity : AppCompatActivity() {

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: ListadoAdapter

    var muestra: ArrayList<Comida> = ArrayList()

    val arrayDocs = arrayListOf<String>()
    val arrayCom = arrayListOf<Comida>()

    val comidaRef = Firebase.firestore.collection("comida")

    var comistrajos: ArrayList<Comida> = ArrayList()
    var comiditas: ArrayList<Comida> = ArrayList()


    //Notificaciones
    var usuario = "Alguien"
    var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado)
        //setSupportActionBar(toolbar)
        tvTitulo.typeface = Typeface.createFromAsset(assets, "fonts/CURSHT.TTF")
        //bajaComida2()

        if (comistrajos.size == 0) {
            Log.i("comidas", "vacio")
            btnRefrescar.isClickable = false
            bajaComida2()
        } else {
            Log.i("comidas", "lleno")
            setUpRecyclerView(comistrajos)
        }

        btnRefrescar.setOnClickListener {
            setUpRecyclerView(comistrajos)
        }

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        btnComida.setOnClickListener {
            comistrajos.sortBy { it.comida }
            setUpRecyclerView(comistrajos)
        }
        btnTipo.setOnClickListener {
            comistrajos.sortByDescending { it.comida }
            setUpRecyclerView(comistrajos)
        }
        buscador()
    }

    fun buscador() {
        searchV.setOnCloseListener {
            setUpRecyclerView(muestra)
            false
        }
        val queryTextLis = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(textoEscrito: String?): Boolean {
                //TODO Cuando se pulsa en el boton de buscar del teclado entra aqui
                if (textoEscrito != null) {
                    val muestraFiltro = muestra.filter {
                        it.comida.contains(textoEscrito, true) || it.tipo.contains(
                            textoEscrito,
                            true
                        )
                    } as ArrayList<Comida>
                    setUpRecyclerView(muestraFiltro)
                }
                return false
            }

            override fun onQueryTextChange(textoEscrito: String?): Boolean {
                //TODO Cada vez que escribimos una letra entra aqui

                val muestraFiltro = muestra.filter {
                    it.comida.contains(
                        textoEscrito.toString(),
                        true
                    ) || it.tipo.contains(textoEscrito.toString(), true)
                } as ArrayList<Comida>
                setUpRecyclerView(muestraFiltro)
                return false
            }
        } as SearchView.OnQueryTextListener
        searchV.setOnQueryTextListener(queryTextLis)
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

    fun bajaComida3() = CoroutineScope(Dispatchers.IO).launch {
        for (i in 0 until arrayDocs.size) {
            DataManager.db.collection("comida").document(arrayDocs[i]).get()
                .addOnSuccessListener { result ->
                    val a1 = result.get("id").toString()
                    val a2 = result.get("comida").toString()
                    val a3 = result.get("tipo").toString()
                    val a4 = result.get("tiempo").toString().toInt()
                    val a5 = result.get("duplicable").toString().toBoolean()
                    val af = Comida(a1, a2, a3, a4, a5)
                    Log.i("myapp", "$a1 $a2 $a3 $a4, ${af.id}")
                    arrayCom.add(af)
                }
        }
        muestra = arrayCom
        comistrajos = arrayCom
        btnRefrescar.isClickable = true
        //shareInfoComida()
    }

    fun setUpRecyclerView(lista: ArrayList<Comida>) {
        mRecyclerView = rvComidas
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = ListadoAdapter {
            val intent = Intent(this, ComidaInfo::class.java)
            intent.putExtra("comida", it.comida)
            intent.putExtra("tipo", it.tipo)
            intent.putExtra("tiempo", it.tiempo)
            startActivity(intent)
        }
        mAdapter.RecyclerAdapter(lista, this)
        mRecyclerView.adapter = mAdapter
    }

    //Shared
    fun bajaShared() {
        //Shared
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        var emailId = ""
        emailId = sharedPref.getString("emailId", "")!!
        id = emailId
    }

    fun bajaShared2() = CoroutineScope(Dispatchers.IO).launch {
        //Shared
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)

        val gson = Gson()
        val json: String = sharedPref.getString("listadoComida", "")!!
        val type: Type = object : TypeToken<List<Comida?>?>() {}.type
        val listaComidas: List<Comida> = gson.fromJson(json, type)
        //muestra.addAll(listaComidas)
        comistrajos.addAll(listaComidas)
        Log.i("comidas", "funcion $listaComidas")
        Log.i("comidas", "funcion $comistrajos")
    }


    fun shareInfoComida() {
        //Shared
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        sharedPref?.edit()?.remove("listadoComida")?.apply()
        val gson = Gson()
        val json2 = gson.toJson(comistrajos)
        editor.apply {
            putString("listadoComida", json2)
            apply()
        }
    }

    fun aPintar() {
        comiditas.clear()
        comiditas.addAll(comistrajos)
        setUpRecyclerView(comiditas)
    }

    fun aPintar2() {
        comiditas.clear()
        for (i in 0..3) {
            if (comistrajos.isNotEmpty()) {
                comiditas.add(comistrajos[i])
            }
        }
        for (i in 0..3) {
            if (comiditas.isNotEmpty()) {
                comiditas.remove(comiditas[0])
            }
        }
        comiditas.addAll(comistrajos)
        setUpRecyclerView(comiditas)
        Log.i("comidas", "tonteria")
    }
}
