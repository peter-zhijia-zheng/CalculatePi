//
// Created by peter on 2019-04-20.
//

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "com_calculate_pi_NativeCalculatePi.h"

// Each time add INCREMENT count point to increase the accuracy, we can adjust this value to make
// it faster(bigger) or slower(smaller) to increase the accuracy.
const int INCREMENT = 100000;
long totalPointCount = 0;
long pointCountInCircle = 0;

/*
 * Class:     com_calculate_pi_NativeCalculatePi
 * Method:    calculatePi
   use Monte Carlo method to calculate PI:
   http://www.eveandersson.com/pi/monte-carlo-circle
   the points will have a incremental increase so that
   calculates pi with increasing accuracy over time.
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_com_calculate_pi_CalculatePiService_calculatePi
  (JNIEnv *env, jclass jclass) {
        totalPointCount += INCREMENT;
        double x, y;
        double pi;
        srand((unsigned)time(NULL));
        for (int i = 0; i < INCREMENT; i++) {
              // A random number from 0 to 1
              x = rand() % 1001 * 0.001;
              y = rand() % 1001 * 0.001;
              if (x * x + y * y < 1) {
                  pointCountInCircle++;
              }
        }
        pi = 4.0 * pointCountInCircle / (double)totalPointCount;

        printf("CalculatePi pointCountInCircle:%ld totalPointCount:%ld/n", pointCountInCircle
              , totalPointCount);
        return pi;
  }

/*
 * Class:     com_calculate_pi_NativeCalculatePi
 * Method:    initCalculate
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_calculate_pi_CalculatePiService_resetCalculateVariables
  (JNIEnv *env, jclass jclass) {
        totalPointCount = 0;
        pointCountInCircle = 0;
  }
