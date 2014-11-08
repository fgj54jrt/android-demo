package com.cwits.cyx_drive_sdk.bean;

import java.io.Serializable;

/**
 * 查询行程历史时返回的行程数据结果对象
 * @author lxh
 *
 */
public class StrokeResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String stroke;				// 行程编号
	private String begin;				// 行程开始时间Datetime格式yyyy-MM-ddHH:mm:ss
	private String end;					// 行程结束时间Datetime格式yyyy-MM-ddHH:mm:ss
	private int times;					// 行程时间长度Int
	private double mileages;			// 行程里程，单位m
	private int endstate;				// 行程结束标志1：结束0：未结
	private double startlon;			// 起点经度
	private double startlat;			// 起点纬度
	private double endlon;				// 钟点经度
	private double endlat;				// 终点纬度	
	private float ave;					// 平均速度Km/h
	private float max;					// 最高时速Km/h
	private int dece;					// 急减速次数
	private int aece;					// 急加速次数
	private int turns;					// 急转弯次数
	private int speeds;					// 超速
	private int tires;					// 疲劳驾驶
	private int changes = 0;			// 急变道
	private String startAddName;	    //起点中文名称
	private String endAddName;	        //终点中文名称
	public String getStroke() {
		return stroke;
	}
	public void setStroke(String stroke) {
		this.stroke = stroke;
	}
	public String getBegin() {
		return begin;
	}
	public void setBegin(String begin) {
		this.begin = begin;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public double getMileages() {
		return mileages;
	}
	public void setMileages(double mileages) {
		this.mileages = mileages;
	}
	public int getEndstate() {
		return endstate;
	}
	public void setEndstate(int endstate) {
		this.endstate = endstate;
	}
	public double getStartlon() {
		return startlon;
	}
	public void setStartlon(double startlon) {
		this.startlon = startlon;
	}
	public double getStartlat() {
		return startlat;
	}
	public void setStartlat(double startlat) {
		this.startlat = startlat;
	}
	public double getEndlon() {
		return endlon;
	}
	public void setEndlon(double endlon) {
		this.endlon = endlon;
	}
	public double getEndlat() {
		return endlat;
	}
	public void setEndlat(double endlat) {
		this.endlat = endlat;
	}
	public float getAve() {
		return ave;
	}
	public void setAve(float ave) {
		this.ave = ave;
	}
	public float getMax() {
		return max;
	}
	public void setMax(float max) {
		this.max = max;
	}
	public int getAece() {
		return aece;
	}
	public void setAece(int aece) {
		this.aece = aece;
	}
	public int getDece() {
		return dece;
	}
	public void setDece(int dece) {
		this.dece = dece;
	}
	public int getTurns() {
		return turns;
	}
	public void setTurns(int turns) {
		this.turns = turns;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getStartAddName() {
		return startAddName;
	}
	public void setStartAddName(String startAddName) {
		this.startAddName = startAddName;
	}
	public String getEndAddName() {
		return endAddName;
	}
	public void setEndAddName(String endAddName) {
		this.endAddName = endAddName;
	}
	public int getSpeeds() {
		return speeds;
	}
	public void setSpeeds(int speeds) {
		this.speeds = speeds;
	}
	public int getTires() {
		return tires;
	}
	public void setTires(int tires) {
		this.tires = tires;
	}
	public int getChanges() {
		return changes;
	}
	public void setChanges(int changes) {
		this.changes = changes;
	}
	
	
	
}
