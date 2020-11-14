package com.example.comidapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_calendario.*
import java.util.*
import kotlin.collections.ArrayList

class Calendario : Fragment() {

    val TAG = "myapp"
    lateinit var mRecyclerView: RecyclerView
    val mAdapter: CalendarAdapder = CalendarAdapder()
    private lateinit var listaDias: ArrayList<Dia>
    var listita: ArrayList<Dia> = ArrayList()
    var semana= arrayListOf<String>("L","M","X","J","V","S","D")
    var comidaLista: ArrayList<Comida> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArticulos()
        listaDias = arrayListOf<Dia>()
        listaDias.addAll(listita)
        setUpRecyclerView(listaDias)
    }

    fun getArticulos() {
        for (i in 0..6){
            if(Calendar.DAY_OF_WEEK == i+1){
                val ar1 = Dia(
                    "",
                    "hoy",
                    "pechuga",
                    "alitas"
                )
                listita.add(ar1)
            }
            else {
                val ar1 = Dia(
                    "",
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
        mRecyclerView.layoutManager = LinearLayoutManager(context, OrientationHelper.HORIZONTAL, false)
        mAdapter.RecyclerAdapter(lista, context!!, 2)
        mRecyclerView.adapter = mAdapter
    }

    fun actuComida(){
        val nuevaComida = Comida()
        comidaLista.add(nuevaComida)
    }

}