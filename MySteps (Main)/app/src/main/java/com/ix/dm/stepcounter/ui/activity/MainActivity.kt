package com.ix.dm.stepcounter.ui.activity

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.Log.d
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.ix.dm.stepcounter.database.AppDatabase
import com.ix.dm.stepcounter.database.User
import com.ix.dm.stepcounter.database.UserDao
import com.ix.dm.stepcounter.databinding.ActivityMainBinding
import com.ix.dm.stepcounter.other.STEPNUMBER
import com.ix.dm.stepcounter.util.Constant
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding

    override fun onResume() {
        stopService(Intent(this, MyService::class.java))
        super.onResume()
    }

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(toolbar)

        //ROOM database setup
        val database = Room.databaseBuilder(this, AppDatabase::class.java,"Step_Database"
        )   .allowMainThreadQueries()
            .build()

        //Inserting a user, depending on the date
        //val currentTime: Date = Calendar.getInstance().getTime() //Gets current date
        //val currentDay = android.text.format.DateFormat.format("EEEE", currentTime) //extracts current day name
        //database.userDao().insert( User(dayCode = currentDay.toString(), stepsCounted = 321) )

        //val dayStorage = database.userDao().getAllDays()

        //Search for "dayTest" in LogCat to find this
        //d("dayTest","all days stored? ${dayStorage}") //used for LogCat in order to see what it holds

    }

    override fun onStop() {
        ContextCompat.startForegroundService(this, Intent(this, MyService::class.java))
        super.onStop()
    }


}

class MyService : Service(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalStep = 0f
    private var previousTotalStep = 0f

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            running  =true
            val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

            sensorManager?.registerListener(this,stepSensor, SensorManager.SENSOR_DELAY_UI)
        } catch (e: Exception) {
            Log.e("eee ERROR", e.message.toString())
        }
        return START_STICKY
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //EMPTY
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onSensorChanged(event: SensorEvent?) {

        if (running)
            totalStep = event!!.values[0]
        val currentSteps = totalStep.toInt() - previousTotalStep.toInt()
        Constant.editor(this).putFloat(STEPNUMBER,previousTotalStep).apply()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    override fun onDestroy() {
        val intent = Intent(this, MyPhoneReciver::class.java)
        sendBroadcast(intent)
        super.onDestroy()
    }
}

class MyPhoneReciver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        ContextCompat.startForegroundService(context!!, Intent(context, MyService::class.java))
    }
}
