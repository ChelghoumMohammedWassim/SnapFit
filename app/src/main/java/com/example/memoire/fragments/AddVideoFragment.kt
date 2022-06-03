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
import com.example.memoire.VideoRepository.singleton.videodowloadUri
import java.util.*
lateinit var videoProg:TextView
class AddVideoFragment(context:MainActivity):Fragment() {
    var videofile: Uri?=null
    lateinit var changeButton:TextView
    lateinit var addbutton: ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_add_video,container,false)
        //identifer les objets
        val titleinput=view.findViewById<EditText>(R.id.title_input)
        val descriptoninput=view.findViewById<EditText>(R.id.description_input)
        addbutton=view.findViewById<ImageView>(R.id.select_video)
        val cnfButton=view.findViewById<Button>(R.id.add_video_confirm_button)
        changeButton=view.findViewById<TextView>(R.id.pick_other)
        val progress=view.findViewById<ProgressBar>(R.id.progressBar)
        videoProg=view.findViewById(R.id.Video_progress)
        //selecioner un video
        addbutton.setOnClickListener {
            PickUpVideo()
        }

        cnfButton.setOnClickListener {
            val title=titleinput.text.toString().trim()
            val discreption=descriptoninput.text.toString().trim()
            if(videofile!=null && title.isNotEmpty()){
                videoProg.visibility=View.VISIBLE
                progress.visibility=View.VISIBLE
                cnfButton.visibility=View.INVISIBLE
                val repo=VideoRepository()
                titleinput.isEnabled=false
                descriptoninput.isEnabled=false
                //ajouter le video aux cloud(Video)
                repo.uploadvideo(videofile!!){
                    val dowloadUrl= videodowloadUri
                    val video=VideoModel(
                        UUID.randomUUID().toString(),
                        title,
                        User_email,
                        User_Image,
                        dowloadUrl.toString(),
                        discreption
                    )
                    //ajouter la publication
                    repo.insertVideo(video)

                    changeButton.visibility=View.INVISIBLE
                    changeButton.isClickable=false
                    addbutton.setImageResource(R.drawable.add_video)
                    addbutton.isClickable=true
                    titleinput.text.clear()
                    descriptoninput.text.clear()
                    videofile=null
                    cnfButton.visibility=View.VISIBLE
                    progress.visibility=View.INVISIBLE
                    addbutton.requestFocus()
                    titleinput.isEnabled=true
                    descriptoninput.isEnabled=true
                }
            }
            if (titleinput.text.toString().trim().isEmpty()){
                titleinput.text.clear()
                titleinput.requestFocus()
                titleinput.error="Entrer le titre"
            }
            if (videofile==null){
                Toast.makeText(context,"Choisir un video",Toast.LENGTH_SHORT).show()
            }
        }
        //choisir un autre video
        changeButton.setOnClickListener {
        PickUpVideo()
        }

        return view
    }
    //lancre activity de choix
    fun PickUpVideo(){
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100)
    }
    // lorsque l'utlisateur choisir
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) return
            videofile = data.data
            if (videofile!=null){
                changeButton.visibility=View.VISIBLE
                changeButton.isClickable=true
                addbutton.setImageResource(R.drawable.accepted_icon)
                addbutton.isClickable=false
            }
        }
    }

}