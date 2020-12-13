package com.example.comidapp.toolbarActivities


import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.comidapp.*
import com.example.comidapp.calendarAdapters.*
import com.example.comidapp.notifications.NotificationData
import com.example.comidapp.notifications.PushNotification
import com.example.comidapp.notifications.RetrofitInstance
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.reflect.Type
import java.time.LocalDate


const val TOPIC = "/topics/Maison"

@Suppress("DEPRECATION")
class CalendarActivity : AppCompatActivity() {

    val TAG = "myApp"

    //Reciclers
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: CalendarAdapder
    lateinit var mRecyclerView2: RecyclerView
    lateinit var mAdapter2: CalendarAdapder2
    lateinit var mRecyclerView3: RecyclerView
    lateinit var mAdapter3: CalendarAdapder3
    lateinit var mRecyclerView4: RecyclerView
    lateinit var mAdapter4: CalendarAdapder4
    lateinit var mRecyclerView5: RecyclerView
    lateinit var mAdapter5: CalendarAdapder5


    var listita: ArrayList<Dia> = ArrayList()
    var semana = arrayListOf<String>(
        "L",
        "M",
        "X",
        "J",
        "V",
        "S",
        "D"
    )

    var comidaLista: ArrayList<Comida> = ArrayList()

    var sem1: ArrayList<Dia> = arrayListOf()
    var sem2: ArrayList<Dia> = arrayListOf()
    var semanaza: ArrayList<Dia> = arrayListOf()


    //Generar comidas
    var calendario: ArrayList<Dia> = arrayListOf()
    var calendarioNuevo: ArrayList<Dia> = arrayListOf()
    var comistrajos: ArrayList<Comida> = ArrayList()

    var hoy = ""
    var semana1: ArrayList<Dia> = arrayListOf()
    var semana2: ArrayList<Dia> = arrayListOf()
    var semana3: ArrayList<Dia> = arrayListOf()
    var semana4: ArrayList<Dia> = arrayListOf()
    var semana5: ArrayList<Dia> = arrayListOf()


    //Trampas
    var trampa = 50
    var trampa2 = 50

    var muestra: ArrayList<Comida> = ArrayList()

    //Firebase
    val arrayDocsCom = arrayListOf<String>()
    val arrayCom = arrayListOf<Comida>()

    val arrayDocsDias = arrayListOf<String>()
    val arrayDias = arrayListOf<Dia>()

    val comidaRef = Firebase.firestore.collection("comida")
    val diaRef = Firebase.firestore.collection("Dia")


    var actu = false
    var actuDia = false

    //Notificaciones
    var usuario = "Alguien"
    var id = ""

    lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        //setSupportActionBar(toolbar)
        Log.i("comidas", semana1.toString())
        tvTitulo.typeface = Typeface.createFromAsset(assets, "fonts/CURSHT.TTF")

        bajaComida()
        trampa = windowManager.defaultDisplay.width / 4
        trampa2 = windowManager.defaultDisplay.width / 4

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //val newFragment = LoadFragment()
        //newFragment.show(supportFragmentManager, "Info")
        //bajaComida()
        //bajaDias()
        try {
            bajaShared2()

        } catch (e: Exception) {
        }
        Log.i("comidas", "OR: $calendario")
        Log.i("comidas", "NU: $calendarioNuevo")
        if (!actu) {
            separaSemanas(calendario)
            calendarioNuevo.clear()
            calendarioNuevo.addAll(calendario)
            Log.i("comidas", "vacio")
        } else {
            separaSemanas(calendarioNuevo)
            btnNoti.setBackgroundResource(R.color.colorAccent)
            Log.i("comidas", "lleno")
        }


