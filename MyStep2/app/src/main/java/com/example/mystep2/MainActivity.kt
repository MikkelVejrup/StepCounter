package com.example.mystep2

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.mystep2.R
import com.example.mystep2.databinding.ActivityMainBinding
import com.example.mystep2.other.*
import com.example.mystep2.util.Constant
import com.example.mystep2.other.STEPNUMBER
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*



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
