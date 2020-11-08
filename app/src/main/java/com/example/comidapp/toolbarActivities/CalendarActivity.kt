package com.example.comidapp.toolbarActivities


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.comidapp.CalendarAdapder
import com.example.comidapp.Comida
import com.example.comidapp.Dia
import com.example.comidapp.R
import kotlinx.android.synthetic.main.activity_calendar.*
import java.util.*

class CalendarActivity : AppCompatActivity() {

    lateinit var mRecyclerView: RecyclerView
    val mAdapter: CalendarAdapder = CalendarAdapder()
    private lateinit var listaDias: ArrayList<Dia>
    var listita: ArrayList<Dia> = ArrayList()
    var semana = arrayListOf<String>("L", "M", "X", "J", "V", "S", "D")
    var comidaLista: ArrayList<Comida> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        setSupportActionBar(toolbar)

        getArticulos()
        listaDias = arrayListOf<Dia>()
        listaDias.addAll(listita)
        setUpRecyclerView(listaDias)
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
                val intent = Intent(this, ListadoActivity::class.java)
                startActivity(intent)
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

    fun getArticulos() {
        for (i in 0..6) {
            if (Calendar.DAY_OF_WEEK == i + 1) {
                val ar1 = Dia(
                    "hoy",
                    "pechuga",
                    "alitas"
                )
                listita.add(ar1)
            } else {
                val ar1 = Dia(
                    semana[i],
                    "pechuga",
                    "alitas"
                )
                listita.add(ar1)
            }
        }
    }

    @SuppressLint("WrongConstant")
    fun setUpRecyclerView(lista: List<Dia>) {
        mRecyclerView = rvSem1
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        mAdapter.RecyclerAdapter(lista, this)
        mRecyclerView.adapter = mAdapter
    }


}
