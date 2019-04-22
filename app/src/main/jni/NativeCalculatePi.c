//
// Created by peter on 2019-04-20.
//

#include <jni.h>
#include "com_calculate_pi_NativeCalculatePi.h"

// π = 3 + 4/(2*3*4) - 4/(4*5*6) + 4/(6*7*8) - 4/(8*9*10) + 4/(10*11*12) - (4/(12*13*14) ...
double i = 2.0;
double pi = 3.0;
double flag = 1.0;

/*
 * Class:     com_calculate_pi_NativeCalculatePi
 * Method:    calculatePi
   use Nilakantha method to calculate π
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_com_calculate_pi_CalculatePiService_calculatePi
  (JNIEnv *env, jclass jclass) {
        for (int count = 0; count < 100; count++) {
            pi += flag * 4.0 / (i * (i + 1) * (i + 2));
            i = i + 2;
            flag = -flag;
        }
        return pi;
  }

/*
 * Class:     com_calculate_pi_NativeCalculatePi
 * Method:    resetCalculateVariables
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_calculate_pi_CalculatePiService_resetCalculateVariables
  (JNIEnv *env, jclass jclass) {
        i = 2.0;
        pi = 3.0;
        flag = 1.0;
  }
