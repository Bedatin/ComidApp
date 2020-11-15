package com.example.comidapp


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapder2(var onDiaClick: (dia: Dia) -> Unit) :
    RecyclerView.Adapter<CalendarAdapder2.ViewHolder>() {

    var lista: List<Dia> = ArrayList()
    lateinit var context: Context
    var trampa = 50

    fun RecyclerAdapter(series: List<Dia>, context: Context, ancho: Int) {
        this.lista = series
        this.context = context
        this.trampa = ancho
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.bind(item, trampa, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_dia, parent, false))
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("WrongViewCast")
        val tvDia = view.findViewById(R.id.tvDia) as TextView
        val tvComida = view.findViewById(R.id.tvComida) as TextView
        val tvCena = view.findViewById(R.id.tvCena) as TextView
        val cuadro = view.findViewById(R.id.item_dia) as LinearLayout


        @SuppressLint("SetTextI18n")
        fun bind(listita: Dia, trampita: Int, item: Dia) {
            val num = listita.fecha
            val num1 = num.substring(8,10)
            val num2 = num.substring(5,7)
            tvDia.text = "${listita.dia} $num1/$num2"
            tvComida.text = listita.comida
            tvCena.text = listita.cena
            cuadro.layoutParams.width = trampita
            itemView.setOnClickListener {
                onDiaClick(item)
            }
        }
    }

}