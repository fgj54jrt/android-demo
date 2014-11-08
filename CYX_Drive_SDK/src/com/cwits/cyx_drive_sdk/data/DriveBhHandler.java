package com.cwits.cyx_drive_sdk.data;

import java.util.LinkedList;

import com.cwits.cyx_drive_sdk.libDriveBh.GPSData;
import com.cwits.cyx_drive_sdk.libDriveBh.SensorData;

public class DriveBhHandler {

	private static LinkedList<INotifyDriveBhListener> mlistenerList;
	
	public DriveBhHandler(){
		mlistenerList=new LinkedList<INotifyDriveBhListener>();
	}
	
	public static void driveBhEvent(int eventType, GPSData gpsData,
			SensorData sensorData) {
		// TODO Auto-generated method stub
		for (int i = 0; i < mlistenerList.size(); i++) {
			if(eventType!=0){
				mlistenerList.get(i).onDriveBhHappened(eventType, gpsData, sensorData);
			}else{
				return;
			}
		}
	}
	public boolean addNotifyDriveBhListener(INotifyDriveBhListener listener) {
		// TODO Auto-generated method stub
		return mlistenerList.add(listener);
	}

	public boolean removeNotifyDriveBhListener(INotifyDriveBhListener listener) {
		// TODO Auto-generated method stub
		return mlistenerList.remove(listener);
	}
	

}
