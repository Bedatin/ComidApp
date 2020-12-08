package com.example.comidapp.toolbarActivities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comidapp.*
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_dia_info.*
import kotlinx.android.synthetic.main.item_dia.tvCena
import kotlinx.android.synthetic.main.item_dia.tvComida
import kotlinx.android.synthetic.main.item_dia.tvDia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.reflect.Type

class DiaInfo : AppCompatActivity() {

    val diaRef = Firebase.firestore.collection("Dia")
    val comidaRef = Firebase.firestore.collection("comida")

    var calendarioNuevo: ArrayList<Dia> = arrayListOf()

    val arrayDocsCom = arrayListOf<String>()
    val arrayCom = arrayListOf<Comida>()
    var comistrajos: ArrayList<Comida> = ArrayList()

    var eleComida = 0

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: ListadoAdapter2


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dia_info)
        bajaComida()
        bajaShared2()

        val fecha = intent.extras?.getSerializable("fecha") as String
        val dia = intent.extras?.getSerializable("dia") as String
        val comida = intent.extras?.getSerializable("comida") as String
        val cena = intent.extras?.getSerializable("cena") as String


        val num1 = fecha.substring(8, 10)
        val num2 = fecha.substring(5, 7)
        val num3 = fecha.substring(0, 4)

        tvDia.text = "$dia $num1/$num2/$num3"
        tvComida.text = comida
        tvCena.text = cena
        etComida.hint = comida
        etCena.hint = cena

        btnEtComida.setOnClickListener {
            setUpRecyclerView(comistrajos)
            eleComida = 1
            btnEtComida.setBackgroundResource(R.color.colorAccent)
            btnEtCena.setBackgroundResource(R.drawable.btn_animado)
        }
        btnEtCena.setOnClickListener {
            setUpRecyclerView(comistrajos)
            eleComida = 2
            btnEtComida.setBackgroundResource(R.drawable.btn_animado)
            btnEtCena.setBackgroundResource(R.color.colorAccent)
        }

        buscador()
        btnComida.setOnClickListener {
            comistrajos.sortBy { it.comida.compareTo(comida, true) }
            setUpRecyclerView(comistrajos)
        }
        btnTipo.setOnClickListener {
            comistrajos.sortByDescending { it.comida.compareTo(comida, true) }
            setUpRecyclerView(comistrajos)
        }


        btnSubeCambio.setOnClickListener {
            val nuevaComida = if (etComida.text.isNotEmpty()) {
                etComida.text
            } else {
                tvComida.text
            }
            val nuevaCena = if (etCena.text.isNotEmpty()) {
                etCena.text
            } else {
                tvCena.text
            }
            val nuevo = Dia(fecha, dia, nuevaComida.toString(), nuevaCena.toString())
            //saveComida(nuevo)
            //calendario[orden.toInt()] = nuevo
            //enviaInfo()
            for (i in 0 until calendarioNuevo.size) {
                if (nuevo.fecha == calendarioNuevo[i].fecha) {
                    calendarioNuevo[i] = nuevo
                }
            }
            shareInfo(true)
            enviaInfo()
            /*val intent = Intent(this, CalendarActivity::class.java)
            intent.putExtra("cambios", true)
            startActivity(intent)*/
            finish()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val intent = Intent(this, CalendarActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun buscador() {
        searchV.setOnCloseListener {
            setUpRecyclerView(comistrajos)
            false
        }
        val queryTextLis = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(textoEscrito: String?): Boolean {
                //TODO Cuando se pulsa en el boton de buscar del teclado entra aqui
                if (textoEscrito != null) {
                    val muestraFiltro = comistrajos.filter {
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

                val muestraFiltro = comistrajos.filter {
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

    fun setUpRecyclerView(lista: ArrayList<Comida>) {
        mRecyclerView = rvComidas
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = ListadoAdapter2 {
            when (eleComida) {
                1 -> {
                    etComida.setText(it.comida)
                }
                2 -> {
                    etCena.setText(it.comida)
                }
                else -> {
                }
            }
        }
        mAdapter.RecyclerAdapter(lista, this)
        mRecyclerView.adapter = mAdapter
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
        comistrajos.clear()
        //muestra.clear()
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
            comistrajos = arrayCom
            //muestra = arrayCom
        } catch (e: Exception) {
        }
        Log.i("comida2", "baja $comistrajos")

    }

    fun bajaShared2() {
        //Shared
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)

        val gson2 = Gson()
        val json2: String = sharedPref.getString("listadoDiasNuevos", "")!!
        val type2: Type = object : TypeToken<List<Dia?>?>() {}.type
        val listaDias: List<Dia> = gson2.fromJson(json2, type2)
        calendarioNuevo.addAll(listaDias)
    }

    fun enviaInfo() {
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gson = Gson()
        val json2 = gson.toJson(calendarioNuevo)
        editor.apply {
            putString("listadoDiasNuevos", json2)
            apply()
        }
        val intent = Intent(this, CalendarActivity::class.java)
        intent.putExtra("cambios", true)
        startActivity(intent)
    }

    fun shareInfo(actualiza: Boolean) {
        //Shared
        val sharedPref= getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.apply {
            putBoolean("actu", actualiza)
            apply()
        }
    }

    fun saveComida(dia: Dia) = CoroutineScope(Dispatchers.IO).launch {
        try {
            diaRef.document(dia.fecha).set(dia)
        } catch (e: Exception) {
            Toast.makeText(this@DiaInfo, e.message, Toast.LENGTH_LONG).show()
        }
    }

}
