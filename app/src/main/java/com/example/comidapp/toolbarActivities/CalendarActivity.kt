package com.example.comidapp.toolbarActivities


import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.LocaleData
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.comidapp.*
import com.google.api.SystemParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


@Suppress("DEPRECATION")
class CalendarActivity : AppCompatActivity() {

    lateinit var mRecyclerView: RecyclerView
    val mAdapter: CalendarAdapder = CalendarAdapder()
    lateinit var mRecyclerView2: RecyclerView
    lateinit var mAdapter2: CalendarAdapder2
    var listita: ArrayList<Dia> = ArrayList()
    var semana = arrayListOf<String>(
        "Lunes",
        "Martes",
        "Miércoles",
        "Jueves",
        "Viernes",
        "Sábado",
        "Domingo"
    )
    var semanaInglesa = arrayListOf<String>(
        "MONDAY",
        "TUESDAY",
        "WEDNESDAY",
        "THURSDAY",
        "FRIDAY",
        "SATURDAY",
        "SUNDAY"
    )
    var comidaLista: ArrayList<Comida> = ArrayList()

    var sem1: ArrayList<Dia> = arrayListOf()
    var sem2: ArrayList<Dia> = arrayListOf()
    var semanaza: ArrayList<Dia> = arrayListOf()

    var trampa = 50
    var trampa2 = 50

    var muestra: ArrayList<Comida> = ArrayList()

    val arrayDocs = arrayListOf<String>()
    val arrayCom = arrayListOf<Comida>()

    val arrayDocsDias = arrayListOf<String>()
    val arrayDias = arrayListOf<Dia>()

    val comidaRef = Firebase.firestore.collection("comida")
    val diaRef = Firebase.firestore.collection("Dia")


    var actu = false
    var actuDia = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        setSupportActionBar(toolbar)

        trampa = windowManager.defaultDisplay.width / 4
        trampa2 = windowManager.defaultDisplay.width / 4

        bajaDias()
        bajaComida2()
        Handler().postDelayed({
            color.setBackgroundResource(R.color.amarillo)
        }, 4000)

        btnActu.setOnClickListener {
            if (!actu) {
                setUpRecyclerView(sem1, rvSem2)
                setUpRecyclerView2(sem2, rvSem3)
                actu = true
            } else {
                actu = false
                bajaComida2()
                bajaDias()
                /*val intent = Intent(this, Main2Activity::class.java)
                startActivity(intent)*/
            }
        }

        btnGen1.setOnClickListener {
            llenaArray()
            for (i in 0 until listita.size) {
                val diita =
                    Dia(listita[i].fecha, listita[i].dia, listita[i].comida, listita[i].cena)
                saveComida(diita)
            }
        }

