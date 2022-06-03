package com.example.memoire

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.memoire.VideoRepository.singleton.VideoList
import com.example.memoire.adapteur.VideoAdapteur

class VideoActivity : AppCompatActivity() {

    var videoAdapter: VideoAdapteur? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val ViewPager = findViewById<ViewPager2>(R.id.videoPager)
        //injecter la list
        videoAdapter = VideoAdapteur(VideoList.filter { it.email != User_email } as ArrayList<VideoModel>)
        ViewPager.adapter = videoAdapter
    }

}