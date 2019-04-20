package com.calculate.pi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

class MainPresenter {
    public static final String START = "Start";
    public static final String PAUSE = "Pause";
    public static final String CONTINUE = "Continue";
    public static final String STOP = "Stop";

    public static final String ACTION_UPDATE_PI = "action_update_pi";
    public static final String PI = "pi";
    public static final String TIME = "time";

    private final IMainView mView;
    private final Activity mActivity;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && ACTION_UPDATE_PI.equals(intent.getAction())) {
                mView.updatePiAndTime(intent.getDoubleExtra(PI, 0),
                        intent.getStringExtra(TIME));
            }
        }
    };

    public MainPresenter(Activity activity, IMainView view) {
        this.mView = view;
        this.mActivity = activity;
    }

    public void init() {
        mActivity.registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_PI));
        mView.initView();
    }

    /**
     * Calculate Pi according to user's action, the calculation is computing intensive, so we start
     * a service to do this.
     * @param action The user actions: START, PAUSE and STOP
     */
    public void takeAction(String action) {
        Intent intent = new Intent(mActivity, CalculatePiService.class);
        intent.putExtra(CalculatePiService.USER_ACTION, action);
        mActivity.startService(intent);

        switch (action) {
            case START:
                mView.startCalculation();
                break;
            case PAUSE:
                mView.pauseCalculation();
                break;
            case STOP:
                mView.stopCalculation();
                break;
        }
    }

    public void stopCalculateService() {
        Intent intent = new Intent(mActivity, CalculatePiService.class);
        mActivity.stopService(intent);
    }

    public void destroy() {
        mActivity.unregisterReceiver(mReceiver);
    }
}
