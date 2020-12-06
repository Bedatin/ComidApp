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
            Log.i("comida2", comistrajos.toString())
        } catch (e: Exception) {
        }
        try {
            actu = intent.getBooleanExtra("cambios", false)
            if (actu) {
                btnNoti.setBackgroundResource(R.color.colorAccent)
            }
        } catch (e: Exception) {
        }

        separaSemanas()

        btnGen1.setOnClickListener {
            aleatorizaComida(semana1)
            aleatorizaComida(semana2)

            setUpRecyclerView(semana1, rvSem1)
            setUpRecyclerView2(semana2, rvSem2)
        }
        btnGen2.setOnClickListener {
            aleatorizaComida(semana3)
            aleatorizaComida(semana4)

            setUpRecyclerView3(semana3, rvSem3)
            setUpRecyclerView4(semana4, rvSem4)
        }
        btnGen3.setOnClickListener {
            aleatorizaComida(semana5)

            setUpRecyclerView5(semana5, rvSem5)
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
            /*calendarioNuevo.clear()
            calendarioNuevo.addAll(semana1)
            calendarioNuevo.addAll(semana2)
            calendarioNuevo.addAll(semana3)
            calendarioNuevo.addAll(semana4)
            calendarioNuevo.addAll(semana5)*/
            val newFragment = SureFragment(calendarioNuevo)
            newFragment.show(supportFragmentManager, "Info")
            //enviaActualizacion()
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

    @SuppressLint("ResourceType")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_add -> {
                llenaArray2()
                /*sem1.addAll(arrayDias)
                Log.i("dias", sem1[0].dia + sem1[1].dia + sem1[2].dia + sem1[3].dia)*/
                setUpRecyclerView(sem1, rvSem1)
                setUpRecyclerView2(sem2, rvSem2)
                setUpRecyclerView3(sem1, rvSem3)
                setUpRecyclerView4(sem2, rvSem4)
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

    //Reciclers
    @SuppressLint("WrongConstant")
    fun setUpRecyclerView(lista: List<Dia>, RV: RecyclerView) {
        mRecyclerView = RV
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        mAdapter = CalendarAdapder {
            val intent = Intent(this, DiaInfo::class.java)
            intent.putExtra("fecha", it.fecha)
            intent.putExtra("dia", it.dia)
            intent.putExtra("comida", it.comida)
            intent.putExtra("cena", it.cena)
            intent.putExtra("orden", calendario.indexOf(it).toString())
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
            val intent = Intent(this, DiaInfo::class.java)
            intent.putExtra("fecha", it.fecha)
            intent.putExtra("dia", it.dia)
            intent.putExtra("comida", it.comida)
            intent.putExtra("cena", it.cena)
            intent.putExtra("orden", calendario.indexOf(it).toString())
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
            val intent = Intent(this, DiaInfo::class.java)
            intent.putExtra("fecha", it.fecha)
            intent.putExtra("dia", it.dia)
            intent.putExtra("comida", it.comida)
            intent.putExtra("cena", it.cena)
            intent.putExtra("orden", calendario.indexOf(it).toString())
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
            val intent = Intent(this, DiaInfo::class.java)
            intent.putExtra("fecha", it.fecha)
            intent.putExtra("dia", it.dia)
            intent.putExtra("comida", it.comida)
            intent.putExtra("cena", it.cena)
            intent.putExtra("orden", calendario.indexOf(it).toString())
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
            val intent = Intent(this, DiaInfo::class.java)
            intent.putExtra("fecha", it.fecha)
            intent.putExtra("dia", it.dia)
            intent.putExtra("comida", it.comida)
            intent.putExtra("cena", it.cena)
            intent.putExtra("orden", calendario.indexOf(it).toString())
            startActivity(intent)
            this.finish()
        }
        mAdapter5.RecyclerAdapter(lista, this, trampa)
        mRecyclerView5.adapter = mAdapter5
    }

    //Generar comidas
    fun generaComida() {
        listita.clear()
        for (i in 0..6) {
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
                LocalDate.now().plusDays((i - 6).toLong()).toString(),
                semana[i],
                comistrajos[a].comida,
                comistrajos[c].comida
            )
            listita.add(ar1)
        }
    }

    fun llenaArray() {
        sem1.clear()
        sem2.clear()
        generaComida()
        sem1.add(listita[0])
        sem1.add(listita[1])
        sem1.add(listita[2])
        sem1.add(listita[3])
        sem2.add(listita[4])
        sem2.add(listita[5])
        sem2.add(listita[6])
        for (i in 0 until listita.size) {
            val diita =
                Dia(listita[i].fecha, listita[i].dia, listita[i].comida, listita[i].cena)
            //saveComida(diita)
        }
        val newFragment = SureFragment(listita)
        newFragment.show(supportFragmentManager, "Info")
        //setUpRecyclerView(sem1, rvSem1)
        //setUpRecyclerView2(sem2, rvSem2)
    }

    fun llenaArray2() {
        sem1.clear()
        sem2.clear()
        if (semanaza.size != 0) {
            Log.i("dias", semanaza.toString())
            sem1.add(semanaza[0])
            sem1.add(semanaza[1])
            sem1.add(semanaza[2])
            sem1.add(semanaza[3])
            sem2.add(semanaza[4])
            sem2.add(semanaza[5])
            sem2.add(semanaza[6])

        }
        if (comistrajos.size != 0) {
            Log.i("dias", comistrajos.toString())
        }
        //setUpRecyclerView(sem1, rvSem1)
        //setUpRecyclerView2(sem2, rvSem2)

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

    fun separaSemanas() {
        semana1.clear()
        semana2.clear()
        semana3.clear()
        semana4.clear()
        semana5.clear()
        for (i in 0..3) {
            semana1.add(calendario[i])
        }
        for (i in 4..6) {
            semana2.add(calendario[i])
        }
        for (i in 7..10) {
            semana3.add(calendario[i])
        }
        for (i in 11..13) {
            semana4.add(calendario[i])
        }
        for (i in 14..17) {
            semana5.add(calendario[i])
        }
        setUpRecyclerView(semana1, rvSem1)
        setUpRecyclerView2(semana2, rvSem2)
        setUpRecyclerView3(semana3, rvSem3)
        setUpRecyclerView4(semana4, rvSem4)
        setUpRecyclerView5(semana5, rvSem5)

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
    fun bajaShared() {
        //Shared
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        var emailId = ""
        emailId = sharedPref.getString("emailId", "")!!
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
        llenaArray2()
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
}
