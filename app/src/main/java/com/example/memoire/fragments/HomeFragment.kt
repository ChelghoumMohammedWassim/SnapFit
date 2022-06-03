package com.example.memoire.fragments


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.memoire.*
import com.example.memoire.ExerciceRepository.singleton.exerciceList
import com.example.memoire.ExerciceRepository.singleton.exerciceListh
import com.example.memoire.adapteur.ExerciceAdapteur
import com.example.memoire.adapteur.ExerciceItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment(private val context:MainActivity) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view= inflater?.inflate(R.layout.fragment_home,container,false)

        //recuperer horizontal recycler view
        val horizontalrecyclerview=view.findViewById<RecyclerView>(R.id.horizontal_recycler_view)
        horizontalrecyclerview.adapter=ExerciceAdapteur(context, exerciceList.sortedByDescending { it.liked}.filter { it.addedBy!= User_email },  R.layout.item_horizontal_exercice)
        //recuperer vertical recycler view
        val verticalrecyclerview=view.findViewById<RecyclerView>(R.id.vertical_recycler_view)
        verticalrecyclerview.adapter=ExerciceAdapteur(context,exerciceList.filter { it.addedBy!= User_email },R.layout.item_vertical_exercice)
        verticalrecyclerview.addItemDecoration(ExerciceItemDecoration())
        //identfier le menu
        val nav:BottomNavigationView=view.findViewById(R.id.home_navigation)
        //afficher l'activity selecioner
        nav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.short_video->{
                    shortVideo=true
                    context.startVideoActivity()
                    Handler().postDelayed({ nav.selectedItemId=R.id.activity },1000)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.steps_conter->{
                    context.startConterActivity()
                    Handler().postDelayed({ nav.selectedItemId=R.id.activity },1000)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.activity->{
                    return@setOnNavigationItemSelectedListener true
                }

                else ->false
            }
        }



        return view

    }
}