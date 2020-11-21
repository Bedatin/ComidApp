package com.example.comidapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.comidapp.R
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnCreateAccount: Button? = null
    private var name: String? = null
    private var email: String? = null
    private var password: String? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private var token: String = ""

    //Google Sign In
    val RC_SIGN_IN = 1234
    val TAG = "myApp"
    var gso: GoogleSignInOptions? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initialise()

        tvRegistro.text = "Inicia sesion"
        auth = FirebaseAuth.getInstance()

        btnDelete.setOnClickListener {
            if (auth.currentUser?.uid != null) {
                tvRegistro.text = "Inicia sesion"
            }
        }

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        callbackManager = CallbackManager.Factory.create()

        sign_in_button.setOnClickListener {
            signIn()
        }


    }

    fun shareInfo(email: String) {
        //Shared
        val sharedPref= getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        var emailId = ""
        editor.apply {
            putString("emailId", email)
            apply()
        }
    }


    private fun signIn() {
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso!!)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
                tvRegistro.text = "Sesion iniciada"
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.let {
                        for (profile in it.providerData) {
                            val name = profile.displayName
                            val email = profile.email
                            shareInfo(email!!)
                            FirebaseAuth.getInstance().setTenantId(email!!)
                            DataManager.createUser(name!!, email!!, token)
                        }
                    }
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    tvRegistro.text = "Sesion iniciada"
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }

                // ...
            }
    }


    private fun initialise() {
        notification()

        etEmail = findViewById<View>(R.id.et_email) as EditText
        etPassword = findViewById<View>(R.id.et_password) as EditText
        btnCreateAccount = findViewById<View>(R.id.btRegistrarme) as Button

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("people")
        mAuth = FirebaseAuth.getInstance()
        btnCreateAccount!!.setOnClickListener { createNewAccount() }
    }

    private fun createNewAccount() {
        name = et_name.text.toString()
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            FirebaseAuth.getInstance().setTenantId(email!!)

            //DataManager.createUser()

            mAuth!!.createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val userId = mAuth!!.currentUser!!.uid
                        Log.i(TAG, "$userId")
                        DataManager.createUser(name!!, email!!, token)
                        tvRegistro.text = "Sesion iniciada"
                        et_name.text.clear()
                        et_email.text.clear()
                        et_password.text.clear()
                        shareInfo(email!!)
                        //Verify Email
                        //verifyEmail()
                        //update user profile information
                        val currentUserDb = mDatabaseReference!!.child(userId)
                        //currentUserDb.child("firstName").setValue(firstName)
                        //currentUserDb.child("lastName").setValue(lastName)
                        //updateUserInfoAndUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }

    }

    fun notification() {
        /*FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            it.result?.token?.let {
                Log.i("fire", it)
            }
        }*/
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            token = it.token
        }
        FirebaseMessaging.getInstance().subscribeToTopic("Maison")
    }

}
