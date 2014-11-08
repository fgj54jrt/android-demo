package com.cwits.cyx_drive_sdk.userInfo;

import android.content.Intent;

import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.ui.TestPage;
import com.cwits.cyx_drive_sdk.ui.cyx_RegisterActivity;
import com.netwise.ematch.RequestMapParam;
import com.netwise.ematch.WHBListener;

public class APPWHBListener implements WHBListener {

	@Override
	public boolean OnRequestMap(RequestMapParam param) {
		System.out.println("纬度"+param.getLatitude());
		System.out.println("经度"+param.getLongitude());
		System.out.println("位置相关信息"+param.getContent());
		CYX_Drive_SDK.getInstance().startWHBMap(param.getLatitude(),param.getLongitude(),param.getContent());
		return false;
	}
}