        color.setOnClickListener {
            try {
                Log.i("dias", sem1[0].dia + sem1[1].dia + sem1[2].dia + sem1[3].dia)
            } catch (e: Exception) {

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        val searchView = menu?.findItem(R.id.app_bar_search)?.actionView as SearchView
        searchView.setOnCloseListener {

            //setUpRecyclerView(series)
            false
        }
        /*val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(textoEscrito: String?): Boolean {
                //TODO Cuando se pulsa en el boton de buscar del teclado entra aqui
                if (textoEscrito != null) {
                    seriesFiltradas = seriesFiltradas.filter { it.title.contains(textoEscrito, true) || it.year.toString().contains(textoEscrito, true)} as ArrayList<Series>
                    setUpRecyclerView(seriesFiltradas)
                }
                return false
            }

            override fun onQueryTextChange(textoEscrito: String?): Boolean {
                //TODO Cada vez que escribimos una letra entra aqui
                Log.i(TAG, "onQueryTextChange $textoEscrito")

                seriesFiltradas = seriesFiltradas.filter { it.title.contains(textoEscrito!!, true) || it.year.toString().contains(textoEscrito, true)} as ArrayList<Series>
                setUpRecyclerView(seriesFiltradas)
                return false
            }
        }
        searchView.setOnQueryTextListener(queryTextListener)*/
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_add -> {
                sem1.addAll(arrayDias)
                Log.i("dias", sem1[0].dia + sem1[1].dia + sem1[2].dia + sem1[3].dia)
                setUpRecyclerView(sem1, rvSem2)
                setUpRecyclerView2(sem2, rvSem3)
            }
            R.id.calendario -> {
                val intent = Intent(this, CalendarActivity::class.java)
                startActivity(intent)
            }
            R.id.listado -> {
                val intent = Intent(this, ListadoActivity::class.java)
                startActivity(intent)
            }
            R.id.reglas -> {
                val intent = Intent(this, ReglasActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item!!)
    }

    @SuppressLint("WrongConstant")
    fun setUpRecyclerView(lista: List<Dia>, RV: RecyclerView) {
        mRecyclerView = RV
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        mAdapter.RecyclerAdapter(lista, this, trampa)
        mRecyclerView.adapter = mAdapter
    }

    @SuppressLint("WrongConstant")
    fun setUpRecyclerView2(lista: List<Dia>, RV: RecyclerView) {
        mRecyclerView2 = RV
        mRecyclerView2.setHasFixedSize(true)
        mRecyclerView2.layoutManager =
            LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        mAdapter2 = CalendarAdapder2 {
            val intent = Intent(this, DiaInfo::class.java)
            intent.putExtra("dia", it.dia)
            intent.putExtra("comida", it.comida)
            intent.putExtra("cena", it.cena)
            startActivity(intent)
        }
        mAdapter2.RecyclerAdapter(lista, this, trampa2)
        mRecyclerView2.adapter = mAdapter2
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
        muestra.clear()
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

    fun bajaDias() = CoroutineScope(Dispatchers.IO).launch {
        arrayDocsDias.clear()
        arrayDias.clear()
        val querySnapshot = diaRef.get().await()
        for (document in querySnapshot.documents) {
            val a = document.id
            Log.i("dias", a)
            arrayDocsDias.add(a)
        }
        bajaDias2()
    }

    fun bajaDias2() {
        sem1.clear()
        sem2.clear()
        for (i in 0 until arrayDocsDias.size) {
            DataManager.db.collection("Dia").document(arrayDocsDias[i]).get()
                .addOnSuccessListener { result ->
                    val a1 = result.get("fecha").toString()
                    val a2 = result.get("dia").toString()
                    val a3 = result.get("comida").toString()
                    val a4 = result.get("cena").toString()
                    val af = Dia(a1, a2, a3, a4)
                    arrayDias.add(af)
                }

        }
        arrayDias.sortBy { it.fecha }
        sem1.addAll(arrayDias)
        /*for (i in 0..3) {
            sem1.add(arrayDias[i])
            Log.i("dias", sem1[i].toString())
        }
        for (i in 4..6) {
            sem2.add(arrayDias[i])
        }*/
    }

    fun getArticulos() {
        listita.clear()
        for (i in 0..6) {
            val a = (0 until muestra.size).random()
            var b = true
            var c: Int
            do {
                c = (0 until muestra.size).random()
                if (a != c) {
                    b = false
                }
            } while (b)
            val ar1 = Dia(
                LocalDate.now().plusDays((1 + i - 6).toLong()).toString(),
                semana[i],
                muestra[a].comida,
                muestra[c].comida
            )
            listita.add(ar1)
        }
    }

    fun llenaArray() {
        sem1.clear()
        sem2.clear()
        getArticulos()
        sem1.add(listita[0])
        sem1.add(listita[1])
        sem1.add(listita[2])
        sem1.add(listita[3])
        sem2.add(listita[4])
        sem2.add(listita[5])
        sem2.add(listita[6])
    }

    fun saveComida(dia: Dia) = CoroutineScope(Dispatchers.IO).launch {
        try {
            diaRef.document(dia.dia).set(dia)
        } catch (e: Exception) {
            Toast.makeText(this@CalendarActivity, e.message, Toast.LENGTH_LONG).show()
        }
    }

}
