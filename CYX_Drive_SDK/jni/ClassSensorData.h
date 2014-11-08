#ifndef ClassSensorData_HeaderFile
#define ClassSensorData_HeaderFile
#include "driveBhLib.h"
class ClassSensorData {
public:
	JNIEnv* mEnv;
	jclass mClass;
	jobject mThizObj;
public:
	jfieldID mfIDsensorType; 	//使用传感器的类型
	jint msensorType;
	jfieldID mfIDcount;			//*motionData所指向数据块的个数
	jint mcount;
	jfieldID mfIDmotionData_t;	//motionData_t的指针
	jobject mmotionData_t;
public:
	ClassSensorData(JNIEnv* env);
	~ClassSensorData();
	void SetObject(jobject obj);
	jobject GetObject() {
		return mThizObj;
	};
	int SetSensorType(int sensorType);
	int SetMotionData_t(int count, motionData_t *motionData_t);
};

#endif