        btnGen1.setOnClickListener {
            aleatorizaComida(semana1)
            aleatorizaComida(semana2)
            shareInfo(true)
            setUpRecyclerView(semana1, rvSem1)
            setUpRecyclerView(semana2, rvSem2)
            btnNoti.setBackgroundResource(R.color.colorAccent)
        }
        btnGen2.setOnClickListener {
            aleatorizaComida(semana3)
            aleatorizaComida(semana4)
            shareInfo(true)
            setUpRecyclerView(semana3, rvSem3)
            setUpRecyclerView(semana4, rvSem4)
            btnNoti.setBackgroundResource(R.color.colorAccent)
        }
        btnGen3.setOnClickListener {
            aleatorizaComida(semana5)
            shareInfo(true)
            setUpRecyclerView(semana5, rvSem5)
            btnNoti.setBackgroundResource(R.color.colorAccent)
        }

        btnLista.setOnClickListener {
            val intent = Intent(this, ListadoActivity::class.java)
            startActivity(intent)
        }
        btnReglas.setOnClickListener {
            val intent = Intent(this, ReglasActivity::class.java)
            startActivity(intent)
        }


        btnNoti.setOnClickListener {
            Log.i("nuevo", calendarioNuevo.size.toString())
            for (i in 0 until calendario.size) {
                if (calendarioNuevo.size != 0) {
                    var add = true
                    for (j in 0 until calendarioNuevo.size) {
                        if (calendario[i].fecha == calendarioNuevo[j].fecha) {
                            add = false
                        }
                    }
                    if (add) {
                        calendarioNuevo.add(calendario[i])
                    }
                } else {
                    calendarioNuevo.add(calendario[i])
                }
            }
            val newFragment = SureFragment(calendario, calendarioNuevo, "sure")
            newFragment.show(supportFragmentManager, "Info")
            //enviaActualizacion()
        }

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        if(actu){
            val newFragment = SureFragment(calendario, calendarioNuevo, "exit")
            newFragment.show(supportFragmentManager, "Info")
        }
        else{
            finish()
        }

    }

    //Reciclers
    @SuppressLint("WrongConstant")
    fun setUpRecyclerView(lista: List<Dia>, RV: RecyclerView) {
        mRecyclerView = RV
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        mAdapter = CalendarAdapder {
            shareInfo(true)
            val intent = Intent(this, DiaInfo::class.java)
            intent.putExtra("fecha", it.fecha)
            intent.putExtra("dia", it.dia)
            intent.putExtra("comida", it.comida)
            intent.putExtra("cena", it.cena)
            startActivity(intent)
            this.finish()
        }
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
            shareInfo(true)
            val intent = Intent(this, DiaInfo::class.java)
            intent.putExtra("fecha", it.fecha)
            intent.putExtra("dia", it.dia)
            intent.putExtra("comida", it.comida)
            intent.putExtra("cena", it.cena)
            startActivity(intent)
            this.finish()
        }
        mAdapter2.RecyclerAdapter(lista, this, trampa2)
        mRecyclerView2.adapter = mAdapter2
    }

    @SuppressLint("WrongConstant")
    fun setUpRecyclerView3(lista: List<Dia>, RV: RecyclerView) {
        mRecyclerView3 = RV
        mRecyclerView3.setHasFixedSize(true)
        mRecyclerView3.layoutManager =
            LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        mAdapter3 = CalendarAdapder3 {
            shareInfo(true)
            val intent = Intent(this, DiaInfo::class.java)
            intent.putExtra("fecha", it.fecha)
            intent.putExtra("dia", it.dia)
            intent.putExtra("comida", it.comida)
            intent.putExtra("cena", it.cena)
            startActivity(intent)
            this.finish()
        }
        mAdapter3.RecyclerAdapter(lista, this, trampa)
        mRecyclerView3.adapter = mAdapter3
    }

    @SuppressLint("WrongConstant")
    fun setUpRecyclerView4(lista: List<Dia>, RV: RecyclerView) {
        mRecyclerView4 = RV
        mRecyclerView4.setHasFixedSize(true)
        mRecyclerView4.layoutManager =
            LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        mAdapter4 = CalendarAdapder4 {
            shareInfo(true)
            val intent = Intent(this, DiaInfo::class.java)
            intent.putExtra("fecha", it.fecha)
            intent.putExtra("dia", it.dia)
            intent.putExtra("comida", it.comida)
            intent.putExtra("cena", it.cena)
            startActivity(intent)
            this.finish()
        }
        mAdapter4.RecyclerAdapter(lista, this, trampa2)
        mRecyclerView4.adapter = mAdapter4
    }

    @SuppressLint("WrongConstant")
    fun setUpRecyclerView5(lista: List<Dia>, RV: RecyclerView) {
        mRecyclerView5 = RV
        mRecyclerView5.setHasFixedSize(true)
        mRecyclerView5.layoutManager =
            LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        mAdapter5 = CalendarAdapder5 {
            shareInfo(true)
            val intent = Intent(this, DiaInfo::class.java)
            intent.putExtra("fecha", it.fecha)
            intent.putExtra("dia", it.dia)
            intent.putExtra("comida", it.comida)
            intent.putExtra("cena", it.cena)
            startActivity(intent)
            this.finish()
        }
        mAdapter5.RecyclerAdapter(lista, this, trampa)
        mRecyclerView5.adapter = mAdapter5
    }

    fun reciclar(q: List<Dia>, w: List<Dia>, e: List<Dia>, r: List<Dia>, t: List<Dia>) {
        setUpRecyclerView(q, rvSem1)
        setUpRecyclerView2(w, rvSem2)
        setUpRecyclerView3(e, rvSem3)
        setUpRecyclerView4(r, rvSem4)
        setUpRecyclerView5(t, rvSem5)
    }

    //Nuevo Generar comidas
    fun aleatorizaComida(cambio: ArrayList<Dia>) {
        val nuevo = arrayListOf<Dia>()
        for (i in 0 until cambio.size) {
            val a = (0 until comistrajos.size).random()
            var b = true
            var c: Int
            do {
                c = (0 until comistrajos.size).random()
                if (a != c) {
                    b = false
                }
            } while (b)
            val ar1 = Dia(
                cambio[i].fecha,
                cambio[i].dia,
                comistrajos[a].comida,
                comistrajos[c].comida
            )
            nuevo.add(ar1)
            //saveComida(ar1)
        }
        cambio.clear()
        cambio.addAll(nuevo)

        //Añado al array de cambios
        for (i in 0 until cambio.size) {
            if (calendarioNuevo.size != 0) {
                var add = true
                for (j in 0 until calendarioNuevo.size) {
                    if (cambio[i].fecha == calendarioNuevo[j].fecha) {
                        calendarioNuevo[j] = cambio[i]
                        add = false
                    }
                }
                if (add) {
                    calendarioNuevo.add(cambio[i])
                }
            } else {
                calendarioNuevo.add(cambio[i])
            }
        }
        actu = true
        btnNoti.setBackgroundResource(R.color.colorAccent)
    }

    fun aleatorizaComidaReglada(cambio: ArrayList<Dia>) {
        var ordenCambio = 0
        for (i in 0 until calendarioNuevo.size){
            if(calendarioNuevo[i].fecha == cambio[1].fecha){
                ordenCambio = i
            }
        }
        val nuevo = arrayListOf<Dia>()
        for (i in 0 until cambio.size) {
            val a = (0 until comistrajos.size).random()
            var b = true
            var c: Int
            do {
                c = (0 until comistrajos.size).random()
                if (a != c) {
                    b = false
                }
            } while (b)
            val ar1 = Dia(
                cambio[i].fecha,
                cambio[i].dia,
                comistrajos[a].comida,
                comistrajos[c].comida
            )
            nuevo.add(ar1)
            //saveComida(ar1)
        }
        cambio.clear()
        cambio.addAll(nuevo)

        //Añado al array de cambios
        for (i in 0 until cambio.size) {
            if (calendarioNuevo.size != 0) {
                var add = true
                for (j in 0 until calendarioNuevo.size) {
                    if (cambio[i].fecha == calendarioNuevo[j].fecha) {
                        calendarioNuevo[j] = cambio[i]
                        add = false
                    }
                }
                if (add) {
                    calendarioNuevo.add(cambio[i])
                }
            } else {
                calendarioNuevo.add(cambio[i])
            }
        }
        actu = true
        btnNoti.setBackgroundResource(R.color.colorAccent)
    }

    fun separaSemanas(origen: ArrayList<Dia>) {
        semana1.clear()
        semana2.clear()
        semana3.clear()
        semana4.clear()
        semana5.clear()
        for (i in 0..3) {
            semana1.add(origen[i])
        }
        for (i in 4..6) {
            semana2.add(origen[i])
        }
        for (i in 7..10) {
            semana3.add(origen[i])
        }
        for (i in 11..13) {
            semana4.add(origen[i])
        }
        for (i in 14..17) {
            semana5.add(origen[i])
        }
        setUpRecyclerView(semana1, rvSem1)
        setUpRecyclerView(semana2, rvSem2)
        setUpRecyclerView(semana3, rvSem3)
        setUpRecyclerView(semana4, rvSem4)
        setUpRecyclerView(semana5, rvSem5)
    }

    //Firebase
    fun saveComida(dia: Dia) = CoroutineScope(Dispatchers.IO).launch {
        try {
            diaRef.document(dia.fecha).set(dia)
        } catch (e: Exception) {
            Toast.makeText(this@CalendarActivity, e.message, Toast.LENGTH_LONG).show()
        }
    }

    //Notificaciones
    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d(TAG, "Response: ${Gson().toJson(response)}")
                } else {
                    Log.e(TAG, response.errorBody().toString())
                }
            } catch (e: java.lang.Exception) {
                Log.e(TAG, e.toString())
            }
        }

    fun enviaActualizacion() {
        val title = "Maison"
        try {
            bajaShared()
        } catch (e: Exception) {
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                DataManager.db.collection("people").document(id).get()
                    .addOnSuccessListener { result ->
                        usuario = result.get("name").toString()
                        Log.i(TAG, usuario)
                    }.await()
            } catch (e: Exception) {
                Log.i(TAG, "No pilla el nombre")
            }
            val message = "$usuario ha actualizado el calendario"
            PushNotification(
                NotificationData(title, message, "mensaje"),
                TOPIC
            ).also {
                sendNotification(it)
            }
        }
    }

    //Shared
    fun shareInfo(actualiza: Boolean) {
        //Shared
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gson = Gson()
        val json2 = gson.toJson(calendarioNuevo)
        editor.apply {
            putBoolean("actu", actualiza)
            putString("listadoDiasNuevos", json2)
            apply()
        }
    }

    fun bajaShared() {
        //Shared
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val emailId = sharedPref.getString("emailId", "")!!
        id = emailId
    }

    fun bajaShared2() {
        //Shared
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)

        val gson = Gson()
        val json: String = sharedPref.getString("listadoComida", "")!!
        val type: Type = object : TypeToken<List<Comida?>?>() {}.type
        val listaComidas: List<Comida> = gson.fromJson(json, type)
        //muestra.addAll(listaComidas)
        comistrajos.addAll(listaComidas)

        val gson2 = Gson()
        val json2: String = sharedPref.getString("listadoDias", "")!!
        val type2: Type = object : TypeToken<List<Dia?>?>() {}.type
        val listaDias: List<Dia> = gson2.fromJson(json2, type2)
        semanaza.addAll(listaDias)
        calendario.addAll(listaDias)

        val gson3 = Gson()
        val json3: String = sharedPref.getString("listadoDiasNuevos", "")!!
        val type3: Type = object : TypeToken<List<Dia?>?>() {}.type
        val listaDiasNuevos: List<Dia> = gson3.fromJson(json3, type3)
        calendarioNuevo.addAll(listaDiasNuevos)

        val hayCambios = sharedPref.getBoolean("actu", false)
        actu = hayCambios

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

    fun reglas(){

    }
}
