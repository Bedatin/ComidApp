package com.example.comidapp.toolbarActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.comidapp.R
import kotlinx.android.synthetic.main.activity_reglas.*

class ReglasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reglas)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        /*val searchView = menu?.findItem(R.id.app_bar_search)?.actionView as SearchView
        searchView.setOnCloseListener {

            setUpRecyclerView(muestra)
            false
        }
        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(textoEscrito: String?): Boolean {
                //TODO Cuando se pulsa en el boton de buscar del teclado entra aqui
                if (textoEscrito != null) {
                    val muestraFiltro = muestra.filter { it.comida.contains(textoEscrito, true) || it.tipo.contains(textoEscrito, true)} as ArrayList<Comida>
                    setUpRecyclerView(muestraFiltro)
                }
                return false
            }

            override fun onQueryTextChange(textoEscrito: String?): Boolean {
                //TODO Cada vez que escribimos una letra entra aqui

                val muestraFiltro = muestra.filter { it.comida.contains(textoEscrito.toString(), true) || it.tipo.contains(textoEscrito.toString(), true)} as ArrayList<Comida>
                setUpRecyclerView(muestraFiltro)
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

}
