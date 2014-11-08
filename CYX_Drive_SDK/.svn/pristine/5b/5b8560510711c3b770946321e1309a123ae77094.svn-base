package com.cwits.cyx_drive_sdk.libDriveBh;

public class DriveBh {
	public final static int HARDBRAK = 4;               //急刹车
	public final static int HARDACCL = 5;               //急加速
	public final static int HARDTURN = 6;               //急转弯
	public final static int OVERSPEED = 7;			//超速
	public final static int FATIGUEDRIVING = 8;	//疲劳驾驶	
	public final static int SNAP = 9;		//急变道
	
	public final static int SENSOR_SLOW = 0;     				// 传感器频率为20HZ左右(或20HZ以下)
	public final static int SENSOR_NORMAL = 1;   				// 传感器频率为 40HZ左右
	public final static int SENSOR_FASTER = 2;   				// 传感器频率为60HZ左右
	public final static int SENSOR_FASTEST = 3;  				// 传感器频率为最快的频率
	
	public final static int EQUIVALENT_TO_DEFINE = 0;	//与设定的相等
	public final static int FASTER_THEN_DEFINE = 1;		//比设定的要快
	public final static int SLOWER_THEN_DEFINE = 2;			//比设定的要慢
	    
	public final static int SENSOR_OPEN = 1;     				// 传感器开启
	public final static int SENSOR_CLOSE = 0;    				// 传感器关闭
	    
	public final static int RESULT_SUCCEED = 0;  				// 方法执行结果为成功
	public final static int RESULT_ERROR = 1;    				// 方法执行失败
	
	public final static int SENSOR_EXIST = 1;					//传感器存在
	public final static int SENSOR_NONEXISTENCE = 0;	//传感器不存在
	
	
	private native static int  NativeGetDriveBhParameter(DriveBhlibParameter parmeter);
	private native static int  NativeInitDriveBhLib(byte[] availableSensor);
	private native static int  NativeAccDataHandler(MotionData[] motionDatas);
	private native static int  NativeGyroDataHandler(MotionData[] motionDatas);
	private native static int  NativeGravityDataHandler(MotionData[] motionDatas);
	private native static int  NativeOrientationDataHandler(MotionData[] motionDatas);
	private native static int  NativeGpsDataHandler(GPSData[] gpsDatas);
	private native static double NativeGetDistance();
	private native static int  NativeUninitDriveBhLib();
	
	//加载lib 
	static {
		System.loadLibrary("DriveBh");
	}

	/**
	 * 获取驾驶行为采集库参数
	 * @param parmeter
	 * @return 方法执行结果
	 */
	public static  int getDriveBhParameter(DriveBhlibParameter parmeter){
		return NativeGetDriveBhParameter(parmeter);
//		return 0;
	}
	
	/**
	 * 通知驾驶行为采集开始
	 * @param availableSensor
	 * @return
	 */
	public static int initDriveBhLib(byte[] availableSensor){
		return NativeInitDriveBhLib(availableSensor);
//		return 0;
	}
	
	/**
	 * 处理线性加速度传感器数据
	 * @param motionData 
	 * @return
	 */
	public static int accDataHandler(MotionData[] motionDatas){
		return NativeAccDataHandler(motionDatas);
//		return 0;
	}
	
	/**
	 * 处理陀螺仪数据
	 * @param motionDatas
	 * @return
	 */
	public static int gyroDataHandler(MotionData[] motionDatas){
		return NativeGyroDataHandler(motionDatas);
//		return 0;
	}
	
	/**
	 * 处理重力传感器数据
	 * @param motionDatas
	 * @return
	 */
	public static int gravityDataHandler(MotionData[] motionDatas){
		return NativeGravityDataHandler(motionDatas);
//		return 0;
	}
	
	/**
	 * 处理方向传感器数据
	 * @return
	 */
	public static int orientationDataHandler(MotionData[] motionDatas){
		return NativeOrientationDataHandler(motionDatas);
		//return 0;
	}
	
	/**
	 * 处理Gps数据
	 * @param gpsDatas
	 * @return
	 */
	public static int gpsDataHandler(GPSData[] gpsDatas){
		return NativeGpsDataHandler(gpsDatas);
		//return 0;
	}
	
	/**
	 * 获取当前里程
	 * @return
	 */
	public static double getDistance(){
		return NativeGetDistance();
//		return 0;
	}
	/**
	 * 通知驾驶行为采集结束
	 * @return 方法执行结果0 为成功， 1为失败
	 */
	public static int uninitDriveBhLib(){
		return NativeUninitDriveBhLib();
//		return 0;
	}
}
