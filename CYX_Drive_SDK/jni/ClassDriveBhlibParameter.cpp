#include <jni.h>
#include "ClassDriveBhlibParameter.h"

ClassDriveBhlibParameter::ClassDriveBhlibParameter(JNIEnv* env) :
		mEnv(env), mThizObj(0) {
	/* Get the class definition */
	mClass = env->FindClass(
			"com/cwits/cyx_drive_sdk/libDriveBh/DriveBhlibParameter");

	if (mClass) {
		// 创建Java类CarMaintainingData的对象
		jmethodID midConstructor = mEnv->GetMethodID(mClass, "<init>", "()V");
		if (midConstructor) {
			mThizObj = mEnv->NewObject(mClass, midConstructor);

			// 获得属性：sensorSwitchArray的ID
			mfIDsensorSwitchArray = env->GetFieldID(mClass, "sensorSwitchArray",
					"[B");

			// 获得属性：sensorRateArray的ID
			mfIDsensorRateArray = env->GetFieldID(mClass, "sensorRateArray",
					"[B");

			// 获得属性：intervalForHandler的ID
			mfIDintervalForHandler = env->GetFieldID(mClass,
					"intervalForHandler", "I");

		}
	}
}

ClassDriveBhlibParameter::~ClassDriveBhlibParameter(void) {
	if (mThizObj && mEnv)
		mEnv->DeleteLocalRef(mThizObj), mThizObj = 0;

	if (mClass && mEnv)
		mEnv->DeleteLocalRef(mClass), mClass = 0;
}

void ClassDriveBhlibParameter::SetObject(jobject obj) {
	if (mThizObj && mEnv)
		mEnv->DeleteLocalRef(mThizObj), mThizObj = 0;

	if (mEnv && obj) {
		mThizObj = mEnv->NewLocalRef(obj);

		msensorSwitchArray = (jbyteArray) mEnv->GetObjectField(mThizObj,
				mfIDsensorSwitchArray);
		msensorRateArray = (jbyteArray) mEnv->GetObjectField(mThizObj,
				mfIDsensorRateArray);
		mintervalForHandler = mEnv->GetIntField(mThizObj,
				mfIDintervalForHandler);

	}
}

int ClassDriveBhlibParameter::SetSensorSwitchArray(char sensorSwitchArray[16]) {
	//mEnv->SetByteArrayRegion(msensorSwitchArray, 0, sizeof(sensorSwitchArray), (jbyte*)sensorSwitchArray);

	jbyteArray ba = mEnv->NewByteArray(16);
	mEnv->SetByteArrayRegion(ba, 0, 16, (jbyte*) sensorSwitchArray);
	mEnv->SetObjectField(mThizObj, mfIDsensorSwitchArray, ba);
	return 0;
}

int ClassDriveBhlibParameter::SetSensorRateArray(char sensorRateArray[16]) {

	jbyteArray ba = mEnv->NewByteArray(16);
	mEnv->SetByteArrayRegion(ba, 0, 16, (jbyte*) sensorRateArray);
	mEnv->SetObjectField(mThizObj, mfIDsensorRateArray, ba);
	return 0;
}

int ClassDriveBhlibParameter::SetIntervalForHandler(int intervalForHandler) {

	mEnv->SetIntField(mThizObj, mfIDintervalForHandler, intervalForHandler);
	return 0;
}
