#ifndef driveBhWrapper_h
#define driveBhWrapper_h
#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <stdio.h>
#include "driveBhLib.h"
#include "com_cwits_cyx_drive_test_libDriveBh.h"
#include "ClassGPSData.h"
#include "ClassSensorData.h"


#define LOG_TAG    "libDriveBh" // 这个是自定义的LOG的标识
//#undef LOG // 取消默认的LOG
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG, __VA_ARGS__)
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG, __VA_ARGS__)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG, __VA_ARGS__)
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG_TAG, __VA_ARGS__)

typedef struct _sensorData_t {
	int sensorType;     	//使用传感器的类型
	int count;				//*motionData所指向数据块的个数
	motionData_t *motionData; //motionData_t的指针
} sensorData_t;

void driveBhEvent_cpp(int eventType, gpsData_t *gpsData, sensorData_t *sensorData)
{
	JNIEnv *env = getJNIEnv();
	jclass clazz = env->FindClass("com/cwits/cyx_drive_sdk/data/DriveBhHandler");
	int i = 0;
	if(clazz == 0) {
//		class not found
		LOGI("DriveBhHandler class not found");
		exit(-1);
	}

	jmethodID mid = env->GetStaticMethodID(clazz,"driveBhEvent", "(ILcom/cwits/cyx_drive_sdk/libDriveBh/GPSData;Lcom/cwits/cyx_drive_sdk/libDriveBh/SensorData;)V");

	if(mid == 0) {
		//method not found
		LOGI("driveBhEvent method not found");
		exit(-1);
	}

	ClassGPSData gpsDataObject(env);
	if(gpsData == NULL) {
		LOGI("-------------  gpsData is null ");
	}
	if(sensorData == NULL) {
		LOGI("-------------  sensorData is null ");
	}
	if(gpsData != NULL) {
		gpsDataObject.SetTime(gpsData->time);
		gpsDataObject.SetDirection(gpsData->direction);
		gpsDataObject.SetSpeed(gpsData->speed);
		gpsDataObject.SetRadius(gpsData->radius);
		gpsDataObject.SetLongitude(gpsData->longitude);
		gpsDataObject.SetLatitude(gpsData->latitude);
		gpsDataObject.SetAltitude(gpsData->altitude);
	}
	ClassSensorData sensorDataObejct(env);
	if(sensorData != NULL) {
		sensorDataObejct.SetSensorType(sensorData->sensorType);
		sensorDataObejct.SetMotionData_t(sensorData->count, sensorData->motionData);
	}
	env->CallStaticVoidMethod(clazz, mid, eventType, gpsData==NULL?NULL:gpsDataObject.GetObject(), sensorData==NULL?NULL:sensorDataObejct.GetObject());

}

extern "C" void driveBhEvent(int eventType, gpsData_t *gpsData, sensorData_t *sensorData)
{
	driveBhEvent_cpp(eventType, gpsData, sensorData);
}

extern "C" int printLog(const char *format, ...)
{

	JNIEnv *env = getJNIEnv();
	jclass clazz = env->FindClass("com/cwits/cyx_drive_sdk/exception/LogUtil");
	if(clazz == 0) {
		//class not found
		LOGI("LogUtil class not found");
		exit(-1);
	}

	jmethodID mid = env->GetStaticMethodID(clazz,"printLog", "(ILjava/lang/String;Ljava/lang/String;)V");

	if(mid == 0) {
		//method not found
		LOGI("printLog method not found");
		exit(-1);
	}

	char printf_buf[1024] = {0};
	va_list args;
	int printed;

	va_start(args, format);
	printed = vsprintf(printf_buf, format, args);	//int vsprintf(char * str, const char * format, va_list ap); vsprintf()会根据参数format 字符串来转换并格式化数据, 然后将结果复制到参数str 所指的字符串数组, 直到出现字符串结束('\0')为止.
	va_end(args);

	jstring content = env->NewStringUTF( printf_buf);

	env->CallStaticVoidMethod(clazz, mid, 0, env->NewStringUTF( "libDriveBh"), content);

	return 0;
}
#endif
