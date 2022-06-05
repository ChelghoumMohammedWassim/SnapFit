package com.example.memoire.adapteur

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.memoire.*

var exist=false
class ExerciceAdapteur(val context:MainActivity, private val exerciceList:List<ExerciceModel>, private val layoutId:Int) : RecyclerView.Adapter<ExerciceAdapteur.ViewHolder>() {


    //boite pour ronger les composants
    class ViewHolder(view:View): RecyclerView.ViewHolder(view) {
        val exerciceimage=view.findViewById<ImageView>(R.id.image_item)
        val exercicename:TextView?=view.findViewById(R.id.name_item)
        val exercicemuscle:TextView?=view.findViewById(R.id.muscle_item)
        val trachIcon=view.findViewById<ImageView>(R.id.trach_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //attacher le RCl
       val view=LayoutInflater.from(parent.context).inflate(layoutId,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     //recuperer les info de chaque modele
        val currentExercice=exerciceList[position]
        //recupere le repo
        val repo=ProfileRepository()
        //utiliser glide pour recupere l'image url
        Glide.with(context).load(Uri.parse(currentExercice.imgeUrl)).into(holder.exerciceimage)
        //mettre a jour le nom et le muscle
        holder.exercicename?.text=currentExercice.name
        holder.exercicemuscle?.text=currentExercice.muscle
        //verifier si l'exercice a ete like
        if (currentExercice.addedBy== User_email){
            holder.trachIcon.visibility=View.VISIBLE
            holder.trachIcon.setOnLongClickListener {
                val repos = ExerciceRepository()
                repos.deleteExercice(currentExercice)
                profileClicked=true
                Toast.makeText(context,"La publication est supprimée",Toast.LENGTH_SHORT).show()
                true
            }
        }
        holder.itemView.setOnClickListener{
            //afficher la popup
            val popup= ExercicePopup(this,currentExercice)
            popup.window?.setBackgroundDrawableResource(R.drawable.poup_background)
            popup.show()

        }

    }

    override fun getItemCount(): Int {
       return exerciceList.size
    }
}