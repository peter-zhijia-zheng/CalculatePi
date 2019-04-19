LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := NativeCalculatePi
LOCAL_SRC_FILES := NativeCalculatePi.c
include $(BUILD_SHARED_LIBRARY)