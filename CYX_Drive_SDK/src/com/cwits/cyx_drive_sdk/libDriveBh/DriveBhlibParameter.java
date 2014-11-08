package com.cwits.cyx_drive_sdk.libDriveBh;

/**
 * 驾驶行为采集库参数
 * @author lxh
 *
 */
public class DriveBhlibParameter {

	public byte[]  sensorSwitchArray;	   // 要使用的传感器有哪些
	public byte[]  sensorRateArray;	       // 每个传感器对应的工作频率
	public int     intervalForHandler;     //handler的调用周期，可以为空，默认值为1000ms
    
}
