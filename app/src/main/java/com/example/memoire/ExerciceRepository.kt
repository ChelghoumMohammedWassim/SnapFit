package com.example.memoire


import android.net.Uri
import com.example.memoire.ExerciceRepository.singleton.databaseRef
import com.example.memoire.ExerciceRepository.singleton.dowloadUri
import com.example.memoire.ExerciceRepository.singleton.exerciceList
import com.example.memoire.ExerciceRepository.singleton.exerciceListh
import com.example.memoire.ExerciceRepository.singleton.storageReference
import com.example.memoire.fragments.imageProg
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*

class ExerciceRepository {

    object singleton {
        private val BUCKET_URl: String = "gs://memoire-b4f08.appspot.com"

        //connecter a la ref model
        val databaseRef = FirebaseDatabase.getInstance().getReference("Model")

        //conecter au stokage
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(BUCKET_URl)

        //cree une liste
        val exerciceList = arrayListOf<ExerciceModel>()
        val exerciceListh = arrayListOf<ExerciceModel>()

        var dowloadUri: Uri? = null
    }

    fun updateData(callback: () -> Unit) {

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //supprimer les elements
                exerciceList.clear()
                //recolter la liste
                for (ds in snapshot.children) {
                    //construire un objet ExerciceModel
                    val exercice = ds.getValue(ExerciceModel::class.java)

                    if (exercice != null) {
                        exerciceList.add(exercice)
                    }

                }
                //lancer la code blocker
                callback()
            }

            override fun onCancelled(error: DatabaseError) {}


        })

    }


    //ajouter des fichier a la base
    fun UploadImage(file: Uri, callback: () -> Unit) {
        if (file != null) {
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val reference = storageReference.child("Image").child(fileName)
            //ajoueter l image a cloud
            val uploadTask = reference.putFile(file)
            //envoi l image
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                return@Continuation reference.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //recupere l image
                    dowloadUri = task.result
                    callback()
                }
            }
            //recuperer le taux d avancement
            uploadTask.addOnProgressListener {
                val pr = (100 * it.bytesTransferred) / it.totalByteCount
                imageProg.text = "${pr.toString()}%"
            }
        }
    }

    //mettre a jour un objet
    fun updateExercice(exercice: ExerciceModel) {
        databaseRef.child(exercice.id).setValue(exercice)
    }

    //insereun exercice
    fun insertExercice(exercice: ExerciceModel) {
        databaseRef.child(exercice.id).setValue(exercice)
    }

    //supprimer l exercice
    fun deleteExercice(exercice: ExerciceModel) {
        databaseRef.child(exercice.id).removeValue()
    }

}