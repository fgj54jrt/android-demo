#include <jni.h>
#include "ClassGPSData.h"


ClassGPSData::ClassGPSData(JNIEnv* env)
:  mEnv(env), mThizObj(0)
{
	/* Get the class definition */
	mClass = env->FindClass("com/cwits/cyx_drive_sdk/libDriveBh/GPSData");

	if(mClass)
	{
		// 创建Java类CarMaintainingData的对象
		jmethodID midConstructor = mEnv->GetMethodID(mClass, "<init>", "()V");
		if (midConstructor)
		{
			mThizObj = mEnv->NewObject(mClass, midConstructor);

			// 获得属性：time的ID
			mfIDtime = env->GetFieldID(mClass, "time", "J");
			// 获得属性：direction的ID
			mfIDdirection = env->GetFieldID(mClass, "direction", "F");
			// 获得属性：speed的ID
			mfIDspeed = env->GetFieldID(mClass, "speed", "F");
			// 获得属性：radius的ID
			mfIDradius = env->GetFieldID(mClass, "radius", "F");
			// 获得属性：longituge的ID
			mfIDlongitude = env->GetFieldID(mClass, "longitude", "D");
			// 获得属性：latitude的ID
			mfIDlatitude = env->GetFieldID(mClass, "latitude", "D");
			//获得属性：altitude的ID
			mfIDaltitude = env->GetFieldID(mClass,"altitude","D");
		}
	}
}

ClassGPSData::~ClassGPSData(void)
{
	if(mThizObj && mEnv)
		mEnv->DeleteLocalRef(mThizObj), mThizObj = 0;

	if(mClass && mEnv)
		mEnv->DeleteLocalRef(mClass), mClass = 0;
}

void ClassGPSData::SetObject(jobject obj)
{
	if(mThizObj && mEnv)
		mEnv->DeleteLocalRef(mThizObj), mThizObj = 0;

	if(mEnv && obj)
	{
		mThizObj = mEnv->NewLocalRef(obj);

		mtime = mEnv->GetLongField(mThizObj, mfIDtime);
		mdirection = mEnv->GetFloatField(mThizObj, mfIDdirection);
		mspeed = mEnv->GetFloatField(mThizObj, mfIDspeed);
		mradius = mEnv->GetFloatField(mThizObj, mfIDradius);
		mlongitude = mEnv->GetDoubleField(mThizObj, mfIDlongitude);
		mlatitude = mEnv->GetDoubleField(mThizObj, mfIDlatitude);
		maltitude = mEnv->GetDoubleField(mThizObj, mfIDaltitude);

	}
}

int ClassGPSData::SetTime(long time)
{
	mEnv->SetLongField(mThizObj, mfIDtime, time);
	return 0;
}

int ClassGPSData::SetDirection(float direction)
{
	mEnv->SetFloatField(mThizObj, mfIDdirection, direction);
	return 0;
}

int ClassGPSData::SetSpeed(float speed)
{
	mEnv->SetFloatField(mThizObj, mfIDspeed, speed);
	return 0;
}

int ClassGPSData::SetRadius(float radius)
{
	mEnv->SetFloatField(mThizObj, mfIDradius, radius);
	return 0;
}

int ClassGPSData::SetLongitude(double longitude)
{
	mEnv->SetDoubleField(mThizObj, mfIDlongitude, longitude);
	return 0;
}

int ClassGPSData::SetLatitude(double latitude)
{
	mEnv->SetDoubleField(mThizObj, mfIDlatitude, latitude);
	return 0;
}

int ClassGPSData::SetAltitude(double altitude)
{
	mEnv->SetDoubleField(mThizObj, mfIDaltitude, altitude);
	return 0;
}
