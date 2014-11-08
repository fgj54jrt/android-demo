#include <jni.h>
#include "ClassSensorData.h"
#include "ClassMotionData.h"
#include <android/log.h>
#define LOG_TAG    "libDriveBh" // 这个是自定义的LOG的标识
//#undef LOG // 取消默认的LOG
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG, __VA_ARGS__)

ClassSensorData::ClassSensorData(JNIEnv* env)
	:  mEnv(env), mThizObj(0)
{
	/* Get the class definition */
	mClass = env->FindClass("com/cwits/cyx_drive_sdk/libDriveBh/SensorData");

	if(mClass)
	{
		// 创建Java类CarMaintainingData的对象
		jmethodID midConstructor = mEnv->GetMethodID(mClass, "<init>", "()V");
		if (midConstructor)
		{
			mThizObj = mEnv->NewObject(mClass, midConstructor);

			// 获得属性：sensorType的ID
			mfIDsensorType = env->GetFieldID(mClass, "sensorType", "I");

			// 获得属性：motionDatas的ID
			mfIDmotionData_t = env->GetFieldID(mClass, "motionDatas", "[Lcom/cwits/cyx_drive_sdk/libDriveBh/MotionData;");
		}
	}
}


ClassSensorData::~ClassSensorData(void)
{
	if(mThizObj && mEnv)
		mEnv->DeleteLocalRef(mThizObj), mThizObj = 0;

	if(mClass && mEnv)
		mEnv->DeleteLocalRef(mClass), mClass = 0;
}

void ClassSensorData::SetObject(jobject obj)
{
	if(mThizObj && mEnv)
		mEnv->DeleteLocalRef(mThizObj), mThizObj = 0;

	if(mEnv && obj)
	{
		mThizObj = mEnv->NewLocalRef(obj);

		msensorType = mEnv->GetIntField(mThizObj, mfIDsensorType);
		mmotionData_t = (jobject)mEnv->GetObjectField(mThizObj, mfIDmotionData_t);
	}
}

int ClassSensorData::SetSensorType(int sensorType)
{
	mEnv->SetIntField(mThizObj, mfIDsensorType, sensorType);
	return 0;
}

int ClassSensorData::SetMotionData_t(int count, motionData_t *motionData_t)
{
	int i = 0;
	jclass motionDataCalss = mEnv->FindClass("com/cwits/cyx_drive_sdk/libDriveBh/MotionData");
	jobjectArray motionDatas = mEnv->NewObjectArray(count, motionDataCalss, NULL);

	for (i = 0; motionData_t && i < count; ++i) {

		ClassMotionData motionDatasObj(mEnv);
		motionDatasObj.SetX(motionData_t->x);
		motionDatasObj.SetY(motionData_t->y);
		motionDatasObj.SetZ(motionData_t->z);
		motionDatasObj.SetTime(motionData_t->time);

		mEnv->SetObjectArrayElement(motionDatas, i, motionDatasObj.GetObject());
	}
	mEnv->SetObjectField(mThizObj, mfIDmotionData_t, motionDatas);

	return 0;
}
