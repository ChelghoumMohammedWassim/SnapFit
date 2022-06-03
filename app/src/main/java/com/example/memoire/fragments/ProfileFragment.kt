package com.example.memoire.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.memoire.*
import com.example.memoire.ExerciceRepository.singleton.exerciceList
import com.example.memoire.ProfileRepository.singleton.downloadPImage
import com.example.memoire.adapteur.ExerciceAdapteur
import com.example.memoire.adapteur.ExerciceItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileFragment(private val context: MainActivity) : Fragment() {
    var userImage: ImageView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val userEmail: TextView = view.findViewById(R.id.user_email)
        //recuperer l'email
        lateinit var auth: FirebaseAuth
        val favrepo = ProfileRepository()
        auth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        //afficher l'email
        userEmail.text = auth.currentUser?.email.toString()
        //afficher la photo de profile
        userImage = view.findViewById<ImageView>(R.id.image_user)
        favrepo.getImage {
            if (User_Image != null) {
                Glide.with(context).load(Uri.parse(User_Image)).into(userImage!!)
            }
        }
        //injecter le menu
        val MenuButton: ImageView = view.findViewById(R.id.menu_button)
        MenuButton.setOnClickListener {
            val popup = PopupMenu(context, MenuButton)
            popup.inflate(R.menu.optionmenu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    //modifer la photo de profile
                    R.id.edit -> {
                        pickupImage()
                        true
                    }
                    else -> false
                }
            }
            try {//afficher le menu de type popup
                val fildeMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fildeMPopup.isAccessible = true
                val mPopup = fildeMPopup.get(popup)
                mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("Main", "Error showing menu icon:", e)
            }
            popup.show()
        }

        //afficher la liste des exercice partager
        val profileRcl = view.findViewById<RecyclerView>(R.id.profile_recycler)
        profileRcl.adapter = ExerciceAdapteur(
            context,
            exerciceList.filter { it.addedBy == User_email }.sortedBy { it.name },
            R.layout.item_vertical_exercice
        )
        //ajouter le margine
        profileRcl.addItemDecoration(ExerciceItemDecoration())
        profileRcl.layoutManager = LinearLayoutManager(context)

        return view
    }
    //lance l'activity pour choisir une image
    private fun pickupImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 47)
    }
    //lorsque l'utilisateur choisir une image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 47 && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) return
            val selectedImage = data.data
            userImage?.setImageURI(selectedImage)
            val repo = ProfileRepository()
            repo.uploadProfileImage(selectedImage!!) {
                val prepo = ProfileRepository()
                prepo.updateImage(downloadPImage.toString())
            }
        }
    }
}
