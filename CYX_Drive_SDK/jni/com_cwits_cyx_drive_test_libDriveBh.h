#ifndef com_cwits_cyx_drive_sdk_libDriveBh_HeaderFile
#define com_cwits_cyx_drive_sdk_libDriveBh_HeaderFile

#ifdef __cplusplus
extern "C" {
#endif

/**
 * getJNIEnv
 */
JNIEnv* getJNIEnv();

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved);

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:    NativeGetDriveBhParameter
 * private native static int  NativeGetDriveBhParameter(DriveBhlibParameter parmeter);
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/AccData;Lcom/cwits/cyx_drive_sdk/libDriveBh/GPSData;Lcom/cwits/cyx_drive_sdk/libDriveBh/MagData;)S
 */
JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeGetDriveBhParameter
  (JNIEnv* env, jobject thiz, jobject parmeter);

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:    NativeInitDriveBhLib
 * private native static int  NativeInitDriveBhLib(char[] availableSensor);
 * Signature: (S)Z
 */
JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeInitDriveBhLib
  (JNIEnv* env, jobject thiz,jbyteArray availableSensor);

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:    NativeAccDataHandler(motionDatas[] motionDatass);
 * private native static int  NativeAccDataHandler(motionDatas[] motionDatass);
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeAccDataHandler
  (JNIEnv* env, jobject thiz,jobjectArray motionDatas);

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:     NativeGyroDataHandler(motionDatas[] motionDatass);
 * private native static int  NativeGyroDataHandler(motionDatas[] motionDatass);
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/motionDatas;B)D
 */
JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeGyroDataHandler
  (JNIEnv* env, jobject thiz, jobjectArray motionDatas);

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:    NativeGravityDataHandler(motionDatas[] motionDatass);
 * private native static int NativeGravityDataHandler(motionDatas[] motionDatass);
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/motionDatas;B)D
 */
JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeGravityDataHandler
  (JNIEnv* env, jobject thiz, jobjectArray motionDatas);

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:    NativeOrientationDataHandler(motionDatas[] motionDatass);
 * private native static int NativeOrientationDataHandler(motionDatas[] motionDatass);
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/motionDatas;B)D
 */
JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeOrientationDataHandler
  (JNIEnv* env, jobject thiz, jobjectArray motionDatas);

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:     NativeGpsDataHandler(motionDatas[] motionDatass);
 * private native static int  NativeGpsDataHandler(motionDatas[] motionDatass);
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/motionDatas;B)D
 */
JNIEXPORT jint JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeGpsDataHandler
  (JNIEnv* env, jobject thiz, jobjectArray gpsData);

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:     NativeGpsDataHandler();
 * private native static double  NativeGpsDataHandler();
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/motionDatas;B)D
 */
JNIEXPORT jdouble JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeGetDistance
  (JNIEnv* env, jobject thiz);

/*
 * Class:     com_cwits_cyx_drive_sdk_libDriveBh_DriveBh
 * Method:     NativeUninitDriveBhLib();
 * private native static int  NativeUninitDriveBhLib();
 * Signature: (Lcom/cwits/cyx_drive_sdk/libDriveBh/motionDatas;B)D
 */
JNIEXPORT jdouble JNICALL Java_com_cwits_cyx_1drive_1sdk_libDriveBh_DriveBh_NativeUninitDriveBhLib
  (JNIEnv* env, jobject thiz);
#ifdef __cplusplus
}
#endif
#endif // com_cwits_cyx_drive_sdk_libDriveBh_HeaderFile
