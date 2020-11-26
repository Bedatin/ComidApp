package com.example.comidapp.toolbarActivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.comidapp.R
import kotlinx.android.synthetic.main.activity_comida_info.*

class ComidaInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comida_info)

        val comida = intent.extras?.getSerializable("comida") as String
        val tipo = intent.extras?.getSerializable("tipo") as String
        val tiempo = intent.extras?.getSerializable("tiempo") as Int

        tvComida.text = comida
        tvTipo.text = tipo
        when (tiempo) {
            1 -> tvTiempo.text = "Comida"
            2 -> tvTiempo.text = "Cena"
            else -> tvTiempo.text = "Ambas"
        }

        etComida.hint = comida
        etTipo.hint = tipo
        when (tiempo) {
            1 -> etTiempo.hint = "Comida"
            2 -> etTiempo.hint = "Cena"
            else -> etTiempo.hint = "Ambas"
        }
    }
}
