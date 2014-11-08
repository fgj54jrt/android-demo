#ifndef ClassMotionData_HeaderFile
#define ClassMotionData_HeaderFile

class ClassMotionData {
public:
	JNIEnv* mEnv;
	jclass mClass;
	jobject mThizObj;
public:
	jfieldID mfIDx;
	jfloat mx;
	jfieldID mfIDy;
	jfloat my;
	jfieldID mfIDz;
	jfloat mz;
	jfieldID mfIDtime;
	jint mtime;
public:
	ClassMotionData(JNIEnv* env);
	~ClassMotionData();
	void SetObject(jobject obj);
	jobject GetObject() {
		return mThizObj;
	};

	int SetX(float x);
	int SetY(float y);
	int SetZ(float z);
	int SetTime(int time);
};

#endif // ClassMotionData_HeaderFile
