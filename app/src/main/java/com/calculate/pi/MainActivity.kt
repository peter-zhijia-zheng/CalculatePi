package com.calculate.pi

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

class MainActivity : AppCompatActivity(), IMainView {

    @BindView(R.id.tv_pi)
    lateinit var tvPi: TextView
    @BindView(R.id.tv_time_elapsed)
    lateinit var tvTimeElapsed: TextView
    @BindView(R.id.btn_stop)
    lateinit var btnStop: Button
    @BindView(R.id.btn_start_or_pause)
    lateinit var btnStartOrPause: Button

    private val mPresenter = MainPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        mPresenter.init()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mPresenter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mPresenter.restoreInstanceState(savedInstanceState)
    }

    override fun onDestroy() {
        mPresenter.destroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        mPresenter.stopCalculateService()
        super.onBackPressed()
    }

    @OnClick(R.id.btn_start_or_pause, R.id.btn_stop)
    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_start_or_pause -> {
                when (btnStartOrPause.text.toString()) {
                    MainPresenter.START, MainPresenter.CONTINUE ->

                        // User has pressed the Start/Continue button, need to start/continue the
                        // calculation of PI.
                        mPresenter.takeAction(MainPresenter.START)
                    MainPresenter.PAUSE ->

                        // User has pressed the Pause button, need to pause the calculation of PI.
                        mPresenter.takeAction(MainPresenter.PAUSE)
                }
                btnStop.isEnabled = true
            }
            R.id.btn_stop ->

                // User has pressed stop button, so reset the environment.
                mPresenter.takeAction(MainPresenter.STOP)
        }
    }

    override fun initViews() {
        btnStartOrPause.text = MainPresenter.START
        btnStop.text = MainPresenter.STOP
        btnStop.isEnabled = false
        tvPi.text = "π = 0.0"
        tvTimeElapsed.text = "00:00:00"
    }

    override fun updatePiAndTime(pi: String, time: String) {
        tvPi.text = pi
        tvTimeElapsed.text = time
    }

    /**
     * Save current UI state when activity goto background
     */
    override fun saveState(outState: Bundle) {
        val status = btnStartOrPause.text.toString()
        outState.putString(KEY_START_OR_PAUSE, status)
        outState.putString(KEY_TEXT_PI, tvPi.text.toString())
        outState.putString(KEY_TEXT_TIME_ESCAPED, tvTimeElapsed.text.toString())
    }

    /**
     * Restore the UI state when activity restarted
     */
    override fun restoreState(savedInstanceState: Bundle) {
        val status = savedInstanceState.getString(KEY_START_OR_PAUSE)
        btnStartOrPause.text = status
        tvPi.text = savedInstanceState.getString(KEY_TEXT_PI)
        tvTimeElapsed.text = savedInstanceState.getString(KEY_TEXT_TIME_ESCAPED)
        btnStop.isEnabled = !TextUtils.equals(status, MainPresenter.START)
    }

    override fun startCalculation() {
        btnStartOrPause.text = MainPresenter.PAUSE
    }

    override fun pauseCalculation() {
        btnStartOrPause.text = MainPresenter.CONTINUE
    }

    override fun stopCalculation() {
        initViews()
        mPresenter.stopCalculateService()
    }

    companion object {
        private const val KEY_START_OR_PAUSE = "start_or_pause"
        private const val KEY_TEXT_PI = "text_pi"
        private const val KEY_TEXT_TIME_ESCAPED = "text_time_escaped"
    }
}
