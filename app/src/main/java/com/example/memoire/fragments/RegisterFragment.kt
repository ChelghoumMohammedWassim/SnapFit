package com.example.memoire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.memoire.MainActivity2
import com.example.memoire.R

class RegisterFragment(private val context: MainActivity2) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_register, container, false)
        //injecter les element
        val registreButton: Button = view.findViewById(R.id.register_page_register_button)
        val connectButton: ImageView = view.findViewById(R.id.register_page_connect_button)
        val email = view.findViewById<EditText>(R.id.email_input)
        val password: EditText = view.findViewById(R.id.password_input)
        val conf_password = view.findViewById<EditText>(R.id.Confirme_password_input)
        //verifer le langeur de mote
        password.doOnTextChanged { text, start, before, count ->
            if (text!!.length < 6) {
                password.error = "Le mot de passe contient au moins 6 caractères"
            } else if (text.length >= 6) {
                password.error = null
            }
        }
        //cree un compte
        registreButton.setOnClickListener {
            if (password.text.toString() == conf_password.text.toString()){
                context.Register(email, password)
            } else{
                conf_password.error = "Verifier votre Mot de passe"
            }
        }
        //afficher le fragment de connection
        connectButton.setOnClickListener {
            context.loadLoginFragment()
        }
        return view
    }
}