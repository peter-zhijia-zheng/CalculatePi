package com.calculate.pi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements IView {

    public static final String TAG = "CalculatePi";
    public static final String ACTION_UPDATE_PI = "action_update_pi";
    public static final String PI = "pi";
    public static final String TIME = "time";
    public static final String START = "Start";
    public static final String PAUSE = "Pause";
    public static final String CONTINUE = "Continue";
    public static final String STOP = "Stop";
    private static final String KEY_START_OR_PAUSE = "start_or_pause";
    private static final String KEY_TEXT_PI = "text_pi";
    private static final String KEY_TEXT_TIME_ESCAPED = "text_time_escaped";

    @BindView(R.id.tv_pi)
    TextView tvPi;
    @BindView(R.id.tv_time_elapsed)
    TextView tvTimeElapsed;
    @BindView(R.id.btn_stop)
    Button btnStop;
    @BindView(R.id.btn_start_or_pause)
    Button btnStartOrPause;

    private MainPresenter mPresenter = new MainPresenter(this);

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_UPDATE_PI:
                    tvPi.setText("PI:" + intent.getDoubleExtra(PI, 0));
                    tvTimeElapsed.setText(intent.getStringExtra(TIME));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        btnStartOrPause.setText(START);
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_PI));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String status = btnStartOrPause.getText().toString();
        outState.putString(KEY_START_OR_PAUSE, status);
        outState.putString(KEY_TEXT_PI, tvPi.getText().toString());
        outState.putString(KEY_TEXT_TIME_ESCAPED, tvTimeElapsed.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String status = savedInstanceState.getString(KEY_START_OR_PAUSE);
        btnStartOrPause.setText(status);
        tvPi.setText(savedInstanceState.getString(KEY_TEXT_PI));
        tvTimeElapsed.setText(savedInstanceState.getString(KEY_TEXT_TIME_ESCAPED));
        btnStop.setEnabled(!TextUtils.equals(status, START));
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "activity onDestroy");
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CalculatePiService.class);
        stopService(intent);
        super.onBackPressed();
    }

    @OnClick({R.id.btn_start_or_pause, R.id.btn_stop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_or_pause:
                switch (btnStartOrPause.getText().toString()) {
                    case START:
                    case CONTINUE:
                        // User has pressed the Start/Continue button, need to start/continue the
                        // calculation of PI.
                        btnStartOrPause.setText(PAUSE);
                        takeAction(START);
                        break;
                    case PAUSE:
                        // User has pressed the Pause button, need to pause the calculation of PI.
                        btnStartOrPause.setText(CONTINUE);
                        takeAction(PAUSE);
                        break;
                }
                btnStop.setEnabled(true);
                break;
            case R.id.btn_stop:
                // User has pressed stop button, so reset the environment.
                btnStartOrPause.setText(START);
                btnStop.setEnabled(false);
                tvPi.setText("PI:0.0");
                tvTimeElapsed.setText("00:00:00");
                takeAction(STOP);
                break;
        }
    }

    /**
     * Calculate Pi according to user's action, the calculation is computing intensive, so we start
     * a service to do this.
     * @param action
     */
    private void takeAction(String action) {
        Intent intent = new Intent(this, CalculatePiService.class);
        intent.putExtra(CalculatePiService.USER_ACTION, action);
        startService(intent);
    }
}
