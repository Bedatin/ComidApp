package com.example.comidapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.comidapp.DataManager.db
import com.example.comidapp.copiaEOI.AddTaskActivity
import com.example.comidapp.copiaEOI.TaskViewHolder
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirestoreRegistrar
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_listado.*

class Listado2 : Fragment() {

    val TAG = "myapp"
    lateinit var mRecyclerView: RecyclerView
    //val mAdapter: ComidasAdapter = ComidasAdapter()
    private lateinit var listaDias: ArrayList<Comida>
    var listita: ArrayList<Comida> = ArrayList()
    var listitaOrd: ArrayList<Comida> = ArrayList()
    var semana = arrayListOf<String>("L", "M", "X", "J", "V", "S", "D")
    var comidaLista: ArrayList<Comida> = ArrayList()

    var mDatabase = FirebaseDatabase.getInstance().reference
    var mDatabase2= FirebaseFirestore.getInstance()
    var mAdapter: FirebaseRecyclerAdapter<Comida, TaskViewHolder>? = null
    val taskTable = "Comida"
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listado, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listaDias = arrayListOf<Comida>()
        //NO FUFA



        setUpRecyclerView(listita)


        fab.setOnClickListener {
            val intent = Intent(context, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }


    @SuppressLint("WrongConstant")
    fun setUpRecyclerView(lista: ArrayList<Comida>) {
        mRecyclerView = rvComidas
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context, OrientationHelper.HORIZONTAL, false)
        //mAdapter.RecyclerAdapter(lista, this)
        mRecyclerView.adapter = Listado2Adapder(lista)
    }

}