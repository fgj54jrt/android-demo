package com.cwits.cyx_drive_sdk.util;

public class LocationFilter {
//	private static final String TAG = "LocationFilter";
	
	public static boolean isAvailableLocatioin(
			double current_lat,double current_lon,double radius, double last_lat,double last_lon) {
		boolean isAvailableLocatioin=false;
		if(last_lat==-1&&last_lon==-1){
			isAvailableLocatioin=true;
//			System.out.println("-------isAvailableLocatioin:"+isAvailableLocatioin);
			return isAvailableLocatioin;
		}
		double distance = JourneyTool.getDistance(current_lat, current_lon,last_lat,last_lon);
		boolean flag = true;
		
		if(distance > 1.5/1000 && flag) {	//距离大于1.5m并且精度小于10m
			isAvailableLocatioin=true;
		}
		return isAvailableLocatioin;
	}
	
}
