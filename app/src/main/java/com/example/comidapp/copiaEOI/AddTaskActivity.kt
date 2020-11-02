package com.example.comidapp.copiaEOI

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.comidapp.Comida
import com.example.comidapp.DataManager.db
import com.example.comidapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_task.*


class AddTaskActivity : AppCompatActivity() {

    // Referencia a bbdd de firebase
    var mDatabase = FirebaseDatabase.getInstance().reference

    // Nombre de nuestra tabla/collection
    val taskTable = "comida"

    lateinit var mAuth: FirebaseAuth

    private var fileUri: Uri? = null
    private var imageReference: StorageReference? = null

    private val TAG = "StorageActivity"
    private val CHOOSING_IMAGE_REQUEST = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Instancia el Toolbar
        //setSupportActionBar(miToolbar)

        mAuth = FirebaseAuth.getInstance()

        imageReference = FirebaseStorage.getInstance().reference.child("images")



        btnAddtask.setOnClickListener {
            createTaskInFirebase()
        }

    }

    // Crea nuestra tarea en firebase
    private fun createTaskInFirebase() {

        val item = Comida()
        item.id = etComida.text.toString() + etTipo.text.toString() + etTiempo.text.toString()
        item.comida = etComida.text.toString()
        item.tipo = etTipo.text.toString()
        item.tiempo = etTiempo.text.toString().toInt()


        // Creamos una tarea en la tabla y nos dará una id única
        //val newItem = mDatabase.child(taskTable).child(mAuth.currentUser?.uid.toString()).push()


        // Y finalizando... usamos la referencia para asignar valor a esa id
        //newItem.setValue(item)
        /*db.collection("comida").add({
            item.id!!
        })*/
        db.collection("Comida").document(item.id!!).set(item)
            //db.collection("comida").document(item.id!!).update("Comida", item.comida)
            //db.collection("comida").document(item.id!!).update("Tipo", item.tipo)
            //db.collection("comida").document(item.id!!).update("Tiempo", item.tiempo)
            // Cierra la activity
            this.finish()
        }
    }


