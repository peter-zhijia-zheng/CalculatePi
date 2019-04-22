package com.calculate.pi

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import java.util.*

class CalculatePiService : Service() {

    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null
    private var mBaseTime: Long = 0
    private var mTimeEscaped: Long = 0

    // hh : mm : ss
    private val FORMAT_DURATION_H_M_S = "%02d:%02d:%02d"

    override fun onCreate() {
        super.onCreate()

        // Make this service to be foregrounded, so that if the calculation is running,
        // it will continue when the app is backgrounded.
        startForeground(1, Notification())
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.getStringExtra(USER_ACTION)) {
                // Start to calculate PI
                MainPresenter.START -> startTimer()

                // Stop to calculate PI
                MainPresenter.STOP -> {
                    resetCalculateVariables()
                    mTimeEscaped = 0
                    destroyTimer()
                }

                // Pause to calculate PI
                MainPresenter.PAUSE -> {
                    mTimeEscaped = SystemClock.elapsedRealtime() - mBaseTime
                    destroyTimer()
                }
            }
        }
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        destroyTimer()
        super.onDestroy()
    }

    /**
     * Start to calculate the PI, the calculate frequency is 1 second, the escaped time will also be
     * calculated.
     */
    private fun startTimer() {
        mBaseTime = SystemClock.elapsedRealtime() - mTimeEscaped
        mTimerTask = object : TimerTask() {

            override fun run() {
                val time = ((SystemClock.elapsedRealtime() - this@CalculatePiService.mBaseTime) / 1000).toInt()
                val timeFormat = String.format(FORMAT_DURATION_H_M_S, time / 3600, time % 3600 / 60,
                        time % 3600 % 60)

                val broadcastIntent = Intent(MainPresenter.ACTION_UPDATE_PI)
                broadcastIntent.putExtra(MainPresenter.PI, "π = " + calculatePi())
                broadcastIntent.putExtra(MainPresenter.TIME, timeFormat)
                sendBroadcast(broadcastIntent)
            }
        }
        mTimer = Timer()
        mTimer!!.scheduleAtFixedRate(mTimerTask, 0, 1000L)
    }

    /**
     * Destroy the timer and timer task
     */
    private fun destroyTimer() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
        if (mTimerTask != null) {
            mTimerTask!!.cancel()
            mTimerTask = null
        }
    }

    /**
     * A native method which will calculate the PI more efficient than java method.
     * @return The current calculated value of π
     */
    private external fun calculatePi(): Double

    /**
     * A native method which will reset the variables value which used when calculating the π
     */
    private external fun resetCalculateVariables()

    companion object {
        const val USER_ACTION = "user_action"

        init {
            System.loadLibrary("NativeCalculatePi")
        }
    }
}
