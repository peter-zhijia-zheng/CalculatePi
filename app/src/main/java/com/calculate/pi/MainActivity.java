package com.calculate.pi;

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

public class MainActivity extends AppCompatActivity implements IMainView {

    public static final String TAG = "CalculatePi";
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

    private final MainPresenter mPresenter = new MainPresenter(this, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPresenter.init();
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
        btnStop.setEnabled(!TextUtils.equals(status, MainPresenter.START));
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "activity onDestroy");
        mPresenter.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        mPresenter.stopCalculateService();
        super.onBackPressed();
    }

    @OnClick({R.id.btn_start_or_pause, R.id.btn_stop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_or_pause:
                switch (btnStartOrPause.getText().toString()) {
                    case MainPresenter.START:
                    case MainPresenter.CONTINUE:
                        // User has pressed the Start/Continue button, need to start/continue the
                        // calculation of PI.
                        mPresenter.takeAction(MainPresenter.START);
                        break;
                    case MainPresenter.PAUSE:
                        // User has pressed the Pause button, need to pause the calculation of PI.
                        mPresenter.takeAction(MainPresenter.PAUSE);
                        break;
                }
                btnStop.setEnabled(true);
                break;
            case R.id.btn_stop:
                // User has pressed stop button, so reset the environment.
                mPresenter.takeAction(MainPresenter.STOP);
                break;
        }
    }

    @Override
    public void initView() {
        btnStartOrPause.setText(MainPresenter.START);
    }

    @Override
    public void updatePiAndTime(double pi, String time) {
        tvPi.setText("PI:" + pi);
        tvTimeElapsed.setText(time);
    }

    @Override
    public void startCalculation() {
        btnStartOrPause.setText(MainPresenter.PAUSE);
    }

    @Override
    public void pauseCalculation() {
        btnStartOrPause.setText(MainPresenter.CONTINUE);
    }

    @Override
    public void stopCalculation() {
        btnStartOrPause.setText(MainPresenter.START);
        btnStop.setEnabled(false);
        tvPi.setText("PI:0.0");
        tvTimeElapsed.setText("00:00:00");
    }
}
