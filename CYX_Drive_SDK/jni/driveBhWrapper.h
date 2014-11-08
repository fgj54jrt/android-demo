#ifndef driveBhWrapper_h
#define driveBhWrapper_h
#include "driveBhLib.h"

#ifdef __cplusplus
extern "C" {
;
#endif

typedef struct _sensorData_t {
	int sensorType;     	//使用传感器的类型
	int count;				//*motionData所指向数据块的个数
	motionData_t *motionData; //motionData_t的指针
} sensorData_t;

void driveBhEvent(int eventType, gpsData_t *gpsData, sensorData_t *sensorData); //通知驾驶行为产生
int printLog(const char *format,...);	//打印库方法里的信息

#ifdef __cplusplus
}
#endif
#endif
