package com.ix.dm.stepcounter.ui.fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.ix.dm.stepcounter.databinding.FragmentMainBinding
import com.ix.dm.stepcounter.other.STEPNUMBER
import com.ix.dm.stepcounter.other.STEPGOALNUMBER
import com.ix.dm.stepcounter.util.Constant
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.ix.dm.stepcounter.database.User
import com.ix.dm.stepcounter.database.UserViewModel

class MainFragment : Fragment() , SensorEventListener {
    lateinit var mBinding: FragmentMainBinding
    lateinit var mUserViewModel: UserViewModel
    private var sensorManager: SensorManager? = null
    private var running : Boolean = false
    private var totalStep = 0f
    private var previousTotalStep = 0f
    private var stepsResetByLongPress : Boolean = false //FOR DATABASE TEST
    private var manualSetSteps = 1000f
    private var detectedDateChange : Boolean = false
    private var dailyStepGoal : Int = 2500


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        loadData()
        super.onResume()
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        mBinding.txtTotalStepCount.text = ("$dailyStepGoal") //Sets the default stepGoal

        sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       mBinding = FragmentMainBinding.inflate(inflater,container,false).apply {
           executePendingBindings()
       }

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

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
        detectDateChange()
        calStep()
        setStepGoal()
        resetSteps() //TEMP !
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
                while (run) {
                    try {
                        Thread.sleep(1000)
                        mHandler.post(Runnable {
                            val current = LocalDateTime.now()
                            val formatter = DateTimeFormatter.ofPattern("HH:mm")
                            val currentTime = current.format(formatter)
                            mBinding.TimeLive.text = ("$currentTime") //Displaying current date in corner

                            detectTimeChange() //runs the time checker to see if specific time is reached for reset etc...
                        })
                    } catch (e: Exception) {
                    }
                }
            }
        }).start()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun detectDateChange() {
        val currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        val c = Calendar.getInstance()

        d("UserLogDEBUG","time? ${currentTime}") //used for LogCat in order to see what is saved
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun detectTimeChange() {
        //Function to check when time changes to specified value
        val inputStr: String = mBinding.TimeLive.text.toString()
        val detectVal = "17:05"
        val clearVal = "16:06"

        if (inputStr == clearVal) {
                detectedDateChange = false //waiting for another change
        }
        if (inputStr == detectVal && !detectedDateChange) {
            detectedDateChange = true
            mBinding.TimeChanged.text = ("$detectedDateChange")

            saveDayInDBAfterChange()
        }
    }


    private fun saveDayInDBAfterChange() {
        //Saves day in database by updating existing user by daycode
        // then also prompts an update of the previous days progressbars
        d("UserLogDEBUG","Updating the day with current steps")
        val thisUser : User = mUserViewModel.getSpecificUserByDay(getCurrentDayCode())

        //test before
        val userStepsBefore = thisUser.stepsCounted
        d("UserLogDEBUG","userSteps Before: $userStepsBefore")
        //===========

        thisUser.stepsCounted = getCurrentStepsTaken()

        mUserViewModel.update(thisUser)

        //test after
        val thisUser2 : User = mUserViewModel.getSpecificUserByDay(getCurrentDayCode())
        val userStepsAfter = thisUser2.stepsCounted
        d("UserLogDEBUG","userSteps After: $userStepsAfter")
        //===========

        fillInPrevDaysFromDB()
        resetDayilySteps()
    }


    private fun fillInPrevDaysFromDB() {
        //This function shall go through the DB object and update all UI objects
        // this is done by a for loop going through the whole DB and then updating
        // the corresponding circularProgressBar

        mBinding.circularProgressBarMo.apply {
            setProgressWithAnimation(dbGetStepsFromDay("Mo").toFloat()) }

        mBinding.circularProgressBarTu.apply {
            setProgressWithAnimation(dbGetStepsFromDay("Tu").toFloat()) }

        mBinding.circularProgressBarWe.apply {
            setProgressWithAnimation(dbGetStepsFromDay("We").toFloat()) }

        mBinding.circularProgressBarTh.apply {
            setProgressWithAnimation(dbGetStepsFromDay("Th").toFloat()) }

        mBinding.circularProgressBarFr.apply {
            setProgressWithAnimation(dbGetStepsFromDay("Fr").toFloat()) }

        mBinding.circularProgressBarSa.apply {
            setProgressWithAnimation(dbGetStepsFromDay("Sa").toFloat()) }

        mBinding.circularProgressBarSu.apply {
            setProgressWithAnimation(dbGetStepsFromDay("Su").toFloat()) }
    }

    private fun dbGetStepsFromDay(dayCode : String) : Int {
        //Returns the walked currently walked steps for current day
        val thisUser = mUserViewModel.getSpecificUserByDay(dayCode)
        val steps = thisUser.stepsCounted

        val stepsinfo = "${thisUser.stepsCounted} steps taken for ${thisUser.dayCode}"
        d("UserLogDEBUG","dbGetStepsFromDay says: $stepsinfo")

        return steps
    }

    private fun getCurrentDayCode(): String {
        //This function gets the current dayCode (used for updating DB)
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_WEEK]
        var dayCodeTMP = ""

        when (day) {
            Calendar.MONDAY     -> {dayCodeTMP = "Mo"}
            Calendar.TUESDAY    -> {dayCodeTMP = "Tu"}
            Calendar.WEDNESDAY  -> {dayCodeTMP = "We"}
            Calendar.THURSDAY   -> {dayCodeTMP = "Th"}
            Calendar.FRIDAY     -> {dayCodeTMP = "Fr"}
            Calendar.SATURDAY   -> {dayCodeTMP = "Sa"}
            Calendar.SUNDAY     -> {dayCodeTMP = "Su"}
        }
        return dayCodeTMP
    }

    private fun getCurrentStepsTaken(): Int {
        var currentStepsTMP: Int = 0

        currentStepsTMP = totalStep.toInt() - previousTotalStep.toInt()

        return currentStepsTMP
    }

    private fun resetDayilySteps() {
        previousTotalStep = totalStep
        mBinding.txtStepCount.text = ("${0}")
        mBinding.circularProgressBar.apply {
            setProgressWithAnimation(0f)
        }
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


    private fun resetSteps() {
        mBinding.txtStepCount.setOnLongClickListener {
            previousTotalStep = totalStep
            mBinding.txtStepCount.text = "0"
            mBinding.circularProgressBar.apply {
                setProgressWithAnimation(0f)
            }
            saveDate()
            true
        }
    }

    private fun calStep(){
        mBinding.bCalStep.setOnClickListener {
            val age = mBinding.age.text.toString().toInt()
            val aLvl = mBinding.ALvl.text.toString().toInt()

            if (aLvl == 1) {
                if (age < 20){
                    mBinding.txtcalSteps.text = ("6000")}

                if (age >= 20){
                    mBinding.txtcalSteps.text = ("3000")}
            }

            if (aLvl == 2) {
                if (age <= 6){
                    mBinding.txtcalSteps.text = ("10000")}

                if (age > 6 || age < 11){
                    mBinding.txtcalSteps.text = ("13000")}

                if (age >= 11 || age < 20){
                    mBinding.txtcalSteps.text = ("10000")}

                if (age >= 20 || age < 65){
                    mBinding.txtcalSteps.text = ("7000")}

                if (age >= 65){
                    mBinding.txtcalSteps.text = ("7000")}
            }

            if (aLvl == 3) {
                if (age <= 6){
                    mBinding.txtcalSteps.text = ("14500")}

                if (age > 6 || age < 11){
                    mBinding.txtcalSteps.text = ("15500")}

                if (age >= 11 || age < 20){
                    mBinding.txtcalSteps.text = ("12500")}

                if (age >= 20 || age < 65) {
                    mBinding.txtcalSteps.text = ("11500")}

                if (age >= 65) {
                    mBinding.txtcalSteps.text = ("1050")}

            }
        }
    }

    private fun setStepGoal(){
        mBinding.bStepGoal.setOnClickListener {

            dailyStepGoal = mBinding.txtcalSteps.text.toString().toFloat().toInt()

            mBinding.txtTotalStepCount.text = ("$dailyStepGoal")

            mBinding.circularProgressBar.apply {
                progressMax = dailyStepGoal.toFloat()
            }
            saveDate()
        }
    }


    private fun saveDate() {
        Constant.editor(requireContext()).putFloat(STEPNUMBER, previousTotalStep).apply()
        Constant.editor(requireContext()).putFloat(STEPGOALNUMBER, dailyStepGoal.toFloat()).apply()
    }


    private fun loadData() {
        previousTotalStep = Constant.getSharePref(requireContext()).getFloat(STEPNUMBER, 0f)
        dailyStepGoal = Constant.getSharePref(requireContext()).getFloat(STEPGOALNUMBER, dailyStepGoal.toFloat()).toInt()
    }
}
