package com.example.comidapp.toolbarActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.comidapp.Dia
import com.example.comidapp.LoadActivity
import com.example.comidapp.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_sure.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SureFragment(var dias :ArrayList<Dia>) : DialogFragment() {

    val diaRef = Firebase.firestore.collection("Dia")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sure, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("myApp", dias.toString())
        btnSi.setOnClickListener {
            //activity?.finish()
            for (i in 0 until dias.size) {
                val diita =
                    Dia(dias[i].fecha, dias[i].dia, dias[i].comida, dias[i].cena)
                saveComida(diita)
            }
            dismiss()
            startActivity(Intent(context, LoadActivity::class.java))
            /*if (juego == "emparejar") {
                startActivity(Intent(context, EmparejarActivity::class.java))
                activity?.finish()
            }
            if (juego == "sumas") {
                startActivity(Intent(context, SumasActivity::class.java))
                activity?.finish()
            }
            if (juego == "memory") {
                startActivity(Intent(context, MemoryActivity::class.java))
                activity?.finish()
            }
            if (juego == "tres") {
                val intent = Intent(context, TresSolo::class.java)
                intent.putExtra("difi", tres)
                intent.putExtra("animal", animal)
                startActivity(intent)
                activity?.finish()
            }
            if (juego == "tresdos") {
                startActivity(Intent(context, TresDos::class.java))
                activity?.finish()
            }*/
        }

        btnNo.setOnClickListener {
            dismiss()
        }


    }

    fun saveComida(dia: Dia) = CoroutineScope(Dispatchers.IO).launch {
        try {
            diaRef.document(dia.fecha).set(dia)
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }


}