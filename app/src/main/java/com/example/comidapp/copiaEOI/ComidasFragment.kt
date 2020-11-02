package com.example.comidapp.copiaEOI


import android.content.Intent
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comidapp.Comida
import com.example.comidapp.R
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase


class ComidasFragment : NavigationChildFragment(), TaskListener {

    // Referencia a bbdd de firebase
    var mDatabase = FirebaseDatabase.getInstance().reference

    // Nombre de nuestra tabla/collection
    val taskTable = "tareas"

    // Intancia del adapter
    var mAdapter: FirebaseRecyclerAdapter<Comida, TaskViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comida, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Titulo del fragment
        //mainActivity.title = "Tareas por hacer"

        // Ocultar el botón de atras


        //mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Boton flotante
        val fabF = view.findViewById<FloatingActionButton>(R.id.fabF)
        fabF.setOnClickListener {
            //addNewTaskDialog()

            val intent = Intent(context, AddTaskActivity::class.java)
            startActivity(intent)

        }

        // Listado de tareas
        val taskList = view.findViewById<RecyclerView>(R.id.rvComidasF)
        taskList.layoutManager = LinearLayoutManager(context)

        // Loader
        //val progressBar = view.findViewById<ProgressBar>(R.id.progressbar)

        // Peticion a bbdd de firebase
        val query =
            mDatabase.child(taskTable).child(mAuth.currentUser?.uid.toString()).orderByChild("done").equalTo(false)
        val options = FirebaseRecyclerOptions.Builder<Comida>()
            .setQuery(query, Comida::class.java)
            .build()

        // Adapter de firebase
        mAdapter = object : FirebaseRecyclerAdapter<Comida, TaskViewHolder>(options) {

            override fun onDataChanged() {
                super.onDataChanged()

                // Yo tengo un progress bar, y aqui lo escondo porque hay datos :)
                //progressBar.visibility = View.GONE
            }

            override fun onChildChanged(type: ChangeEventType, snapshot: DataSnapshot, newIndex: Int, oldIndex: Int) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex)

                // Cada vez que cambia un dato o se añade, el scrollview crece,
                // asi que hago scroll del recycler a la ultima posicion
                taskList.scrollToPosition(newIndex)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
                val myView = LayoutInflater.from(parent.context).inflate(R.layout.item_comida, parent, false)
                return TaskViewHolder(myView)
            }

            // Esta funcion carga tantas veces como objetos tengas en firebase database
            override fun onBindViewHolder(holder: TaskViewHolder, position: Int, model: Comida) {
                holder.bindTask(model)
                // Cada vez que una tarea carge, le ponemos si esta done (realizada) o no
                //holder.check.isChecked = model.done!!

                holder.delete.setOnClickListener {
                    // Eliminar tarea
                    deleteTask(model.id!!)
                }

                /*holder.check.setOnClickListener {
                    // Marcar como tarea completada
                    completeTask(model.id!!, true)
                }*/

                holder.cardView.setOnClickListener {
                    // Abrir activity, cambiar fragemnt, lo que quieas

                    /*val detail = Intent(context, DetailActivity::class.java)
                    detail.putExtra("miTarea", model.id!!)
                    startActivity(detail)*/
                }
            }

        }

        taskList.adapter = mAdapter
    }



    override fun deleteTask(idTask: String) {
        mDatabase.child(taskTable).child(mAuth.currentUser?.uid.toString()).child(idTask).removeValue()
    }

    override fun completeTask(idTask: String, isDone: Boolean) {
        mDatabase.child(taskTable).child(mAuth.currentUser?.uid.toString()).child(idTask).child("done").setValue(isDone)
    }


    override fun onStart() {
        super.onStart()
        mAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter?.stopListening()
    }

}
