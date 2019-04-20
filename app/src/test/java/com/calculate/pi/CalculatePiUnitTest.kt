package com.calculate.pi

import android.content.Context
import android.os.Bundle
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class CalculatePiUnitTest {
    @Mock
    private lateinit var view: IMainView

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var bundleState: Bundle

    private lateinit var presenter: MainPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = MainPresenter(context, view)
    }

    @After
    fun tearDown() {
        presenter.stopCalculateService()
        presenter.destroy()
    }

    @Test fun `When started, start to init`() {
        presenter.init()

        verify(view).initView()
    }

    @Test fun `When take start action, start to calculate PI`() {
        presenter.takeAction(MainPresenter.START)

        verify(view).startCalculation()
    }

    @Test fun `When take pause action, pause to calculate PI`() {
        presenter.takeAction(MainPresenter.PAUSE)

        verify(view).pauseCalculation()
    }

    @Test fun `When take stop action, stop to calculate PI`() {
        presenter.takeAction(MainPresenter.STOP)

        verify(view).stopCalculation()
    }

    @Test fun `When activity goto background, the state will be saved`() {
        presenter.saveInstanceState(bundleState)

        verify(view).saveState(bundleState)
    }

    @Test fun `When activity killed or restarted, the state will be restored`() {
        presenter.saveInstanceState(bundleState)

        verify(view).saveState(bundleState)
    }
}