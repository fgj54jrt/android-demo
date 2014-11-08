#ifndef ClassDriveBhlibParameter_HeaderFile
#define ClassDriveBhlibParameter_HeaderFile

class ClassDriveBhlibParameter {
public:
	JNIEnv* mEnv;
	jclass mClass;
	jobject mThizObj;
public:
	jfieldID mfIDsensorSwitchArray;	//使用什么传感器
	jbyteArray msensorSwitchArray;
	jfieldID mfIDsensorRateArray;	//每个传感器对应的工作频率
	jbyteArray msensorRateArray;
	jfieldID mfIDintervalForHandler;//handler的调用周期
	jint mintervalForHandler;
public:
	ClassDriveBhlibParameter(JNIEnv* env);
	~ClassDriveBhlibParameter();
	void SetObject(jobject obj);
	jobject GetObject() { return mThizObj; };

	int SetSensorSwitchArray(char sensorSwitchArray[16]);
	int SetSensorRateArray(char sensorRateArray[16]);
	int SetIntervalForHandler(int intervalForHandler);
};

#endif

