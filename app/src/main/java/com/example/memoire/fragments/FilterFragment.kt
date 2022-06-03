package com.example.memoire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memoire.ExerciceRepository.singleton.exerciceList
import com.example.memoire.MainActivity
import com.example.memoire.R
import com.example.memoire.User_email
import com.example.memoire.adapteur.ExerciceAdapteur
import com.example.memoire.adapteur.ExerciceItemDecoration

class FilterFragment(private val context: MainActivity, val filter: String) : Fragment() {
    lateinit var image: ImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_filter, container, false)
        //identfier le Rcl d'affichage
        val filterRcl = view.findViewById<RecyclerView>(R.id.filer_Rcl)
        filterRcl.adapter = ExerciceAdapteur(context,exerciceList.filter { it.muscle == filter && it.addedBy != User_email },R.layout.item_vertical_exercice)
        //ajouter le margine
        filterRcl.addItemDecoration(ExerciceItemDecoration())
        filterRcl.layoutManager = LinearLayoutManager(context)
        //identifer l'image
        image = view.findViewById(R.id.muscle_image)
        val nameMuscle = view.findViewById<TextView>(R.id.muscle_name)
        nameMuscle.text = "${filter}"
        //afficher la bon image de muscle selctioner
        setImage()
        return view
    }
    //afficher la bon image
    fun setImage() {
        when (filter) {
            "Pec" -> image.setImageResource(R.drawable.pec)
            "Dos" -> image.setImageResource(R.drawable.dos)
            "Paule" -> image.setImageResource(R.drawable.paule)
            "Jambe" -> image.setImageResource(R.drawable.jambe)
            "Biceps" -> image.setImageResource(R.drawable.bicepes)
            "Triceps" -> image.setImageResource(R.drawable.triceps)
            "Cardio" -> image.setImageResource(R.drawable.cardio)
        }
    }
}