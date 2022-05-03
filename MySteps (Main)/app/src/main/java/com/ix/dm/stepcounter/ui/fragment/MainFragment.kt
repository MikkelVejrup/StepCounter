package com.ix.dm.stepcounter.ui.fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.ix.dm.stepcounter.database.AppDatabase
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

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss")
        val formatted = current.format(formatter)

        mBinding.Time.text = ("$formatted")



        loadData()
        resetSteps()
        addStepsManuel()
        resetDatabase()
        detectDateChange()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        super.onViewCreated(view, savedInstanceState)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun detectDateChange() {
        val currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        val c = Calendar.getInstance()



        d("TestTime","time? ${currentTime}") //used for LogCat in order to see what it holds
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
