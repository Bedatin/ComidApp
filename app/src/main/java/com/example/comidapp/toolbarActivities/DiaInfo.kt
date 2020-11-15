package com.example.comidapp.toolbarActivities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.comidapp.Dia
import com.example.comidapp.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_dia_info.*
import kotlinx.android.synthetic.main.item_dia.tvCena
import kotlinx.android.synthetic.main.item_dia.tvComida
import kotlinx.android.synthetic.main.item_dia.tvDia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaInfo : AppCompatActivity() {

    val diaRef = Firebase.firestore.collection("Dia")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dia_info)

        val fecha = intent.extras?.getSerializable("fecha") as String
        val dia = intent.extras?.getSerializable("dia") as String
        val comida = intent.extras?.getSerializable("comida") as String
        val cena = intent.extras?.getSerializable("cena") as String

        val num1 = fecha.substring(8, 10)
        val num2 = fecha.substring(5, 7)
        val num3 = fecha.substring(0, 4)

        tvDia.text = "$dia $num1/$num2/$num3"
        tvComida.text = comida
        tvCena.text = cena
        etComida.hint = comida
        etCena.hint = cena

        btnSubeCambio.setOnClickListener {
            val nuevaComida = if (etComida.text.isNotEmpty()) {
                etComida.text
            }else{
                tvComida.text
            }
            val nuevaCena = if (etCena.text.isNotEmpty()) {
                etCena.text
            }else{
                tvCena.text
            }
            val nuevo = Dia(fecha,dia,nuevaComida.toString(), nuevaCena.toString())
            saveComida(nuevo)
            this.finish()
        }
    }

    fun saveComida(dia: Dia) = CoroutineScope(Dispatchers.IO).launch {
        try {
            diaRef.document(dia.fecha).set(dia)
        } catch (e: Exception) {
            Toast.makeText(this@DiaInfo, e.message, Toast.LENGTH_LONG).show()
        }
    }

}
