package com.example.memoire

import android.net.Uri
import com.example.memoire.ProfileRepository.singleton.databaseRef
import com.example.memoire.ProfileRepository.singleton.downloadPImage
import com.example.memoire.ProfileRepository.singleton.favoriteList
import com.example.memoire.ProfileRepository.singleton.storageReference
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*

class ProfileRepository {

    object singleton {
        private val BUCKET_URl: String = "gs://memoire-b4f08.appspot.com"

        //connecter a la base
        val databaseRef = FirebaseDatabase.getInstance().getReference("User")

        //cree une liste
        val favoriteList = arrayListOf<ExerciceModel>()

        //conecter au stokage
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(BUCKET_URl)

        var downloadPImage: Uri? = null
    }

    fun updateFavorite(callback: () -> Unit) {
    //recuperer les donnee
        databaseRef.child(User_id).child("Favorite")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //supprimer les elements
                    favoriteList.clear()
                    for (ds in snapshot.children) {
                        val execice = ds.getValue(ExerciceModel::class.java)
                        if (execice != null) {
                            favoriteList.add(execice)
                        }
                    }
                    //liberer le code blocker
                    callback()
                }

                override fun onCancelled(error: DatabaseError) {}

            })

    }
    //ajouetr la publication
    fun insertFav(exercice: ExerciceModel) {
        databaseRef.child(User_id).child("Favorite").child(exercice.id).setValue(exercice)
    }
    //supprimer la publication
    fun deleteFav(exercice: ExerciceModel) {
        databaseRef.child(User_id).child("Favorite").child(exercice.id).removeValue()
    }
    //recuperer la photo de profile
    fun getImage(callback: () -> Unit) {
        databaseRef.child(User_id).child("image")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        User_Image = snapshot.getValue().toString()
                    } else
                        User_Image = "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                    callback()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
    //modifier la photo de profile
    fun updateImage(url: String) {
        databaseRef.child(User_id).child("image").setValue(url)
    }
    //ajoueter l'image selectioner aux cloud
    fun uploadProfileImage(file: Uri, callback: () -> Unit) {
        if (file != null) {
            val fileName = UUID.randomUUID().toString() + "jpg"
            val ref = storageReference.child("Image").child(fileName)
            val uploadtask = ref.putFile(file)

            uploadtask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    downloadPImage = task.result
                    callback()
                }
            }

        }
    }

}
