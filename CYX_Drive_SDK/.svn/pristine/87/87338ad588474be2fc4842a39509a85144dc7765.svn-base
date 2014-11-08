package com.cwits.cyx_drive_sdk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

	public static boolean getNetWorkState(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info =  connectivityManager.getActiveNetworkInfo();
		if(info == null || !info.isAvailable()) 
			return false;
		return true;
	}
	
}
