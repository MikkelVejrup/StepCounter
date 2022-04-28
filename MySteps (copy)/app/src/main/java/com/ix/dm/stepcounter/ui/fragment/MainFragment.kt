package com.ix.dm.stepcounter.ui.fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.ix.dm.stepcounter.databinding.FragmentMainBinding
import com.ix.dm.stepcounter.other.STEPNUMBER
import com.ix.dm.stepcounter.util.Constant



class MainFragment : Fragment() , SensorEventListener {

    lateinit var mBinding:FragmentMainBinding
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalStep = 0f
    private var previousTotalStep = 0f






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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



        mBinding.circularProgressBar.apply {
            setProgressWithAnimation(0f)
        }
        loadData()
        resetSteps()

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


    fun resetSteps() {
        mBinding.txtStepCount.setOnLongClickListener {
            previousTotalStep = totalStep
            mBinding.txtStepCount.text = "1000"
            mBinding.circularProgressBar.apply {
                setProgressWithAnimation(1000f)


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