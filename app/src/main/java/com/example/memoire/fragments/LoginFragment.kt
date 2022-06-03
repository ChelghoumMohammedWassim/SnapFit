package com.example.memoire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.memoire.MainActivity2

import com.example.memoire.R

class LoginFragment(private val context :MainActivity2):Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater?.inflate(R.layout.fragment_login,container,false)
        //identifier les objets
        val connectButton: Button = view.findViewById(R.id.connect_button)
        val registreButton: TextView = view.findViewById(R.id.register_button)
        val email=view.findViewById<EditText>(R.id.email_input)
        val password: EditText =view.findViewById(R.id.password_input)
        //cnonnecter
        connectButton.setOnClickListener {
            context.SignIn(email,password)
        }
        //afficher le fragement inscrit
        registreButton.setOnClickListener {
            context.loadRegisterFragment()
        }
        return view
    }
}