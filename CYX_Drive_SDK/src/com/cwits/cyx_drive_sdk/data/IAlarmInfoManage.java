package com.cwits.cyx_drive_sdk.data;

import java.util.List;

import com.cwits.cyx_drive_sdk.bean.AlarmData;

public interface IAlarmInfoManage {
	
	List<AlarmData> getAlarmList();
	
	boolean removeAlarmList(int position);
	
	public boolean addIAlarmManageListener(IAlarmManageListener listener);
	
	public boolean removeIAlarmManageListener(IAlarmManageListener listener);
	
	public boolean CancelMediaPlayer();
	
	public boolean removeAllAlarmList();
	
	public boolean getMediaPlayerStatus();
	
	public void stopMediaplay();
	
	public void resumeMediaplay();
	
	public int receivedAlarm(AlarmData alarm);
}
