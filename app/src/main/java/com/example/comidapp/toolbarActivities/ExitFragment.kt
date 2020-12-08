package com.example.comidapp.toolbarActivities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import kotlinx.android.synthetic.main.fragment_exit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ExitFragment(var dias :ArrayList<Dia>) : DialogFragment() {

    val diaRef = Firebase.firestore.collection("Dia")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exit, container, false)
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
            val sharedPref = activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
            sharedPref?.edit()?.remove("listadoDias")?.apply()
            dismiss()
            val intent = Intent(context, LoadActivity::class.java)
            intent.putExtra("notificationS", true)
            startActivity(intent)
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