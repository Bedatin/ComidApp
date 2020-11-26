package com.example.comidapp.copiaEOI

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.comidapp.Comida
import com.example.comidapp.R


class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  val comida = itemView.findViewById<TextView>(R.id.tvComida)
  val delete = itemView.findViewById<ImageButton>(R.id.btnDelete)
  //val cardView = itemView.findViewById<CardView>(R.id.carditem)


  fun bindTask(item: Comida) {
    comida.text = item.comida
  }

}


