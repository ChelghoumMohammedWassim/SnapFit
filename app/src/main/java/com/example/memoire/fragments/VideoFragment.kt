package com.example.memoire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.memoire.MainActivity
import com.example.memoire.R
import com.example.memoire.User_email
import com.example.memoire.VideoModel
import com.example.memoire.VideoRepository.singleton.VideoList
import com.example.memoire.adapteur.VideoAdapteur

class VideoFragment(private val context:MainActivity):Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater?.inflate(R.layout.fragment_video,container,false)
        //identifier les objets
        val ViewPager=view.findViewById<ViewPager2>(R.id.MyvideoPager)
        var videoAdapter:VideoAdapteur?=null
        //injecter la list
        videoAdapter= VideoAdapteur(VideoList.filter { it.email == User_email } as ArrayList<VideoModel>)
        ViewPager.adapter=videoAdapter
        return view
    }

}