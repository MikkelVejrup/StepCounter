package com.ix.dm.stepcounter.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ix.dm.stepcounter.R
import com.ix.dm.stepcounter.databinding.ActivityMainBinding
import com.ix.dm.stepcounter.other.STEPNUMBER
import com.ix.dm.stepcounter.ui.fragment.MainFragment
import com.ix.dm.stepcounter.util.Constant
import kotlinx.android.synthetic.main.activity_main.*

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


open class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private var ACTIVITY_RECOGNITION_CODE = 1;

    override fun onResume() {
        stopService(Intent(this, MyService::class.java))
        super.onResume()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(toolbar)

        //Waiting a little while before app checks if App permissions is good to go
        Handler(Looper.getMainLooper()).postDelayed({
            checkSensorPermission(Manifest.permission.ACTIVITY_RECOGNITION, ACTIVITY_RECOGNITION_CODE)
        }, 1000)

        if (savedInstanceState == null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_nav_host_auth, MainFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    //Used to check whether or not, the Physical activity sensor has been granted
    // permission to be used in the app
    private fun checkSensorPermission(permission:String,requestCode:Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission),requestCode)
        } else {
            Toast.makeText(this,"Internal Step Sensor is active",Toast.LENGTH_LONG).show()
        }
    }

    //When calling the "requestPermission" function, this takes over and prompts for user input
    // In order to make the app actually utilize the step counter properly!
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ACTIVITY_RECOGNITION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Activity sensor Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Activity sensor Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onStop() {
        ContextCompat.startForegroundService(this, Intent(this, MyService::class.java))
        super.onStop()
    }

    fun overview(view: View){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_nav_host_auth, MainFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}

class MyService : Service(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalStep = 0f
    private var previousTotalStep = 0f
    var preDay = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd")
        val currentday = current.format(formatter)
        preDay = currentday.toInt()

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

        //-------Reset by day change------------------//
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd")
        val currentday = current.format(formatter)
        val day = currentday.toInt()

        if (day != preDay){
            totalStep = 0f
            previousTotalStep = totalStep
            preDay = day

            Constant.editor(this).putFloat(STEPNUMBER,previousTotalStep).apply()
        }
        //-------------------------------------------//

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
        //d("UserLogDEBUG","MyPhoneReciver Called......")
    }
}
