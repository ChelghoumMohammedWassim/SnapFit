package com.example.memoire.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memoire.ProfileRepository.singleton.favoriteList
import com.example.memoire.MainActivity
import com.example.memoire.R
import com.example.memoire.adapteur.ExerciceAdapteur
import com.example.memoire.adapteur.ExerciceItemDecoration

class FavoriteFragment(private val context: MainActivity) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        //identifer le Rcl d affichage
        val favoriteRcl= view.findViewById<RecyclerView>(R.id.favorite_RecyclerView)
        //injecter la list
        favoriteRcl.adapter=ExerciceAdapteur(context,favoriteList.sortedBy { it.name},R.layout.item_vertical_exercice)
        //ajouter les  margines
        favoriteRcl.addItemDecoration(ExerciceItemDecoration())
        favoriteRcl.layoutManager=LinearLayoutManager(context)
        return view
    }
}