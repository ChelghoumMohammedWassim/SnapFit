package com.example.memoire

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.memoire.fragments.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

var User_id: String = ""
var btn: FloatingActionButton? = null
var favFragment = false
var homeClicked = false
var profileClicked = false
var clicked = false
var User_email = ""
var User_Image = ""
var profileActive = false
var myVideoClicked = false
var shortVideo = false
lateinit var loadBar: ProgressBar
lateinit var connectionTex: TextView


class MainActivity : AppCompatActivity() {
    val repo = ExerciceRepository()
    val favRepo = ProfileRepository()
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var auth: FirebaseAuth
    //identifier les annimations
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_buttom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_buttom_anim
        )
    }
    lateinit var imageAddButton: FloatingActionButton
    lateinit var videoAddButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //recupere le donner de utilisateur
        auth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        User_id = auth.uid.toString()
        User_email = auth.currentUser?.email.toString()

        imageAddButton = findViewById(R.id.add_image_button)
        videoAddButton = findViewById(R.id.add_video_button)
        val menuButton = findViewById<ImageView>(R.id.menu_button)
        val addshadow = findViewById<ImageView>(R.id.add_shadow)
        loadBar = findViewById(R.id.load_bar)
        //verifer si l'utlisateur est connecter
        if (User_id == "null") {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            finish()
        }
        connectionTex = findViewById(R.id.Connection_info)
        checkConnection()
        //injecter fragment home
        homeClicked = true
        repo.updateData {
            if (!fragmentManager.isDestroyed()) {
                //injecte le fragment
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, HomeFragment(this))
                if (homeClicked) {
                    transaction.commit()
                    homeClicked = false
                }
                loadBar.visibility = View.GONE
            }
            navView.setCheckedItem(R.id.home_page)
            connectionTex.visibility = View.GONE
        }
        //afficher les buttons de publication
        val addButton: FloatingActionButton = findViewById(R.id.add_button)
        btn = addButton
        addButton.setOnClickListener {
            onAddButtonClicked(false)
        }
        addButton.setOnLongClickListener {
            onAddButtonClicked(true)
            return@setOnLongClickListener true
        }
        //injecter le fragment publier image
        imageAddButton.setOnClickListener {
            onAddButtonClicked(true)
            if (!fragmentManager.isDestroyed()) {
                //injecte le fragment
                val transaction = supportFragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.slide_out
                )
                transaction.replace(R.id.fragment_container, AddFragment(this))
                transaction.commit()
            }
            addButton.visibility = View.GONE
            btn?.visibility = View.INVISIBLE
            addshadow.visibility = View.INVISIBLE
        }
        //injecter le fragment publier video
        videoAddButton.setOnClickListener {
            onAddButtonClicked(true)
            if (!fragmentManager.isDestroyed()) {
                //injecte le fragment
                val transaction = supportFragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.slide_out
                )
                transaction.replace(R.id.fragment_container, AddVideoFragment(this))
                transaction.commit()
            }
            addButton.visibility = View.INVISIBLE
            btn?.visibility = View.INVISIBLE
            addshadow.visibility = View.INVISIBLE
        }

        //affichage de menu
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        //declartion de navigation view
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById<NavigationView>(R.id.navView)
        navView.setCheckedItem(R.id.home_page)
        navView.bringToFront()
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // injecter le tete de menu et afficher le détail
        val headerview: View = navView.getHeaderView(0)
        val navUserEmail: TextView = headerview.findViewById(R.id.nav_user_email)
        navUserEmail.text = User_email
        val navUserImage: ImageView = headerview.findViewById(R.id.nav_user_image)
        val favrepo = ProfileRepository()
        favrepo.getImage {
            if (User_Image != null) {
                Glide.with(this).load(Uri.parse(User_Image)).into(navUserImage)
            }
        }

        navView.setNavigationItemSelectedListener {
            profileClicked = false
            addButton.visibility = View.VISIBLE
            btn?.visibility = View.VISIBLE
            addshadow.visibility = View.VISIBLE
            profileActive = false
            addshadow.visibility = View.INVISIBLE
            when (it.itemId) {
                R.id.home_page -> {
                    addshadow.visibility = View.VISIBLE
                    favFragment = false
                    homeClicked = true
                    repo.updateData {
                        if (!fragmentManager.isDestroyed()) {
                            //injecte le fragment
                            val transaction = supportFragmentManager.beginTransaction()
                            transaction.setCustomAnimations(
                                R.anim.fade_out,
                                R.anim.slide_to_left
                            )
                            transaction.replace(R.id.fragment_container, HomeFragment(this))
                            if (homeClicked) {
                                transaction.commit()
                                homeClicked = false
                            }
                        }
                    }

                }
                R.id.favorite_page -> {

                    clicked = true
                    favFragment = true
                    if (!fragmentManager.isDestroyed()) {
                        //injecte le fragment
                        favRepo.updateFavorite {
                            val transaction = supportFragmentManager.beginTransaction()
                            transaction.setCustomAnimations(
                                R.anim.fade_out,
                                R.anim.slide_to_left
                            )
                            transaction.replace(R.id.fragment_container, FavoriteFragment(this))
                            if (clicked == true) {
                                transaction.commit()
                                clicked = false
                            }
                        }

                    }
                }
                R.id.profile_page -> {
                    profileActive = true
                    favFragment = false
                    profileClicked = true
                    repo.updateData {
                        if (!fragmentManager.isDestroyed()) {
                            //injecte le fragment
                            val transaction = supportFragmentManager.beginTransaction()
                            transaction.setCustomAnimations(
                                R.anim.fade_out,
                                R.anim.slide_to_left
                            )
                            transaction.replace(R.id.fragment_container, ProfileFragment(this))
                            if (profileClicked) {
                                transaction.commit()
                                profileClicked = false
                            }

                        }
                    }
                }

                R.id.logout -> {
                    SinOut()
                }
                R.id.Pec -> {
                    Filter("Pec")
                }
                R.id.Dos -> {
                    Filter("Dos")
                }
                R.id.Paule -> {
                    Filter("Paule")
                }
                R.id.Jambe -> {
                    Filter("Jambe")
                }
                R.id.Biceps -> {
                    Filter("Biceps")
                }
                R.id.Triceps -> {
                    Filter("Triceps")
                }
                R.id.Cardio -> {
                    Filter("Cardio")
                }
                R.id.profile_video -> {
                    btn?.visibility = View.INVISIBLE
                    addshadow.visibility = View.INVISIBLE
                    val VideoRepo = VideoRepository()
                    myVideoClicked = true
                    if (!fragmentManager.isDestroyed) {
                        VideoRepo.updateData {
                            val transaction = supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container, VideoFragment(this))
                            if (myVideoClicked) {
                                transaction.commit()
                                myVideoClicked = false
                            }

                        }

                    }
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        //injecter le fragment home
        val reloadButton = findViewById<ImageView>(R.id.reload_button)
        reloadButton.setOnClickListener {
            homeClicked = true
            repo.updateData {
                if (!fragmentManager.isDestroyed()) {
                    //injecte le fragment
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, HomeFragment(this))
                    if (homeClicked) {
                        transaction.commit()
                        homeClicked = false
                    }
                    loadBar.visibility = View.GONE
                }
            }
            navView.setCheckedItem(R.id.home_page)
        }
    }

    // manager le button de publication
    private fun onAddButtonClicked(Btnclicked: Boolean) {
        setVisibility(Btnclicked)
        setAnimation(Btnclicked)
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            imageAddButton.startAnimation(fromBottom)
            videoAddButton.startAnimation(fromBottom)
            btn?.startAnimation(rotateClose)
        } else {
            imageAddButton.startAnimation(toBottom)
            videoAddButton.startAnimation(toBottom)
            btn?.startAnimation(rotateOpen)
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            imageAddButton.visibility = View.VISIBLE
            videoAddButton.visibility = View.VISIBLE
            imageAddButton.isClickable = true
            videoAddButton.isClickable = true
        } else {
            imageAddButton.visibility = View.INVISIBLE
            videoAddButton.visibility = View.INVISIBLE
            imageAddButton.isClickable = false
            videoAddButton.isClickable = false
        }
    }

    //choisir l item selectioner de menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //lorsqu'on clique dans le bouton retoure
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    //Deconncecter et l'ancer l activity de connection
    fun SinOut() {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
        finish()
    }

    //injecter le fragemnt de filtrage
    fun Filter(filter: String) {
        if (!fragmentManager.isDestroyed) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.fade_out,
                R.anim.slide_to_left
            )
            transaction.replace(R.id.fragment_container, FilterFragment(this, filter))
            transaction.commit()
        }
    }

    // lancer l activity des video
    fun startVideoActivity() {
        val repo = VideoRepository()
        repo.updateData {
            if (shortVideo) {
                val intent = Intent(this, VideoActivity::class.java)
                startActivity(intent)
                shortVideo = false
            }
        }
    }

    //lancer l activity de coneteur de pas
    fun startConterActivity() {
        val repo = VideoRepository()
        repo.updateData {
            val intent = Intent(this, StepsCounter::class.java)
            startActivity(intent)
            shortVideo = false
        }
    }

    //verifer la connection
    fun checkConnection() {
        val manager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        if (null == networkInfo) {
            connectionTex.visibility = View.VISIBLE
        }
    }

}

