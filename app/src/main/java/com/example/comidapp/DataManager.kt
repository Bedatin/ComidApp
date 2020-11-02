package com.example.comidapp

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object DataManager {

    val db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()
    val TAG = "myapp"
    //var auxiliar: Map<String, Any> = hashMapOf()
    var auxiliar: ArrayList<Int> = arrayListOf()
    var auxiliar3: Any? = null
    var auxiliar4: Any? = null
    var auxiliar5: Any? = null

    var auxiliar41: Any? = null

    var vuelveM = arrayListOf<Int>()

    //Funciones

    /*fun createUser(name:String?, email:String?) {
        val uid = auth.currentUser!!.uid
        val newUser = Usuario(name,email)
        db.collection("people").document(uid).set(newUser)

    }*/

    fun getUsers()  {
        var name = ""
        var mail = ""
        db.collection("people").get().addOnSuccessListener { result ->
            for (document in result) {
                Log.i(TAG, "${document.id} => ${document.data}")
                name= document.data.getValue("nombre").toString()
                mail= document.data.getValue("email").toString()
            }
        }.addOnFailureListener { exception ->
            Log.i(TAG, "Error getting documents.", exception)
        }
    }

    fun updateUser(name: String, valor: Int) {
        auxiliar.clear()
        //auxiliar2.clear()
        db.collection("people").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                auxiliar3 = result.get(name)
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        Log.i(TAG, "auxiliar antes $auxiliar")

        for (element in auxiliar3.toString().toCharArray()) {
            Log.i(TAG, "numeros $element")
            try {
                auxiliar.add(element.toString().toInt())
            } catch (e: Exception) {
                Log.i(TAG, "nope")
            } finally {
            }
        }

        auxiliar.add(valor)
        //auxiliar2.add(valor.toString())
        /*for (i in 0 until auxiliar.size) {
            //if (i%2 == 0){
            //auxiliar2.add(auxiliar[i])
            //}
        }*/
        Log.i(TAG, "auxiliar despues $auxiliar")
        //Log.i(TAG, "corto $auxiliar2")

        //db.collection("people").document(auth.currentUser!!.uid)

        //db.collection("people").document(auth.currentUser!!.uid).set(auxiliar)
        //db.collection("people").document(auth.currentUser!!.uid).update(name, FieldValue.delete())
        android.os.Handler().postDelayed({
            db.collection("people").document(auth.currentUser!!.uid).update(name, auxiliar)
        }, 1000)

    }

    fun updateUser2(name: String, valor: Int) {
        auxiliar.clear()
        db.collection("people").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                auxiliar4 = result.get(name)
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
        if (auxiliar4.toString() == "null") {
            db.collection("people").document(auth.currentUser!!.uid)
                .update(name, FieldValue.arrayUnion(valor))
        } else {
            for (element in auxiliar4.toString().toCharArray()) {
                try {
                    auxiliar.add(element.toString().toInt())
                } catch (e: Exception) {
                } finally {
                }
            }
            auxiliar.add(valor)
            db.collection("people").document(auth.currentUser!!.uid).update(name, auxiliar)
        }
    }

    fun updateUser3(name: String, valor: Int) {
        auxiliar.clear()
        db.collection("people").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                auxiliar5 = result.get(name)
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
        if (auxiliar5.toString() == "null") {
            db.collection("people").document(auth.currentUser!!.uid)
                .update(name, FieldValue.arrayUnion(valor))
        } else {
            for (element in auxiliar5.toString().toCharArray()) {
                try {
                    auxiliar.add(element.toString().toInt())
                } catch (e: Exception) {
                } finally {
                }
            }
            auxiliar.add(valor)

            db.collection("people").document(auth.currentUser!!.uid).update(name, auxiliar)

        }
    }


    fun subeMemo(valor1: Int, valor2: Int, valor3: Int, valor4: Int, valor5: Int, valor6: Int) {
        db.collection("people").document(auth.currentUser!!.uid).update("memo1", valor1)
        db.collection("people").document(auth.currentUser!!.uid).update("memo2", valor2)
        db.collection("people").document(auth.currentUser!!.uid).update("memo3", valor3)
        db.collection("people").document(auth.currentUser!!.uid).update("memo4", valor4)
        db.collection("people").document(auth.currentUser!!.uid).update("memo5", valor5)
        db.collection("people").document(auth.currentUser!!.uid).update("memo6", valor6)
        Log.i("baja", "subo $valor1,$valor2,$valor3,$valor4,$valor5,$valor6")
    }

    fun bajaMemo(): ArrayList<Int> {
        var vuelve1 = arrayListOf<Int>()
        lateinit var vuelve2: List<Int>
        db.collection("people").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                val v1 = result.get("memo1").toString().toInt()
                val v2 = result.get("memo2").toString().toInt()
                val v3 = result.get("memo3").toString().toInt()
                val v4 = result.get("memo4").toString().toInt()
                val v5 = result.get("memo5").toString().toInt()
                val v6 = result.get("memo6").toString().toInt()
                Log.i("baja", "$v1,$v2,$v3,$v4,$v5,$v6")
                vuelve1 = arrayListOf(v1, v2, v3, v4, v5, v6)
                vuelve2 = vuelve1.sortedDescending()
                vuelveM.clear()
                vuelveM.addAll(vuelve2)
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
        Log.i("baja", "mando a activity $vuelveM")

        return vuelveM
    }

    fun getDatosM(name: String): ArrayList<Int> {
        val puntos: ArrayList<Int> = arrayListOf(0, 0, 0, 0, 0, 0)
        var puntos2: ArrayList<Int> = arrayListOf()
        var puntos3: List<Int> = arrayListOf()
        db.collection("people").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                auxiliar41 = result.get(name)
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
        Log.i(TAG, auxiliar41.toString())
        if (auxiliar41.toString() == "null") {
            Log.i(TAG, auxiliar41.toString())
        } else {
            for (element in auxiliar41.toString().toCharArray()) {
                try {
                    puntos.add(element.toString().toInt())
                } catch (e: Exception) {
                } finally {
                }
            }
        }
        puntos3 = puntos.sortedDescending()
        for (i in 0..5) {
            puntos2.add((puntos3[i]))
        }
        return puntos2
    }

    fun deleteUser() {
        var uid = auth.currentUser!!.uid
        uid = ""
        db.collection("people").document(auth.currentUser!!.uid)
            .update("sumas", FieldValue.delete())
    }

}