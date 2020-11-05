package com.example.comidapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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

class Listado : Fragment() {

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
        listaDias.addAll(listita)
        //setUpRecyclerView(listaDias)

        val taskList = view.findViewById<RecyclerView>(R.id.rvComidas)
        taskList.layoutManager = LinearLayoutManager(context)

        //val progressBar = view.findViewById<ProgressBar>(R.id.progressbar)

        val query =
            mDatabase.child(taskTable).orderByKey()
        //.child(mAuth.currentUser?.uid.toString()).orderByChild("done").equalTo(false)
        val options = FirebaseRecyclerOptions.Builder<Comida>()
            .setQuery(query, Comida::class.java)
            .build()


        // Adapter de firebase
        mAdapter = object : FirebaseRecyclerAdapter<Comida, TaskViewHolder>(options) {

            override fun onDataChanged() {
                super.onDataChanged()

                // Yo tengo un progress bar, y aqui lo escondo porque hay datos :)
               // progressBar.visibility = View.GONE
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

                holder.delete.setOnClickListener {
                    // Eliminar tarea
                    //deleteTask(model.id!!)
                }


                holder.cardView.setOnClickListener {
                    // Abrir activity, cambiar fragemnt, lo que quieas

                    /*val detail = Intent(this, DetailActivity::class.java)
                    detail.putExtra("miTarea", model.id!!)
                    startActivity(detail)*/
                }
            }

        }

        taskList.adapter = mAdapter

        fab.setOnClickListener {
            val intent = Intent(context, AddTaskActivity::class.java)
            startActivity(intent)
        }
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