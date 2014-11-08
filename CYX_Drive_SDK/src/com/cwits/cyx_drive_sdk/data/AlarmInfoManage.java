package com.cwits.cyx_drive_sdk.data;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.cwits.cyx_drive_sdk.bean.AlarmData;
import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.ui.MResource;
import com.cwits.cyx_drive_sdk.ui.cyx_FortifyAlarmActivity;
import com.cwits.cyx_drive_sdk.ui.cyx_MyApplication;

public class AlarmInfoManage implements IAlarmInfoManage {

	private LinkedList<IAlarmManageListener> mAlarmManageListener;
	private MediaPlayer mediaPlayer;
	private int m_Priority = 0;

	/**
	 * 构造方法
	 */
	public AlarmInfoManage() {

		mAlarmManageListener = new LinkedList<IAlarmManageListener>();
	}

	public int receivedAlarm(AlarmData alarmdata) {

		int result = -1;
		Context context = CYX_Drive_SDK.getSavedContext();
		addInfo(alarmdata);
		cyx_MyApplication.getAlarmList().add(alarmdata);
		CYX_Drive_SDK.getInstance().getConnection().sendExtData(new ExtraDataProcess()
		.getAlarmResponse(alarmdata.getUserId(), "1"),
				new RequestCallback(){
			@Override
			public void onSuccess(String bizJsonData) {
				System.out.println("--------发送应答成功");
			}
			@Override
			public void onFailed(int reason) {
				// TODO Auto-generated method stub
				super.onFailed(reason);
			}
		});
		if (cyx_MyApplication.getAlarmList().size() == 0) {// 当前没有历史的报警信息
			m_Priority = alarmdata.getPriority();
			handleAlarm(alarmdata, context);
			result = 0;
		} else {
			// 新来的报警优先级低于正在处理的报警
			if (m_Priority >= alarmdata.getPriority()) {
				handleAlarm(alarmdata, context);
				result = 1;
			} else {
				// 新来报警优先级高于正在处理的报警
				// 停止当前声音，播放新报警声音
				OnKeyCancel();
				handleAlarm(alarmdata, context);
				m_Priority = alarmdata.getPriority();
				result = 2;
			}
			
		}

		return result;
	}

	private void addInfo(AlarmData alarmdata) {
		// 将数据存到数据库

	}
	
	// 处理报警
    private void handleAlarm(AlarmData alarm,Context context){
    	int type = alarm.getAlarmType();
    	Log.i("lxh", "------------alarm type"+alarm.getAlarmType());
    	if (AlarmType.FOTIFY_ALARM == type) {// 设防报警
			// 播放设防报警声音
//			PlayerFortifyMedia(context);
			intentToAlarm(context);
		} else if(AlarmType.CRASH_ALARM == type
				|| AlarmType.ROLLOVER_ALARM == type){
			// 侧翻和碰撞
//			PlayerECallMedia(context);
			intentToAlarm(context);
		}else if(AlarmType.OVERSPEED_ALARM == type){
			//超速报警
//			playAlarm(context, "overspeedalarm");
		}else if(AlarmType.TIRED_DRIVE_ALARM==type){
			//疲劳报警
//			playAlarm(context, "fatiguedrivingalarm");
		}else if(AlarmType.SLOW_DOWM_ALARM==type){
			//急减速报警
//			playAlarm(context, "sloweddownalarm");
		}else if(AlarmType.SPEED_UP_ALARM==type){
			//急加速报警
//			playAlarm(context, "speedupalarm");
		}else if(AlarmType.SUDDEN_TURN_ALARM==type){
			//急转弯报警
//			playAlarm(context, "quickturnalarm");
		}
    }
	/**
	 * 播放设防报警声音
	 */
	private void PlayerFortifyMedia(Context context) {
		mediaPlayer = MediaPlayer.create(context, MResource.getRawID(context,"alarm"));
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
	}

	/**
	 * 播放紧急报警声音
	 */
	private void PlayerECallMedia(Context context) {
		mediaPlayer = MediaPlayer.create(context,  MResource.getRawID(context,"alarm2"));
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
	}
    private void playAlarm(Context context,String alarmName){
    	mediaPlayer = MediaPlayer.create(context,  MResource.getRawID(context,alarmName));
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
    }
	// private void
	private void OnKeyCancel() {
		if (null != mediaPlayer) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}

	}
    
	@Override
	public List<AlarmData> getAlarmList() {
		return cyx_MyApplication.getAlarmList();
	}

	@Override
	public boolean removeAlarmList(int position) {
		cyx_MyApplication.getAlarmList().remove(position);
		return true;
	}

	@Override
	public boolean addIAlarmManageListener(IAlarmManageListener listener) {
		return mAlarmManageListener.add(listener);
	}

	@Override
	public boolean removeIAlarmManageListener(IAlarmManageListener listener) {
		return mAlarmManageListener.remove(listener);
	}

	@Override
	public boolean CancelMediaPlayer() {
		OnKeyCancel();
		return false;
	}

	@Override
	public boolean removeAllAlarmList() {
		cyx_MyApplication.getAlarmList() .clear();
		return true;
	}

	@Override
	public boolean getMediaPlayerStatus() {
		boolean ret = false;
		if (null != mediaPlayer && mediaPlayer.isPlaying()) {
			ret = true;
		}
		return ret;
	}

	@Override
	public void stopMediaplay() {
		if (null != mediaPlayer && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
	}

	@Override
	public void resumeMediaplay() {
		if (null != mediaPlayer) {
			if (mediaPlayer.isPlaying())
				return;
			mediaPlayer.start();
		}

	}
	private void intentToAlarm(Context context){
		Intent it = new Intent();
		// Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
		//防止因为后台运行报警完之后通过后台任务进入程序时只弹出警告框但无数据的问题
		it.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		it.setClass(context.getApplicationContext(),
				cyx_FortifyAlarmActivity.class);
		context.getApplicationContext().startActivity(it);
	}
}
