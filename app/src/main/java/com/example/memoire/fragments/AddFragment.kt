package com.example.memoire.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.memoire.*
import com.example.memoire.ExerciceRepository.singleton.dowloadUri
import java.util.*

var conBtn: Button? = null
lateinit var imageProg: TextView

class AddFragment(private val context: MainActivity) : Fragment() {
    private var uploadedImage: ImageView? = null
    private var file: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //attache le fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        //recuperer les element
        uploadedImage = view.findViewById(R.id.pre_image)
        val pickupImageButton = view.findViewById<ImageView>(R.id.pre_image)
        imageProg = view.findViewById(R.id.image_progress)
        pickupImageButton.setOnClickListener { pickupImage() }
        //injecter le spiner de nb Serie
        val nbSerie = resources.getStringArray(R.array.Add_Exercice_Page_nbserie)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.spiner_item, nbSerie)
        val nbSerieTv = view.findViewById<AutoCompleteTextView>(R.id.nbserie_spiner)
        nbSerieTv.setAdapter(arrayAdapter)
        //injecter le Spiner de muscle
        val muscle = resources.getStringArray(R.array.muscle_name)
        val arrayMuscleAdapter = ArrayAdapter(requireContext(), R.layout.spiner_item, muscle)
        val muscleTv = view.findViewById<AutoCompleteTextView>(R.id.muscle_spiner)
        muscleTv.setAdapter(arrayMuscleAdapter)

        val ConfirmButton = view.findViewById<Button>(R.id.confirm_button)
        conBtn = ConfirmButton
        ConfirmButton.setOnClickListener {
            sendForm(view)
        }

        return view
    }

    private fun sendForm(view: View) {
        val repo = ExerciceRepository()
        if (file != null && view.findViewById<EditText>(R.id.name_input).text.trim().isNotEmpty()) {
            val progressBar: ProgressBar = view.findViewById(R.id.progress_bar)
            progressBar.visibility = View.VISIBLE
            imageProg.visibility = View.VISIBLE
            conBtn?.visibility = View.INVISIBLE
            view.findViewById<EditText>(R.id.name_input).isEnabled = false
            //ajouter le fichier aux Cloud
            repo.UploadImage(file!!) {
                val exerciceName =
                    view.findViewById<EditText>(R.id.name_input).text.toString().trim()
                val exerciceMuscle =
                    view.findViewById<AutoCompleteTextView>(R.id.muscle_spiner).text.toString()
                val nbSerie =
                    view.findViewById<AutoCompleteTextView>(R.id.nbserie_spiner).text.toString()
                val downloadImageUrl = dowloadUri

                //cree lobjet exercice
                val exercice = ExerciceModel(
                    UUID.randomUUID().toString(),
                    exerciceName,
                    exerciceMuscle,
                    downloadImageUrl.toString(),
                    nbSerie.toInt(),
                    User_email,
                    0,
                )

                //ajouter a la base real
                repo.insertExercice(exercice)
                progressBar.visibility = View.INVISIBLE
                conBtn?.visibility = View.VISIBLE
                view.findViewById<EditText>(R.id.name_input).text.clear()
                view.findViewById<EditText>(R.id.name_input).isEnabled = true
                imageProg.visibility = View.INVISIBLE
                uploadedImage?.setImageResource(R.drawable.add)
                file = null
                if (profileActive) {
                    profileClicked = true
                }
            }
        } else {
            if (file == null) Toast.makeText(context, "Choisir une image", Toast.LENGTH_SHORT).show()
            else Toast.makeText(context, "Entrer les donnees", Toast.LENGTH_SHORT).show()
        }
    }

    //lancer un activity pour la selection d une image
    private fun pickupImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 47)
    }

    //lorsque l'utilisateur sélectionner une image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 47 && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) return
            file = data.data
            //afficher l'image selectioner
            uploadedImage?.setImageURI(file)
        }
    }
}