package com.example.comidapp.toolbarActivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.comidapp.R
import kotlinx.android.synthetic.main.activity_dia_info.*
import kotlinx.android.synthetic.main.item_dia.tvCena
import kotlinx.android.synthetic.main.item_dia.tvComida
import kotlinx.android.synthetic.main.item_dia.tvDia

class DiaInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dia_info)

        val dia =intent.extras?.getSerializable("dia") as String
        val comida =intent.extras?.getSerializable("comida") as String
        val cena =intent.extras?.getSerializable("cena") as String

        tvDia.text = dia
        tvComida.text = comida
        tvCena.text = cena
        etComida.hint = comida
        etCena.hint = cena

    }
}
