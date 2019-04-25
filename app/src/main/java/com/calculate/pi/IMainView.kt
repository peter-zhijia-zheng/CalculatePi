package com.calculate.pi

import android.os.Bundle

internal interface IMainView {
    fun initViews()

    fun updatePiAndTime(pi: String, time: String)

    fun startCalculation()

    fun pauseCalculation()

    fun stopCalculation()

    fun saveState(outState: Bundle)

    fun restoreState(savedInstanceState: Bundle)
}
