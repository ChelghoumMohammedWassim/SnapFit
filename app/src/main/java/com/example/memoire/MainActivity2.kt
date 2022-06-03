package com.example.memoire

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memoire.fragments.LoginFragment
import com.example.memoire.fragments.RegisterFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity2 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        auth = FirebaseAuth.getInstance()
        //verifer si il y a deja un compte active
        auth = Firebase.auth
        val current_user = auth.currentUser
        if (current_user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        //injecter le fragment de connection
        loadLoginFragment()
    }

    fun SignIn(email: EditText, password: EditText) {
        if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
            //connecter
            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser!!.email.toString()
                        Toast.makeText(
                            this,
                            "Content de te revoir : ${currentUser}",
                            Toast.LENGTH_SHORT
                        ).show()
                        //lancer le main
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener { exeption ->
                    Toast.makeText(this, exeption.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Entrez vos informations", Toast.LENGTH_SHORT).show()
        }
    }

    fun Register(email: EditText, password: EditText) {
        if (email.text.trim().isNotEmpty() && password.text.trim().length > 5) {
            //cree un compte
            auth.createUserWithEmailAndPassword(
                email.text.toString().trim(),
                password.text.toString().trim()
            ).addOnCompleteListener { task ->
                //lancer le main
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener { exeption ->
                //en cas de problem
                Toast.makeText(this, exeption.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Entrez vos informations", Toast.LENGTH_SHORT).show()
        }
        //virifier la langeur de mote de passe
        if (password.text.trim().length < 6 && password.text.trim().isNotEmpty()) {
            Toast.makeText(this, "Mot de passe contient au moin 6 caractère", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun loadRegisterFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.login_signup_fragment, RegisterFragment(this))
        transaction.commit()
    }

    fun loadLoginFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.login_signup_fragment, LoginFragment(this))
        transaction.commit()
    }
}

