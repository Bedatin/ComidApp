package com.example.comidapp


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapder: RecyclerView.Adapter<CalendarAdapder.ViewHolder>() {

    var lista: List<Dia> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(series: List<Dia>, context: Context) {
        this.lista = series
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_dia, parent, false))
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("WrongViewCast")
        val tvDia = view.findViewById(R.id.tvDia) as TextView
        val tvComida = view.findViewById(R.id.tvComida) as TextView
        val tvCena = view.findViewById(R.id.tvCena) as TextView



        fun bind(listita: Dia, context: Context) {
            tvDia.text = listita.dia
            tvComida.text = listita.comida
            tvCena.text = listita.cena
        }
    }

    }