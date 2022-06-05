package com.example.memoire.adapteur

import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.memoire.*

class VideoAdapteur(List: ArrayList<VideoModel>) :
    RecyclerView.Adapter<VideoAdapteur.VideoViewHolder>() {

    var VideoList: ArrayList<VideoModel> = List


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            //attacher le View Pager
            LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.setVideoData(VideoList[position])
    }

    override fun getItemCount(): Int {
        return VideoList.size
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Declarer les composants de chaque Video
        val tvtitle = itemView.findViewById<TextView>(R.id.title_video)
        val tvemail = itemView.findViewById<TextView>(R.id.email_video)
        val video = itemView.findViewById<VideoView>(R.id.videoView)
        val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar_video)
        val ivtrach = itemView.findViewById<ImageView>(R.id.trach_icon_video)
        val tvdescreption = itemView.findViewById<TextView>(R.id.desception_video)

        fun setVideoData(videoModel: VideoModel) {
            //afficher les elements d item
            tvtitle.text = videoModel.title
            tvemail.text = videoModel.email
            tvdescreption.text = videoModel.description
            video.setVideoURI(Uri.parse(videoModel.url))
            //start la video
            video.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
                override fun onPrepared(mp: MediaPlayer) {
                    progressBar.visibility = View.GONE
                    mp.start()
                    val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
                    val screenRatio = video.width / video.height.toFloat()
                    val scale = videoRatio / screenRatio
                    if (scale > 1f) {
                        video.scaleX = scale
                    } else {
                        video.scaleY = (1f / scale)
                    }
                }

            })
            //restarter le video
            video.setOnCompletionListener { mediaPlayer-> mediaPlayer.start() }
            if (videoModel.email == User_email) {
                ivtrach.visibility = View.VISIBLE
            }
            //supprimer la video
            ivtrach.setOnLongClickListener {
                val repo = VideoRepository()
                repo.deleteVideo(videoModel)
                myVideoClicked = true
                true
            }

        }
    }

}