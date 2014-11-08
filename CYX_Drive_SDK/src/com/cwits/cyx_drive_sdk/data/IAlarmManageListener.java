package com.cwits.cyx_drive_sdk.data;

import java.util.List;

import com.cwits.cyx_drive_sdk.bean.AlarmData;

public interface IAlarmManageListener {
	
	public void OnAlarmListUpdate(IAlarmInfoManage obj,List<AlarmData> alist);
	
	public void OnAlarmCancel(IAlarmInfoManage obj,int position);
	
}
