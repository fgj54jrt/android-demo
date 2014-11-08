package com.cwits.cyx_drive_sdk.connection;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChangedReceiver extends BroadcastReceiver {
	private static long lastConnectedTime = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			Log.i("NetworkChangedReceiver", "CONNECTIVITY_ACTION");
			ConnectivityManager connMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo[] networkInfoArr = connMgr.getAllNetworkInfo();
			boolean isNetworkConnected = false;

			for (int i = 0; i < networkInfoArr.length; ++i) {
				if (networkInfoArr[i].isConnected()) {
					isNetworkConnected = true;
					Log.i("NetworkChangedReceiver", "The network connection: "
							+ networkInfoArr[i].getTypeName()
							+ " is connected.");
				}
			}

			if (isNetworkConnected) {
				Log.i("NetworkChangedReceiver",
						"There is at least one networ connected.");
				
				long currTime = new Date().getTime();
				
				if(currTime - lastConnectedTime > 3*1000){
					lastConnectedTime = currTime;
					Connection.getInstance().onNetworkOK();
				}else{
					Log.i("NetworkChangedReceiver",
							"The time is too short after last NetworkConnected Event, do not notify the connectioin.");
				}
			}
		}

	}

}
