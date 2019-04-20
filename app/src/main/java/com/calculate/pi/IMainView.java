package com.calculate.pi;

interface IMainView {
    void initView();

    void updatePiAndTime(double pi, String time);

    void startCalculation();

    void pauseCalculation();

    void stopCalculation();
}
