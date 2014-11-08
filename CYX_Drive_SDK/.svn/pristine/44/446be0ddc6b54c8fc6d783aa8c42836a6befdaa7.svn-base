package com.cwits.cyx_drive_sdk.data;

import com.cwits.cyx_drive_sdk.libDriveBh.GPSData;
import com.cwits.cyx_drive_sdk.libDriveBh.SensorData;

/**
 * 通知驾驶行为事件产生的监听
 * @author lxh
 *
 */
public interface INotifyDriveBhListener {

	/**
	 * 当产生驾驶行为时该方法被调用
	 * @param eventType   驾驶行为事件类型
	 * @param gpsData     驾驶行为发生时相关的gps数据
	 * @param sensorData  分析产生的驾驶行为所用的传感器相关数据
	 */
	public void onDriveBhHappened(int eventType, GPSData gpsData,SensorData sensorData);
}
