package com.calculate.pi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle

internal class MainPresenter(private val mContext: Context, private val mView: IMainView) {

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null && ACTION_UPDATE_PI == intent.action) {
                mView.updatePiAndTime(intent.getStringExtra(PI),
                        intent.getStringExtra(TIME))
            }
        }
    }

    fun init() {
        mContext.registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_PI))
        mView.initViews()
    }

    /**
     * Calculate π according to user's action, the calculation is computing intensive, so we start
     * a service to do this.
     * @param action The user actions: START, PAUSE and STOP
     */
    fun takeAction(action: String) {
        val intent = Intent(mContext, CalculatePiService::class.java)
        intent.putExtra(CalculatePiService.USER_ACTION, action)
        mContext.startService(intent)

        when (action) {
            START -> mView.startCalculation()
            PAUSE -> mView.pauseCalculation()
            STOP -> mView.stopCalculation()
        }
    }

    fun stopCalculateService() {
        val intent = Intent(mContext, CalculatePiService::class.java)
        mContext.stopService(intent)
    }

    fun saveInstanceState(outState: Bundle) {
        mView.saveState(outState)
    }

    fun restoreInstanceState(savedInstanceState: Bundle) {
        mView.restoreState(savedInstanceState)
    }

    fun destroy() {
        mContext.unregisterReceiver(mReceiver)
    }

    companion object {
        const val START = "Start"
        const val PAUSE = "Pause"
        const val CONTINUE = "Continue"
        const val STOP = "Stop"

        const val ACTION_UPDATE_PI = "action_update_pi"
        const val PI = "pi"
        const val TIME = "time"
    }
}
