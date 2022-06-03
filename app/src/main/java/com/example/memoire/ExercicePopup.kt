package com.example.memoire

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.memoire.adapteur.ExerciceAdapteur
import com.example.memoire.adapteur.exist
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

var like = 0
var PubExist = false

class ExercicePopup(private val adapteur: ExerciceAdapteur, val currentExercice: ExerciceModel) :
    Dialog(adapteur.context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_exercice_details)
        setupComppnents()
        setupCloseButton()
        setupStarButton()
    }


    private fun UpdateActionButton(button: ImageView) {
        //verifer si l'element  existe dans la liste de favorite
        val database = FirebaseDatabase.getInstance().getReference("User")
        database.child(User_id).child("Favorite").child(currentExercice.id)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    exist = snapshot.exists()
                    if (exist || currentExercice.addedBy == User_email) {
                        button.setImageResource(R.drawable.like)
                    } else {
                        button.setImageResource(R.drawable.unlike)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun setupStarButton() {
        val starButton = findViewById<ImageView>(R.id.star_button)
        //actualiser l'icon de like
        UpdateActionButton(starButton)
        val repo = ExerciceRepository()
        //verifier si l'ellement ajout par l'utlisateur
        if (User_email == currentExercice.addedBy) {
            starButton.isEnabled = false
        }

        //interaction
        starButton.setOnClickListener {
            getLikes()
            val favRepo = ProfileRepository()
            //si n'existe pas ajoute dans la favorite et actualiser dans la base
            if (exist) {
                val anim: LottieAnimationView = findViewById(R.id.unlikeanim)
                anim.visibility = View.VISIBLE
                anim.speed = (1).toFloat()
                anim.playAnimation()
                Handler().postDelayed({ anim.visibility = View.INVISIBLE }, 700)
                favRepo.deleteFav(currentExercice)
                if (PubExist) {
                    like--
                    currentExercice.liked = like
                    exist = false
                    repo.updateExercice(currentExercice)
                }

            } else {
                //si existe  supprimer et actualiser dans labse
                val anim: LottieAnimationView = findViewById(R.id.likeanim)
                anim.visibility = View.VISIBLE
                anim.speed = (1).toFloat()
                anim.playAnimation()
                Handler().postDelayed({ anim.visibility = View.INVISIBLE }, 850)
                favRepo.insertFav(currentExercice)
                if (PubExist) {
                    like++
                    currentExercice.liked = like
                    repo.updateExercice(currentExercice)
                }
            }
            //afficher les donnees
            setupComppnents()
            if (favFragment) {
                clicked = true
            }
        }
    }

    private fun setupCloseButton() {
        //fermer la popup
        findViewById<ImageView>(R.id.close_button).setOnClickListener {
            dismiss()
        }
    }

    private fun setupComppnents() {
        //actualiser l image
        val exerciceimage = findViewById<ImageView>(R.id.image_item)
        Glide.with(adapteur.context)?.load(Uri.parse(currentExercice.imgeUrl)).into(exerciceimage)
        //le nom
        findViewById<TextView>(R.id.Popup_Exercice_Name).text = currentExercice.name
        //le muscle
        findViewById<TextView>(R.id.Popup_Exercice_Muscle_subtitle).text = currentExercice.muscle
        //les nb de serie
        findViewById<TextView>(R.id.Popup_Exercice_nbserie_subtitle).text =
            currentExercice.nbSerie.toString()
        // les likes
        getLikes()

    }

    private fun setuplikes(liked: Int): String {
        var likes = liked.toString()
        if (liked >= 1000) likes = "${(liked.toFloat() / 1000)}K"
        if (liked >= 1000000) likes = "${liked.toFloat() / 1000000}M"
        return likes
    }

    private fun getLikes() {
        //actualiser le nb de likes si existe
        var l = 0
        val databaseRef = FirebaseDatabase.getInstance().getReference("Model")
        databaseRef.child(currentExercice.id).child("liked")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        l = snapshot.getValue().toString().toInt()
                        findViewById<TextView>(R.id.nb_likes).text = setuplikes(l).toString()
                        like = l
                        PubExist = true
                    } else {
                        findViewById<TextView>(R.id.nb_likes).text = "La publication est supprimée"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        databaseRef.child(currentExercice.id).child("liked")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        PubExist = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}