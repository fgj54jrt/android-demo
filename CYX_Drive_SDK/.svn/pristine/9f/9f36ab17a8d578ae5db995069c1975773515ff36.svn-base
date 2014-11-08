package com.cwits.cyx_drive_sdk.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION = "android.intent.action.BOOT_COMPLETED";   
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			Log.i("CWCEX", "Received boot broadcast");
			Intent bootIntent = new Intent(context, cyx_XMPPService.class);
			context.startService(bootIntent);
		}
	}

}
