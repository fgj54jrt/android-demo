# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(APP_PROJECT_PATH)


include $(CLEAR_VARS)
LOCAL_MODULE := libDriveBhCore
LOCAL_SRC_FILES := libs/$(TARGET_ARCH_ABI)/libDriveBhCore.a
LOCAL_EXPORT_C_INCLUDES := $(APP_PROJECT_PATH)/jni
include $(PREBUILT_STATIC_LIBRARY)


LOCAL_PATH := $(APP_PROJECT_PATH)/jni

include $(CLEAR_VARS)

LOCAL_MODULE    := DriveBh
LOCAL_CPPFLAGS	+= -I$(APP_PROJECT_PATH)/jni

LOCAL_SRC_FILES := ClassDriveBhlibParameter.cpp \
	ClassGPSData.cpp \
	ClassMotionData.cpp \
	ClassSensorData.cpp \
	com_cwits_cyx_drive_test_libDriveBh.cpp \
	driveBhWrapper.cpp
	
LOCAL_LDLIBS 	:= -llog
LOCAL_STATIC_LIBRARIES :=  libDriveBhCore

include $(BUILD_SHARED_LIBRARY)
