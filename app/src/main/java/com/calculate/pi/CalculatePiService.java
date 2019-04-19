package com.calculate.pi;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import static com.calculate.pi.MainActivity.ACTION_UPDATE_PI;
import static com.calculate.pi.MainActivity.PI;
import static com.calculate.pi.MainActivity.TIME;

public class CalculatePiService extends Service {
    public static final String USER_ACTION = "user_action";

    private Timer mTimer;
    private TimerTask mTimerTask;
    private long mBaseTime;
    private long mTimeEscaped;

    static {
        System.loadLibrary("NativeCalculatePi");
    }

    /**
     * A native method which will calculate the PI more efficient than java method.
     * @return The current calculated value of PI
     */
    public native static double calculatePi();

    /**
     * A native method which will reset the variables value which used when calculating the PI
     */
    public native static void resetCalculateVariables();

    @Override
    public void onCreate() {
        super.onCreate();

        // Make this service to be foregrounded, so that if the calculation is running,
        // it will continue when the app is backgrounded.
        startForeground(1, new Notification());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getStringExtra(USER_ACTION)) {
                case MainActivity.START:
                    startTimer();
                    break;
                case MainActivity.STOP:
                    resetCalculateVariables();
                    mTimeEscaped = 0;
                    destroyTimer();
                    break;
                case MainActivity.PAUSE:
                    destroyTimer();
                    mTimeEscaped = SystemClock.elapsedRealtime() - mBaseTime;
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        destroyTimer();
        Log.d(MainActivity.TAG, "service onDestroy");
        super.onDestroy();
    }

    /**
     * Start to calculate the PI, the calculate frequency is 1 second, the escaped time will also be
     * calculated.
     */
    public void startTimer() {
        mBaseTime = SystemClock.elapsedRealtime() - mTimeEscaped;
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                int time = (int) ((SystemClock.elapsedRealtime() - CalculatePiService.this.mBaseTime) / 1000);
                final DecimalFormat format = new DecimalFormat("00");
                final String timeFormat = new StringBuilder()
                        .append(format.format(time / 3600)).append(":")
                        .append(format.format(time % 3600 / 60)).append(":")
                        .append(format.format(time % 60)).toString();

                Intent broadcastIntent = new Intent(ACTION_UPDATE_PI);
                broadcastIntent.putExtra(PI, calculatePi());
                broadcastIntent.putExtra(TIME, timeFormat);
                sendBroadcast(broadcastIntent);
            }
        };
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(mTimerTask, 0, 1000L);
    }

    /**
     * Destroy the timer and timer task
     */
    public void destroyTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }
}
