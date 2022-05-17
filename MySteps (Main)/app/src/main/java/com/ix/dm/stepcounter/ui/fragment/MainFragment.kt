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
import androidx.room.Room
import com.ix.dm.stepcounter.databinding.FragmentMainBinding
import com.ix.dm.stepcounter.other.STEPNUMBER
import com.ix.dm.stepcounter.util.Constant
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.ix.dm.stepcounter.database.UserViewModel

import android.widget.Spinner
import androidx.databinding.DataBindingUtil.setContentView
import com.ix.dm.stepcounter.R

class MainFragment : Fragment() , SensorEventListener {



    lateinit var mBinding:FragmentMainBinding
    lateinit var mUserViewModel: UserViewModel

    private var sensorManager: SensorManager? = null
    private var running : Boolean = false
    private var totalStep = 0f
    private var previousTotalStep = 0f
    private var stepsResetByLongPress : Boolean = false //FOR DATABASE TEST
    private var manualSetSteps = 0f
    private var detectedDateChange : Boolean = false
    private var dailyStepGoal : Int = 2500





    //Instantiating DB object
    /*val databaseMainObject = Room.databaseBuilder(requireActivity().applicationContext, AppDatabase::class.java,"Step_Database"
    )   .allowMainThreadQueries()
        .build()
    */

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

        d("UserLogDEBUG","mUserViewModel initializing")
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
        resetSteps()
        detectDateChange()
        calStep()
        setStepGoal()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mBinding.txtTotalStepCount.text = ("$dailyStepGoal") //Sets the default stepGoal

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
        val detectVal = "00:00"

        if (inputStr == detectVal) {
            detectedDateChange = true
            mBinding.TimeChanged.text = ("$detectedDateChange")

            //Then also a function should prompt the DATABASE to save the current step count for the
            // Current day, and also reset total steps to '0' again
            // remember to set "checkVal = false" again to allow next change to be checked
            //saveDayInDBAfterChange()

        } /*else {
            detectedDateChange = false
            mBinding.TimeChanged.text = ("$detectedDateChange")
        }*/
    }

    /*
    private fun saveDayInDBAfterChange() {
        val databaseMainObject = Room.databaseBuilder(requireActivity().applicationContext, AppDatabase::class.java,"Step_Database"
        )   .allowMainThreadQueries()
            .build()

        databaseMainObject.userDao().updateStepsCounted(getCurrentStepsTaken(),getCurrentDayCode())
        resetDayilySteps()
        detectedDateChange = false //Now program is ready for next time the clock says "00:00"

        //Test section of user update - - - - -
        val dayStorage = databaseMainObject.userDao().getAllDays()
        //Search for "dayTest" in LogCat to find this
        d("dayChangeTest","new day stored? ${dayStorage}") //used for LogCat in order to see what it holds
        //- - - - - - - - - - - - - - - - - - - -
    }
     */

    /*
    private fun fillInPrevDaysFromDB() {
        //This function shall go through the DB object and update all UI objects
        // this is done by a for loop going through the whole DB and then updating
        // the corresponding circularProgressBar
        val databaseMainObject = Room.databaseBuilder(requireActivity().applicationContext, AppDatabase::class.java,"Step_Database"
        )   .allowMainThreadQueries()
            .build()
        mBinding.circularProgressBarMo.apply {
            setProgressWithAnimation(databaseMainObject.userDao().getSpecificUserSteps("Mo")) }

        mBinding.circularProgressBarTu.apply {
            setProgressWithAnimation(databaseMainObject.userDao().getSpecificUserSteps("Tu")) }

        mBinding.circularProgressBarWe.apply {
            setProgressWithAnimation(databaseMainObject.userDao().getSpecificUserSteps("We")) }

        mBinding.circularProgressBarTh.apply {
            setProgressWithAnimation(databaseMainObject.userDao().getSpecificUserSteps("Th")) }

        mBinding.circularProgressBarFr.apply {
            setProgressWithAnimation(databaseMainObject.userDao().getSpecificUserSteps("Fr")) }

        mBinding.circularProgressBarSa.apply {
            setProgressWithAnimation(databaseMainObject.userDao().getSpecificUserSteps("Sa")) }

        mBinding.circularProgressBarSu.apply {
            setProgressWithAnimation(databaseMainObject.userDao().getSpecificUserSteps("Su")) }
    }
    */

    /*
    private fun initializeDatabaseStorage() {
        //This function should create every user/day that can hold a value
        // by using the initialized DB object
        val databaseMainObject = Room.databaseBuilder(requireActivity().applicationContext, AppDatabase::class.java,"Step_Database"
        )   .allowMainThreadQueries()
            .build()

        val dayCodeArray = arrayOf<String>("Mo","Tu","We","Th","Fr","Sa","Su")
        var i : Int = 0
        for (days in dayCodeArray.withIndex()) {
            databaseMainObject.userDao().insert(
                User(
                    dayCode = dayCodeArray[i],
                    stepsCounted = 0,
                    stepDayGoal = dailyStepGoal
                )
            )
            //Printing inserted user (MIGHT BE DELETED LATER)
            val currentDay : User = databaseMainObject.userDao().getSpecificUser(dayCodeArray[i])
            print(currentDay)

            i++
        }
        //For test purposes
        val dayStorage = databaseMainObject.userDao().getAllDays()
        //Search for "dayInsertion" in LogCat to find days inserted
        d("dayInsertion","all days inserted?: ${dayStorage}") //used for LogCat in order to see what it holds
    }*/


    private fun getCurrentDayCode(): String {
        //This function should get the current dayCode for use in the DB user
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
            stepsResetByLongPress = true

            previousTotalStep = totalStep //FOR DATABASE TEST (commented out)
            mBinding.txtStepCount.text = ("${manualSetSteps.toInt()}")
            mBinding.circularProgressBar.apply {
                setProgressWithAnimation(manualSetSteps)
            }

            mBinding.circularProgressBarMo.apply {
                setProgressWithAnimation(0f)
            }

            mBinding.circularProgressBarTu.apply {
                setProgressWithAnimation(0f)
            }

            mBinding.circularProgressBarWe.apply {
                setProgressWithAnimation(0f)
            }

            mBinding.circularProgressBarTh.apply {
                setProgressWithAnimation(0f)
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

            dailyStepGoal = mBinding.txtStepCount.text.toString().toInt()

        }

    }

    private fun saveDate() {
        Constant.editor(requireContext()).putFloat(STEPNUMBER, previousTotalStep).apply()
    }

    private fun loadData() {
        previousTotalStep = Constant.getSharePref(requireContext()).getFloat(STEPNUMBER, 0f)
    }
}
