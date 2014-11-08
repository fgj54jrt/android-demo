#ifndef driveBhLib_h
#define driveBhLib_h
#include <time.h>

#ifdef __cplusplus
extern "C" {
;
#endif

typedef struct _parameter_t {
	char sensorSwitchArray[16];	 //使用什么传感器
	char sensorRateArray[16];	 //每个传感器对应的工作频率
	int intervalForHandler;	     //handler的调用周期，可以为空，默认值为1000ms
} parameter_t;

typedef struct _motionData_t {
	float x;	//x轴的数据
	float y;	//y轴的数据
	float z;	//z轴的数据
	int time;	//时间,当前时间相对于初始化时间的差值，单位为毫秒(ms)
} motionData_t;

typedef struct _gpsData_t {
	time_t time;		//时间
	float direction;	//方向,偏北方向，单位为度
	float speed;		//速度,单位为千米/小时(km/h)
	float radius;		//定位精度,单位为米(m)，数值为0时表示该数值无效
	double longitude;	//经度
	double latitude;	//纬度
	double altitude;	//海拔，单位为米(m)
} gpsData_t;

int getDriveBhParameter(parameter_t *p);	//获取驾驶行为采集库相关参数
int initDriveBhLib(char *availableSensor);	//通知驾驶行为采集开始
int accDataHandler(int count, motionData_t *motionData);	//线性加速度传感器数据处理
int gyroDataHandler(int count, motionData_t *motionData);	//陀螺仪传感器数据处理
int gravityDataHandler(int count, motionData_t *motionData);//重力传感器数据处理
int orientationDataHandler(int count, motionData_t *motionData);//方向传感器数据处理
int gpsDataHandler(int count, gpsData_t *gpsData);	//gps数据处理
double getDistance();	//返回当前里程数
int uninitDriveBhLib();	//通知驾驶行为采集结束

#ifdef __cplusplus
}
#endif
#endif
