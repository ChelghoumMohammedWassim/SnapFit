package com.example.memoire

import android.net.Uri
import android.view.View
import com.example.memoire.VideoRepository.singleton.VideoList
import com.example.memoire.VideoRepository.singleton.databaseRef
import com.example.memoire.VideoRepository.singleton.storageReference
import com.example.memoire.VideoRepository.singleton.videodowloadUri
import com.example.memoire.fragments.videoProg
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*

class VideoRepository {

    object singleton {
        private val BUCKET_URl: String = "gs://memoire-b4f08.appspot.com"
        val databaseRef = FirebaseDatabase.getInstance().getReference("Video")
        val VideoList = arrayListOf<VideoModel>()
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(BUCKET_URl)
        var videodowloadUri: Uri? = null
    }


    fun uploadvideo(videofile: Uri, callback: () -> Unit) {
        if (videofile != null) {
            val videofileName = UUID.randomUUID().toString() + ".mp4"
            val reference = storageReference.child("Videos").child(videofileName)
            val uploadTask = reference.putFile(videofile)
        //ajouter le video aux cloud
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                return@Continuation reference.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //recupere l image
                    videodowloadUri = task.result
                    videoProg.visibility = View.INVISIBLE
                    callback()
                }
            }
            //recuperer le taux d avancement
            uploadTask.addOnProgressListener {
                val pr = (100 * it.bytesTransferred) / it.totalByteCount
                videoProg.text = "${pr.toString()}%"
            }
        }
    }
    //recuperer les donnee dans une liste
    fun updateData(callback: () -> Unit) {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                VideoList.clear()
                for (ds in snapshot.children) {
                    val video = ds.getValue(VideoModel::class.java)
                    if (video != null) {
                        VideoList.add(video)
                    }
                }
                callback()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    //ajouter une publication
    fun insertVideo(video: VideoModel) {
        databaseRef.child(video.id).setValue(video)
    }
    //supprimer une publication
    fun deleteVideo(video: VideoModel) {
        databaseRef.child(video.id).removeValue()
    }

}