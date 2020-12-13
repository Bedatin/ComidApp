package com.example.comidapp


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.comidapp.DataManager.db
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

val comidaRef = Firebase.firestore.collection("comida")

class ListadoAdapter(var onComidaClick: (comida: Comida) -> Unit) :
    RecyclerView.Adapter<ListadoAdapter.ViewHolder>() {

    var lista: ArrayList<Comida> = ArrayList()
    lateinit var context: Context
    //lateinit var context: Context

    fun RecyclerAdapter(series: ArrayList<Comida>, context: Context) {
        this.lista = series
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.bind(item, item)
        holder.btnDelete.setOnClickListener {
            db.collection("comida").document(item.id).delete()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_comida, parent, false))
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvComida = view.findViewById(R.id.tvComida) as TextView
        val tvTipo = view.findViewById(R.id.tvTipo) as TextView
        val tvTiempo = view.findViewById(R.id.tvTiempo) as TextView
        val btnDelete = view.findViewById(R.id.btnDelete) as Button
        val ivDupli = view.findViewById(R.id.ivDupli) as ImageView


        @SuppressLint("SetTextI18n")
        fun bind(listita: Comida, item:Comida) {
            tvComida.text = listita.comida
            tvTipo.text = listita.tipo
            when (listita.tiempo) {
                1 -> tvTiempo.text = "Comida"
                2 -> tvTiempo.text = "Cena"
                else -> tvTiempo.text = "Ambas"
            }
            itemView.setOnClickListener {
                onComidaClick(item)
            }
            btnDelete.setOnClickListener {
                //db.collection("comida").document(item.id).delete()
                comidaRef.document(item.id).delete()
            }
            if(listita.duplicable){
                ivDupli.setImageResource(R.drawable.ic_check)
            }
        }
    }

}