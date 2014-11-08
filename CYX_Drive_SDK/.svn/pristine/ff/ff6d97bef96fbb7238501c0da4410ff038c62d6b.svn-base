#ifndef ClassGPSData_HeaderFile
#define ClassGPSData_HeaderFile

class ClassGPSData
{
public:
	JNIEnv* mEnv;
	jclass mClass;
	jobject mThizObj;

public:
	jfieldID mfIDtime;
	jlong mtime;
	jfieldID mfIDdirection;		//方向,偏北方向，单位为度
	jfloat mdirection;
	jfieldID mfIDspeed;			//速度,单位为千米/小时(km/h)
	jfloat mspeed;
	jfieldID mfIDradius;		//定位精度,单位为米(m)
	jfloat mradius;
	jfieldID mfIDlongitude;		//经度
	jdouble mlongitude;
	jfieldID mfIDlatitude;		//纬度
	jdouble mlatitude;
	jfieldID mfIDaltitude;		//海拔，单位为米(m)
	jdouble maltitude;

public:
	ClassGPSData(JNIEnv* env);
	~ClassGPSData();

	void SetObject(jobject obj);
		jobject GetObject() { return mThizObj;};

	int SetTime(long time);
	int SetDirection(float direction);
	int SetSpeed(float speed);
	int SetRadius(float radius);
	int SetLongitude(double longitude);
	int SetLatitude(double latitude);
	int SetAltitude(double altitude);
};

#endif // ClassGPSData_HeaderFile

