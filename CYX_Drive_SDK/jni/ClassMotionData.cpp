#include <jni.h>
#include "ClassMotionData.h"

ClassMotionData::ClassMotionData(JNIEnv* env) :
		mEnv(env), mThizObj(0) {
	/* Get the class definition */
	mClass = env->FindClass("com/cwits/cyx_drive_sdk/libDriveBh/MotionData");
	if (mClass) {
		// 创建Java类CarMaintainingData的对象
		jmethodID midConstructor = mEnv->GetMethodID(mClass, "<init>", "()V");
		if (midConstructor) {
			mThizObj = mEnv->NewObject(mClass, midConstructor);

			//"Z boolean","B byte","C char","S short","I int",J long","F float","D double","L fully-qualified-class ; fully-qualified-class"
			// 获得属性：mx的ID
			mfIDx = env->GetFieldID(mClass, "mx", "F");
			// 获得属性：my的ID
			mfIDy = env->GetFieldID(mClass, "my", "F");
			// 获得属性：mz的ID
			mfIDz = env->GetFieldID(mClass, "mz", "F");
			//获得属性：time的ID
			mfIDtime = env->GetFieldID(mClass, "time","I");

		}
	}
}

ClassMotionData::~ClassMotionData(void) {
	if (mThizObj && mEnv)
		mEnv->DeleteLocalRef(mThizObj), mThizObj = 0;

	if (mClass && mEnv)
		mEnv->DeleteLocalRef(mClass), mClass = 0;
}
void ClassMotionData::SetObject(jobject obj) {
	if (mThizObj && mEnv)
		mEnv->DeleteLocalRef(mThizObj), mThizObj = 0;

	if (mEnv && obj) {
		mThizObj = mEnv->NewLocalRef(obj);
		mx = mEnv->GetFloatField(mThizObj, mfIDx);
		my = mEnv->GetFloatField(mThizObj, mfIDy);
		mz = mEnv->GetFloatField(mThizObj, mfIDz);
		mtime = mEnv->GetIntField(mThizObj, mfIDtime);

	}
}

int ClassMotionData::SetX(float x)
{
	mEnv->SetFloatField(mThizObj, mfIDx, x);
	mx = x;
	return 0;
}

int ClassMotionData::SetY(float y)
{
	mEnv->SetFloatField(mThizObj, mfIDy, y);
	my = y;
	return 0;
}

int ClassMotionData::SetZ(float z)
{
	mEnv->SetFloatField(mThizObj, mfIDz, z);
	mz = z;
	return 0;
}

int ClassMotionData::SetTime(int time)
{
	mEnv->SetIntField(mThizObj, mfIDtime, time);
	mtime = time;
	return 0;
}
