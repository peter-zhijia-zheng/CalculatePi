package com.duolingo.calculatepijava;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_pi)
    TextView tvPi;
    @BindView(R.id.tv_time_elapsed)
    TextView tvTimeElapsed;
    @BindView(R.id.btn_stop)
    Button btnStop;
    @BindView(R.id.btn_start_or_pause)
    Button btnStartOrPause;

    private long mBaseTime = SystemClock.elapsedRealtime();
    private Timer mTimer;
    private Handler mHandler;
    private long mTimeEscaped;
    private TimerTask mTimerTask;

    private static final String START = "Start";
    private static final String PAUSE = "Pause";
    private static final String CONTINUE = "Continue";

    private static final String KEY_START_OR_PAUSE = "start_or_pause";
    private static final String KEY_TIME_ESCAPED = "time_escaped";
    private static final String KEY_TEXT_PI = "text_pi";
    private static final String KEY_TEXT_TIME_ESCAPED = "text_time_escaped";

    private static class TimerHandler extends Handler {
        private WeakReference<MainActivity> mActivityReference;

        public TimerHandler(MainActivity activity) {
            mActivityReference = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            final MainActivity activity = mActivityReference.get();
            if (activity != null) {
                activity.tvTimeElapsed.setText((String) msg.obj);
                activity.tvPi.setText("PI:" + rand_pi(SystemClock.elapsedRealtime() - activity.mBaseTime));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mHandler = new TimerHandler(this);
        btnStartOrPause.setText(START);
    }

    public static double rand_pi(long n) {
        int numInCircle = 0;
        double x, y;
        double pi;
        for (int i = 0; i < n; i++) {
            x = Math.random();
            y = Math.random();
            if (x * x + y * y < 1)
                numInCircle++;
        }
        pi = (4.0 * numInCircle) / n;
        return pi;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String status = btnStartOrPause.getText().toString();
        outState.putString(KEY_START_OR_PAUSE, status);
        if (TextUtils.equals(PAUSE, status)) {
            mTimeEscaped = SystemClock.elapsedRealtime() - mBaseTime;
        }
        outState.putLong(KEY_TIME_ESCAPED, mTimeEscaped);
        outState.putString(KEY_TEXT_PI, tvPi.getText().toString());
        outState.putString(KEY_TEXT_TIME_ESCAPED, tvTimeElapsed.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String status = savedInstanceState.getString(KEY_START_OR_PAUSE);
        btnStartOrPause.setText(status);
        mTimeEscaped = savedInstanceState.getLong(KEY_TIME_ESCAPED);
        tvPi.setText(savedInstanceState.getString(KEY_TEXT_PI));
        tvTimeElapsed.setText(savedInstanceState.getString(KEY_TEXT_TIME_ESCAPED));
        if (TextUtils.equals(PAUSE, status)) {
            mBaseTime = SystemClock.elapsedRealtime() - mTimeEscaped;
            initTimer();
        }
    }

    @OnClick({R.id.btn_start_or_pause, R.id.btn_stop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_or_pause:
                switch (btnStartOrPause.getText().toString()) {
                    case START:
                    case CONTINUE:
                        btnStartOrPause.setText(PAUSE);
                        mBaseTime = SystemClock.elapsedRealtime() - mTimeEscaped;
                        initTimer();
                        break;
                    case PAUSE:
                        btnStartOrPause.setText(CONTINUE);
                        destroyTimer();
                        mTimeEscaped = SystemClock.elapsedRealtime() - mBaseTime;
                        break;
                }
                break;
            case R.id.btn_stop:
                destroyTimer();
                btnStartOrPause.setText(START);
                mTimeEscaped = 0;
                tvPi.setText("PI:0.0");
                tvTimeElapsed.setText("00:00:00");
                break;
        }
    }

    public void initTimer() {
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                int time = (int) ((SystemClock.elapsedRealtime() - MainActivity.this.mBaseTime) / 1000);
                String hh = new DecimalFormat("00").format(time / 3600);
                String mm = new DecimalFormat("00").format(time % 3600 / 60);
                String ss = new DecimalFormat("00").format(time % 60);
                String timeFormat = hh + ":" + mm + ":" + ss;
                Message msg = new Message();
                msg.obj = timeFormat;
                mHandler.sendMessage(msg);
            }
        };
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(mTimerTask, 0, 1000L);
    }

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
