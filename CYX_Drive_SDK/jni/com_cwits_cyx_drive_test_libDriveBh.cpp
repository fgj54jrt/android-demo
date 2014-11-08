#include <jni.h>
#include <android/log.h>
#include "com_cwits_cyx_drive_test_libDriveBh.h"
#include "ClassMotionData.h"
#include "ClassGPSData.h"
#include "driveBhLib.h"
#include "ClassDriveBhlibParameter.h"
#include "ClassSensorData.h"
#include "driveBhWrapper.h"
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

static JNIEnv* mEnv;
/**
 * getJNIEnv
 */
JNIEnv* getJNIEnv() {
	return mEnv;
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
	vm->GetEnv((void**)&mEnv, JNI_VERSION_1_4);
	return JNI_VERSION_1_4;
}

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:    NativeGetDriveBhParameter
 * private native static int  NativeGetDriveBhParameter(DriveBhlibParameter parmeter);
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/AccData;Lcom/cwits/cyx_drive_sdk/libDriveBh/GPSData;Lcom/cwits/cyx_drive_sdk/libDriveBh/MagData;)S
 */
JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeGetDriveBhParameter(
		JNIEnv* env, jobject thiz, jobject parmeter) {
#define SENSOR_SWITCH 16
#define SENSOR_RATE	16

	parameter_t parameterArr = {0};
	ClassDriveBhlibParameter parameterObject(env);
	int result = getDriveBhParameter(&parameterArr);
	parameterObject.SetObject(parmeter);

	parameterObject.SetSensorSwitchArray(parameterArr.sensorSwitchArray);
	parameterObject.SetSensorRateArray(parameterArr.sensorRateArray);
	parameterObject.SetIntervalForHandler(parameterArr.intervalForHandler);

	return result;
}

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:    NativeInitDriveBhLib
 * private native static int  NativeInitDriveBhLib(char[] availableSensor);
 * Signature: (S)Z
 */
JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeInitDriveBhLib(
		JNIEnv* env, jobject thiz, jbyteArray availableSensor) {
	#define SENSOR_COUNT	16
	int i = 0;
	//jchar* mavailableSensor =  env->GetCharArrayElements(availableSensor,0);
	char mavailableSensor[SENSOR_COUNT] = { 0 };
	char *p = (char*)env->GetByteArrayElements(availableSensor, 0);
	for (i = 0; i < SENSOR_COUNT && i < env->GetArrayLength(availableSensor);
			++i) {
		mavailableSensor[i] = p[i];
	}
	int result = initDriveBhLib(mavailableSensor);
	if(p)
		env->ReleaseByteArrayElements(availableSensor,(jbyte*)p,JNI_ABORT);
	return result;
}

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:    NativeAccDataHandler(motionDatas[] motionDatass);
 * private native static int  NativeAccDataHandler(motionDatas[] motionDatass);
 * Signature: ()I
 */

JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeAccDataHandler
  (JNIEnv* env, jobject thiz,jobjectArray motionDatas){
    int i = 0;
    int count = (int)env->GetArrayLength(motionDatas);

	motionData_t *dataArr =
			(motionData_t*) new motionData_t[sizeof(motionData_t) * count];
	motionData_t *temp = dataArr;
	for (i = 0; dataArr && i < count; ++i) {
		ClassMotionData motionDatasObj(env);
		motionDatasObj.SetObject(env->GetObjectArrayElement(motionDatas, i));
		dataArr->x = motionDatasObj.mx;
		dataArr->y = motionDatasObj.my;
		dataArr->z = motionDatasObj.mz;
		dataArr->time = motionDatasObj.mtime;
		dataArr++;
	}

	int result = accDataHandler(count, temp);
	if (temp)
		delete[] temp;


	return result;
}

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:     NativeGyroDataHandler(motionDatas[] motionDatass);
 * private native static int  NativeGyroDataHandler(motionDatas[] motionDatass);
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/motionDatas;)
 */

JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeGyroDataHandler
  (JNIEnv* env, jobject thiz,jobjectArray motionDatas){
		int i = 0;
	    int count = (int)env->GetArrayLength(motionDatas);

	motionData_t *dataArr =
			(motionData_t*) new motionData_t[sizeof(motionData_t) * count];
	motionData_t *temp = dataArr;
	for (i = 0; dataArr && i < count; ++i) {
		ClassMotionData motionDatasObj(env);
		motionDatasObj.SetObject(env->GetObjectArrayElement(motionDatas, i));
		dataArr->x = motionDatasObj.mx;
		dataArr->y = motionDatasObj.my;
		dataArr->z = motionDatasObj.mz;
		dataArr->time = motionDatasObj.mtime;
		dataArr++;
	}

	int result = gyroDataHandler(count, temp);
	if (temp)
		delete[] temp;

	return result;
}

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:    NativeGravityDataHandler(motionDatas[] motionDatass);
 * private native static int NativeGravityDataHandler(motionDatas[] motionDatass);
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/motionDatas)
 */

JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeGravityDataHandler
  (JNIEnv* env, jobject thiz,jobjectArray motionDatas){
			int i = 0;
		    int count = (int)env->GetArrayLength(motionDatas);


	motionData_t *dataArr =
			(motionData_t*) new motionData_t[sizeof(motionData_t) * count];
	motionData_t *temp = dataArr;
	for (i = 0; dataArr && i < count; ++i) {
		ClassMotionData motionDatasObj(env);
		motionDatasObj.SetObject(env->GetObjectArrayElement(motionDatas, i));
		dataArr->x = motionDatasObj.mx;
		dataArr->y = motionDatasObj.my;
		dataArr->z = motionDatasObj.mz;
		dataArr->time = motionDatasObj.mtime;
		dataArr++;
	}

	int result = gravityDataHandler(count, temp);
	if (temp)
		delete[] temp;

	return result;
}

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:    NativeOrientationDataHandler(motionDatas[] motionDatass);
 * private native static int NativeOrientationDataHandler(motionDatas[] motionDatass);
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/motionDatas;B)D
 */
JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeOrientationDataHandler(
		JNIEnv* env, jobject thiz, jobjectArray motionDatas) {
	int i = 0;
    int count = (int)env->GetArrayLength(motionDatas);

	motionData_t *dataArr =
			(motionData_t*) new motionData_t[sizeof(motionData_t) * count];
	motionData_t *temp = dataArr;
	for (i = 0; dataArr && i < count; ++i) {
		ClassMotionData motionDatasObj(env);
		motionDatasObj.SetObject(env->GetObjectArrayElement(motionDatas, i));
		dataArr->x = motionDatasObj.mx;
		dataArr->y = motionDatasObj.my;
		dataArr->z = motionDatasObj.mz;
		dataArr->time = motionDatasObj.mtime;
		dataArr++;
	}

	int result = orientationDataHandler(count, temp);
	if (temp)
		delete[] temp;

	return result;
}

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:     NativeGpsDataHandler(motionDatas[] motionDatass);
 * private native static int  NativeGpsDataHandler(motionDatas[] motionDatass);
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/motionDatas;B)D
 */
JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeGpsDataHandler(
		JNIEnv* env, jobject thiz, jobjectArray gpsData) {
	int i = 0;
    int count = (int)env->GetArrayLength(gpsData);

	gpsData_t *dataArr = (gpsData_t*) new gpsData_t[sizeof(gpsData_t) * count];
	for (i = 0; dataArr && i < count; ++i) {
		ClassGPSData gpsDataObj(env);
		gpsDataObj.SetObject(env->GetObjectArrayElement(gpsData, i));
		dataArr->time = gpsDataObj.mtime;
		dataArr->direction = gpsDataObj.mdirection;
		dataArr->speed = gpsDataObj.mspeed;
		dataArr->radius = gpsDataObj.mradius;
		dataArr->longitude = gpsDataObj.mlongitude;
		dataArr->latitude = gpsDataObj.mlatitude;
		dataArr->altitude = gpsDataObj.maltitude;
	}

	int result = gpsDataHandler(count, dataArr);
	if (dataArr)
		delete[] dataArr;

	return result;
}

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:     NativeGpsDataHandler();
 * private native static double  NativeGpsDataHandler();
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/motionDatas;B)D
 */
JNIEXPORT jdouble JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeGetDistance(
		JNIEnv* env, jobject thiz) {
	return getDistance();
}

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:     NativeUninitDriveBhLib();
 * private native static int  NativeUninitDriveBhLib();
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/motionDatas;B)D
 */
JNIEXPORT jdouble JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeUninitDriveBhLib(
		JNIEnv* env, jobject thiz) {
	return uninitDriveBhLib();
}

#ifdef __cplusplus
}
#endif
