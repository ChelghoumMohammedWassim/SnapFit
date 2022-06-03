package com.example.memoire


import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class StepsCounter : AppCompatActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var totalSteps = 0f
    private var tot = 0f
    private var previousTotalSteps = 0f
    private var running = false
    lateinit var tv: TextView
    lateinit var button: CardView
    lateinit var calorie_tv: TextView
    lateinit var distance_tv: TextView
    lateinit var rettartBtn: TextView
    var currentSteps = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps_counter)
        tv = findViewById(R.id.TV_STEPS)
        button = findViewById(R.id.button)
        calorie_tv = findViewById(R.id.TV_CALORIES)
        distance_tv = findViewById(R.id.TV_DISTANCE)
        rettartBtn = findViewById(R.id.restart_btn)
        loadData()
        setStats()
        resetSteps()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    //le senseur travailler reste lorsque l'application fermer
    override fun onResume() {
        super.onResume()
        running = true
        val stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepsSensor == null) {
            Toast.makeText(this, "No Senser detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }
    //pour chaque detection
    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            totalSteps = event!!.values[0]
            if (totalSteps != 0f) {
                tot = totalSteps
            }
            setStats()
            savetotal()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    //restarter le conteur
    fun resetSteps() {
        rettartBtn.setOnLongClickListener {
            previousTotalSteps = tot
            tv.text = 0.toString()
            distance_tv.text = "0 M"
            calorie_tv.text = 0.toString()
            saveData()
            Toast.makeText(this, "Le conteur est restarter", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
    }
    //auvegarder le donnee
    private fun saveData() {
        val sheardPreferences: SharedPreferences =
            getSharedPreferences("mPrefs", Context.MODE_PRIVATE)
        val editor = sheardPreferences.edit()
        editor.putFloat("Key1", previousTotalSteps)
        editor.apply()
    }
    //recuperer les donnee
    fun loadData() {
        val sheardPreferences: SharedPreferences =
            getSharedPreferences("mPrefs", Context.MODE_PRIVATE)
        val sevedNumbre = sheardPreferences.getFloat("Key1", 0f)
        val total = sheardPreferences.getFloat("Key2", 0f)
        previousTotalSteps = sevedNumbre
        tot = total
    }

    private fun savetotal() {
        val sheardPreferences: SharedPreferences =
            getSharedPreferences("mPrefs", Context.MODE_PRIVATE)
        val editor = sheardPreferences.edit()
        editor.putFloat("Key2", tot)
        editor.apply()
    }
    //afficher les stats
    private fun setStats() {
        currentSteps = (tot.toInt() - previousTotalSteps.toInt()) / 2
        tv.text = "${(currentSteps).toInt().toString()}"
        var distance = currentSteps / 3.281
        var unit = "M"
        if (distance > 1000) {
            distance = distance / 1000
            unit = "Km"
        }
        val d = ("%.2f".format(distance))
        distance_tv.text = "${d.toString()} ${unit}"
        val calorie = (distance * 0.043)
        calorie_tv.text = "${calorie.toInt().toString()}"
    }

}