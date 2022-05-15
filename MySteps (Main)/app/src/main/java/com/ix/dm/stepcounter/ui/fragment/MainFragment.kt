package com.ix.dm.stepcounter.ui.fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.ix.dm.stepcounter.database.AppDatabase
import com.ix.dm.stepcounter.database.User
import com.ix.dm.stepcounter.databinding.FragmentMainBinding
import com.ix.dm.stepcounter.other.STEPNUMBER
import com.ix.dm.stepcounter.util.Constant
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class MainFragment : Fragment() , SensorEventListener {
    lateinit var mBinding:FragmentMainBinding
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalStep = 0f
    private var previousTotalStep = 0f
    private var stepsResetByLongPress = false //FOR DATABASE TEST
    private var manualSetSteps = 250f
    private var detectedDateChange : Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       mBinding = FragmentMainBinding.inflate(inflater,container,false).apply {
           executePendingBindings()
       }
        return mBinding.root

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.circularProgressBar.apply {
            setProgressWithAnimation(0f)}

        //Displaying current DAY in a textview-----------------------------------
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd. MMM")
        val currentday = current.format(formatter)
        mBinding.Time.text = ("$currentday") //Displaying current date in corner
        //-----------------------------------------------------------------------

        timer()
        loadData()
        resetSteps()
        addStepsManuel()
        resetDatabase()
        detectDateChange()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (totalStep < 0f)
            totalStep = 0f
            previousTotalStep = 0f
        if (previousTotalStep < 0f)
            totalStep = 0f
            previousTotalStep = 0f
        if (totalStep < previousTotalStep)
            totalStep = 0f
            previousTotalStep = 0f

        super.onViewCreated(view, savedInstanceState)
    }

    var run = true //set it to false if you want to stop the timer
    var mHandler: Handler = Handler()

    fun timer() { //Displays current time "Live" in a textView by updating it all the time
        Thread(object : Runnable {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                var savedTime = ""
                while (run) {
                    try {
                        Thread.sleep(1000)
                        mHandler.post(Runnable {
                            val current = LocalDateTime.now()
                            val formatter = DateTimeFormatter.ofPattern("HH:mm")
                            val currentTime = current.format(formatter)
                            savedTime = currentTime
                            mBinding.TimeLive.text = ("$currentTime") //Displaying current date in corner

                            detectTimeChange() //runs the time checker to see if specific time is reached for reset etc...
                        })
                    } catch (e: Exception) {
                    }
                }
            }
        }).start()
    }

    private fun detectTimeChange() {
        //Function to check when time changes to specified value
        val inputStr: String = mBinding.TimeLive.text.toString()
        val detectVal = "14:40"

        if (inputStr == detectVal) {
            detectedDateChange = true
            mBinding.TimeChanged.text = ("$detectedDateChange")
            //Then also a function should prompt the DATABASE to save the current step count for the
            // Current day, and also reset total steps to '0' again
            // remember to set "checkVal = false" again to allow next change to be checked

        } /*else {
            detectedDateChange = false
            mBinding.TimeChanged.text = ("$detectedDateChange")
        }*/
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun detectDateChange() {
        val currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        val c = Calendar.getInstance()

        d("TestTime","time? ${currentTime}") //used for LogCat in order to see what is saved
    }

    private fun saveDayInDBAfterChange() {
        val database = Room.databaseBuilder(requireActivity().applicationContext, AppDatabase::class.java,"Step_Database"
        )   .allowMainThreadQueries()
            .build()

        database.userDao().insert(User(1,))
    }

    private fun getCurrentDayCode() {
        //This function should get the current dayCode for use in the DB user

    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (running)
            totalStep = event!!.values[0]
        val currentSteps = totalStep.toInt() - previousTotalStep.toInt()
        mBinding.txtStepCount.text = ("$currentSteps")

        mBinding.circularProgressBar.apply {
            setProgressWithAnimation(currentSteps.toFloat())
        }
    }

    private fun resetDatabase() {
        //Resets all data saved in database
        mBinding.resetDB.setOnClickListener {
            val database = Room.databaseBuilder(requireActivity().applicationContext, AppDatabase::class.java,"Step_Database"
            )   .allowMainThreadQueries()
                .build()

            database.userDao().deleteAll()

            //val dayStorage = database.userDao().getAllDays()

            //Search for "dayTest" in LogCat to find this
            //d("dayTest","all days stored? ${dayStorage}") //used for LogCat in order to see what it holds
        }
    }

    private fun addStepsManuel() {
        mBinding.buttonStep.setOnClickListener {

            if (running)
                if (stepsResetByLongPress) { //FOR DATABASE TEST
                    totalStep = manualSetSteps.toFloat()
                    previousTotalStep = 0f
                } else {
                    totalStep += 1
                    }
            stepsResetByLongPress = false //FOR DATABASE TEST

            val currentSteps = totalStep.toInt() - previousTotalStep.toInt()
            mBinding.txtStepCount.text = ("$currentSteps")

            mBinding.circularProgressBar.apply {
                setProgressWithAnimation(currentSteps.toFloat())
            }
        }
    }

    private fun resetSteps() {
        mBinding.txtStepCount.setOnLongClickListener {
            stepsResetByLongPress = true //FOR DATABASE TEST

            //previousTotalStep = totalStep //FOR DATABASE TEST (commented out)
            mBinding.txtStepCount.text = ("${manualSetSteps.toInt()}")
            mBinding.circularProgressBar.apply {
                setProgressWithAnimation(manualSetSteps) //FOR DATABASE TEST
            }

            mBinding.circularProgressBarMo.apply {
                setProgressWithAnimation(0f) //FOR DATABASE TEST
            }

            mBinding.circularProgressBarTu.apply {
                setProgressWithAnimation(0f)
            }

            mBinding.circularProgressBarWe.apply {
                setProgressWithAnimation(0f)
            }

            mBinding.circularProgressBarTh.apply {
                setProgressWithAnimation(444f) //FOR DATABASE TEST
            }

            mBinding.circularProgressBarFr.apply {
                setProgressWithAnimation(0f)
            }

            mBinding.circularProgressBarSa.apply {
                setProgressWithAnimation(0f)
            }

            mBinding.circularProgressBarSu.apply {
                setProgressWithAnimation(0f)
            }

            saveDate()
            true
        }
    }


    private fun saveDate() {
        Constant.editor(requireContext()).putFloat(STEPNUMBER, previousTotalStep).apply()
    }

    private fun loadData() {
        previousTotalStep = Constant.getSharePref(requireContext()).getFloat(STEPNUMBER, 0f)
    }
}
