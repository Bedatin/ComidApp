package com.example.comidapp.copiaEOI

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.comidapp.MainActivity
import com.google.firebase.auth.FirebaseAuth


abstract class NavigationChildFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth
    lateinit var ListadoComida: MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        ListadoComida = activity as MainActivity
        ListadoComida.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }


}

