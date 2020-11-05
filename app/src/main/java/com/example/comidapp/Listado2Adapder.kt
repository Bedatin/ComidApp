package com.example.comidapp


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Listado2Adapder (var listaEntrada: ArrayList<Comida>): RecyclerView.Adapter<Listado2Adapder.ViewHolder>() {

    var lista: ArrayList<Comida> = listaEntrada
    lateinit var context: Context

    fun RecyclerAdapter(series: ArrayList<Comida>, context: Context) {
        this.lista = series
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_comida, parent, false))
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvComida = view.findViewById(R.id.tvComida) as TextView
        val tvTipo = view.findViewById(R.id.tvTipo) as TextView
        val tvTiempo = view.findViewById(R.id.tvTiempo) as TextView



        fun bind(listita: Comida, context: Context) {
            tvComida.text = listita.comida
            tvTipo.text = listita.tipo
            tvTiempo.text = listita.tiempo.toString()
        }
    }

    }