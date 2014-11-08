package com.cwits.cyx_drive_sdk.bean;

import java.io.Serializable;

/**
 * 设防报警数据封装类
 * @param username 	收到报警信息的账号
 * @param priority 	报警优先级
 * @param alarmType 报警类型
 * @param phone		ECall报警时自动拨打的电话号码
 * @param accStatus	设防撤防报警的状态（包括点火报警和振动报警）
 * @param fixTime	报警时间
 */
public class AlarmData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*用户ID*/
	public String userId;
	/*报警优先级*/
	public int priority;
	/*报警类型*/
	public int alarmType;
	/*acc状态，1：点火报警，0 :震动报警*/
	public int accStatus;
	/*报警发生时间*/
	public String fixTime;
	
	public AlarmData(){
		
	}
	
	public AlarmData(String userId, int priority, int alarmType,
			int accStatus, String fixTime) {
		super();
		this.userId = userId;
		this.priority = priority;
		this.alarmType = alarmType;
		this.accStatus = accStatus;
		this.fixTime = fixTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}

	public int getAccStatus() {
		return accStatus;
	}

	public void setAccStatus(int accStatus) {
		this.accStatus = accStatus;
	}

	public String getFixTime() {
		return fixTime;
	}

	public void setFixTime(String fixTime) {
		this.fixTime = fixTime;
	}
	
}
