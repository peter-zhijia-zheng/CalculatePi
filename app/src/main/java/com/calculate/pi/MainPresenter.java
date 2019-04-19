package com.calculate.pi;

import android.os.Bundle;

public class MainPresenter {
    private IView view;

    public MainPresenter(IView view) {
        this.view = view;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    public void onSavedInstanceState(Bundle outBundle) {

    }

    public void onInstructionsReady() {

    }

    public void onGridCompleted() {

    }

    public void onRestartRequested() {

    }

    public void onFinishRequested() {

    }
}
