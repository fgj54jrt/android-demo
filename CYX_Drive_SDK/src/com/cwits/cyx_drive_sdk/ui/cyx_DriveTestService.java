package com.cwits.cyx_drive_sdk.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.cwits.cyx_drive_sdk.exception.LogUtil;
import com.cwits.cyx_drive_sdk.libDriveBh.DriveBh;
import com.cwits.cyx_drive_sdk.libDriveBh.DriveBhlibParameter;
import com.cwits.cyx_drive_sdk.libDriveBh.GPSData;
import com.cwits.cyx_drive_sdk.libDriveBh.MotionData;
import com.cwits.cyx_drive_sdk.util.LocationFilter;

public class cyx_DriveTestService extends Service implements BDLocationListener{
	private static final String TAG = "cyx_DriveTestService";
	private static LinkedList<IServiceEndListener> mListener = null;
	private MySensorEventListener mySensorEventListener = null;	//传感器监听器
	// 传感器相"
	private Sensor mLineAcceSensor = null; // 线"加"度传感器
	private Sensor mGravitySensor = null; // 重力传感"
	private Sensor mGyroscopSensor = null; // "��"
	private Sensor mOrientationSensor = null; // 方向感应"
	private SensorManager mSensorManager = null; // 传感器管理类
	
	private LocationManager locationManager;
	double latitude = -1;
	double longitude = -1;
		
	// 装数据的list和数"
	private List<MotionData> mLineAcceList;
	private List<MotionData> mGravityList;
	private List<MotionData> mGyroscopList;
	private List<MotionData> mOrientationList;
	private MotionData[] mLineAcce = null;
	private MotionData[] mGyroscop = null;
	private MotionData[] mOrientation = null;
	private MotionData[] mGravity = null;
	private GPSData gpsData;
	private GPSData[] mGPSData;
	private List<GPSData> gpsDataList = new ArrayList<GPSData>();	
	private long startTime = 0; // 记录"��时间
	private boolean isLineAccFirstData = true;        //标志是否是线性加速度第一次获取的数据
	private boolean ifNeedAdjust = true;	         //标志传感器的工作频率是否"��调整
	private List<Sensor> allSensors;	            //用于装当前设备支持的"��传感"
	private byte[] sensorArray;                    //根据传感器按照预定的顺序""表示是否可用
	private byte[] sensorRateArray; //用来装设定的每个传感器的工作频率
	private long delayMillis = 1000; //多长时间调用"��采集库，默认1000ms
	private Handler handler;
	private MyRunnable myRunnable;
	private class MyRunnable implements Runnable {
		public void run() {
			handler.postDelayed(this, delayMillis);
			// 将list转换为数"
			mLineAcce = mLineAcceList.toArray(new MotionData[mLineAcceList.size()]);
			mGyroscop = mGyroscopList.toArray(new MotionData[mGyroscopList.size()]);
			mOrientation = mOrientationList.toArray(new MotionData[mOrientationList.size()]);
			mGravity = mGravityList.toArray(new MotionData[mGravityList.size()]);
			mGPSData = gpsDataList.toArray(new GPSData[gpsDataList.size()]);
			mGyroscopList.clear();
			mGravityList.clear();
			mOrientationList.clear();
			mLineAcceList.clear();
			gpsDataList.clear();
			callJNI();
			if(ifNeedAdjust) {
				boolean hasAdjust = false;	//用于标志是否有传感器刚修改过工作频率
				hasAdjust |= adjustRote(mGyroscop.length, 0, mGyroscopSensor); //判断和调整陀螺仪的工作频"
				hasAdjust |= adjustRote(mLineAcce.length, 1, mLineAcceSensor); //判断和调整加速度/线"加"度的工作频率
				hasAdjust |= adjustRote(mGravity.length, 2, mGravitySensor); //判断和调整重力的工作频率
				hasAdjust |= adjustRote(mOrientation.length, 3, mOrientationSensor); //判断和调整方向感应器的工作频"
				ifNeedAdjust &= hasAdjust;	//如果有修改过，则下次还需判断是否"��调整，如果无修改，下次则无需再调"
			}
			mLineAcce = null;
			mGyroscop = null;
			mOrientation = null;
			mGravity = null;
			mGPSData = null;
		}
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		startDriveTest();
		super.onCreate();
	}
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("DriveTest", "cyx_DriveTestService.onStartCommand");
		
		if (null != mListener) {
			for (int i = 0; i < mListener.size(); i++) {
				mListener.get(i).serviceEnd();
			}
		}
		
		return START_STICKY;
	}
	public static void addListener(IServiceEndListener listener) {
		if (null == mListener) {
			mListener = new LinkedList<IServiceEndListener>();
		}
		mListener.add(listener);
	}

	public static void removeListener(IServiceEndListener listener) {
		if (null != mListener) {
			mListener.remove(listener);
		}
		
	}
	private void callJNI() {
		// 分别调用
		DriveBh.accDataHandler(mLineAcce);
		DriveBh.gravityDataHandler(mGravity);
		DriveBh.gyroDataHandler(mGyroscop);
		DriveBh.orientationDataHandler(mOrientation);
		DriveBh.gpsDataHandler(mGPSData);
	
//		if (null != journeyHistory && journeyHistory.getCurrentJourneyData() != null) {
//			 journeyHistory.getCurrentJourneyData().setMileage(DriveBh.getDistance());
//		}
	}
	// 初始化传感器及相关工"
	private void initSensor() {
			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			mLineAcceSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); // 线"加""
			mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY); // 重力
			mGyroscopSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); // "��"
			mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); // 方向
			mySensorEventListener = new MySensorEventListener();
	}
	/**
	 * 传感器监听器
	 * 
	 * @author Administrator
	 * 
	 */
	private class MySensorEventListener implements SensorEventListener {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
//			LogUtil.printLog(LogUtil.DEBUG, TAG, "MySensorEventListener");
			MotionData data = new MotionData();
			data.mx = event.values[0];
			data.my = event.values[1];
			data.mz = event.values[2];
			data.time = (int) ((System.currentTimeMillis() - startTime));
			switch(event.sensor.getType()) {
			case Sensor.TYPE_GYROSCOPE:	//"��"
				mGyroscopList.add(data);
				break;
			case Sensor.TYPE_LINEAR_ACCELERATION:
			case Sensor.TYPE_ACCELEROMETER:
				if(isLineAccFirstData && (event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION) ) {	//线"加"度第"��获取数据
					if(!(isLineAccDataAvailable(data.mx) && isLineAccDataAvailable(data.my) && isLineAccDataAvailable(data.mz))) { //如果出现不正常的数据，则将线性加速度改为加"度并重新注册
						mSensorManager.unregisterListener(mySensorEventListener, mLineAcceSensor);
						mLineAcceList.clear();
						if(null != sensorArray && DriveBh.SENSOR_EXIST == sensorArray[10]) {
							mLineAcceSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // 加""
						}
						mSensorManager.registerListener(mySensorEventListener, mLineAcceSensor,getRateByLevel(sensorRateArray[1]));
					}
					isLineAccFirstData = false;
				}
				mLineAcceList.add(data);
				break;
			case Sensor.TYPE_GRAVITY:
				mGravityList.add(data);
				break;
			case Sensor.TYPE_ORIENTATION:
				mOrientationList.add(data);
				break;
			case Sensor.TYPE_LIGHT:
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				break;
			case Sensor.TYPE_PRESSURE:
				break;
			case Sensor.TYPE_TEMPERATURE:
				break;
			case Sensor.TYPE_ROTATION_VECTOR:
				break;
			case Sensor.TYPE_PROXIMITY:
				break;
//			case Sensor.TYPE_RELATIVE_HUMIDITY:
//				break;
				
			}
			data = null;
		}
		
		//用于判断线"加"度的值是否正"
		private boolean isLineAccDataAvailable(float data) {
			if(data>=0 && data<50) 
				return true;
			return false;
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

	}
	/**
	 * 调整传感器工作频"
	 * @param length，传感器对应的list的size
	 * @param position，传感器对应的sensorRateArray的位"
	 * @param sensor，传感器
	 * @return 有调整则返回true，无"��整返回false
	 */
	private boolean adjustRote(int length, int position, Sensor sensor) {
		if(sensor == null) 	//如果该传感器为空，直接返回false
			return false;
		if(DriveBh.FASTER_THEN_DEFINE == getRateCompareToDefine(length, sensorRateArray[position])) {
			if(sensorRateArray[position] != DriveBh.SENSOR_SLOW) {
				sensorRateArray[position] -= 1;
				resetRegisterListener(sensor, sensorRateArray[position]);
				LogUtil.printLog(LogUtil.DEBUG, TAG, sensor.getName()+"---降低了频" + sensorRateArray[position]);
				return true;
			} else {
				return false;
			}
		} else if(DriveBh.SLOWER_THEN_DEFINE == getRateCompareToDefine(length, sensorRateArray[position])) {
			if(sensorRateArray[position] != DriveBh.SENSOR_FASTEST) {
				sensorRateArray[position] += 1;
				resetRegisterListener(sensor, sensorRateArray[position]);
				LogUtil.printLog(LogUtil.DEBUG, TAG, sensor.getName()+"---升高了频" + sensorRateArray[position]);
				return true;
			} else {
				return false;
			}
		} 
		return false;
	}


	// 根据传感器设定的工作频率水平和预先设定的频率进行比较得出快慢"
	int getRateCompareToDefine(int arrayLength, int level) {
			switch (level) {
			case DriveBh.SENSOR_SLOW:
				if (arrayLength > 22)
					return DriveBh.FASTER_THEN_DEFINE;
				break;
			case DriveBh.SENSOR_NORMAL:
				if (arrayLength > 44) {
					return DriveBh.FASTER_THEN_DEFINE;
				} else if (arrayLength < 20) {
					return DriveBh.SLOWER_THEN_DEFINE;
				}
				break;
			case DriveBh.SENSOR_FASTER:
				if (arrayLength > 66) {
					return DriveBh.FASTER_THEN_DEFINE;
				} else if (arrayLength < 40) {
					return DriveBh.SLOWER_THEN_DEFINE;
				}
				break;
			case DriveBh.SENSOR_FASTEST:
				if (arrayLength < 54)
					return DriveBh.SLOWER_THEN_DEFINE;
				break;
			}
			return DriveBh.EQUIVALENT_TO_DEFINE;
		}
   /**
    * 服务启动时调用此方法
    */
	private void startDriveTest(){
		handler = new Handler();
		initSensor();
		startTime = System.currentTimeMillis();
		gpsDataList = new ArrayList<GPSData>();
		//初始化驾驶行为采集库
		if(DriveBh.RESULT_SUCCEED != DriveBh.initDriveBhLib(getSensorArray())) {
			Log.i(TAG, "初始化采集库失败");
			return ;
		}
		// 初始化list
		mLineAcceList = new ArrayList<MotionData>();
		mGravityList = new ArrayList<MotionData>();
		mGyroscopList = new ArrayList<MotionData>();
		mOrientationList = new ArrayList<MotionData>();
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		// 查找到服务信"
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精"
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM); // 功"
		// 从GPS获取"��的定位信" 
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                500, (float) 1.5, new LocationListener() {

					@Override
					public void onLocationChanged(Location location) {
						// TODO Auto-generated method stub
						Log.d(TAG, "latitude :  "+ location.getLatitude() + "   longitude : " + location.getLongitude());
						if(LocationFilter.isAvailableLocatioin(location.getLatitude(), location.getLongitude(), location.getAccuracy(), latitude, longitude)) {
//							Log.d(TAG, "latitude :  "+ location.getLatitude() + "   longitude : " + location.getLongitude());
							gpsData = new GPSData();
							gpsData.time = System.currentTimeMillis();
							gpsData.direction = location.getBearing();
							gpsData.speed = location.getSpeed();
							gpsData.radius = location.getAccuracy();
							gpsData.longitude = location.getLongitude();
							gpsData.latitude = location.getLatitude();
							gpsData.altitude = location.getAltitude();
							gpsDataList.add(gpsData);
							gpsData = null;
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onProviderEnabled(String provider) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onProviderDisabled(String provider) {
						// TODO Auto-generated method stub
						
					}
        	
        });
		
		//获取参数
		DriveBhlibParameter parameter = new DriveBhlibParameter();
		if(DriveBh.RESULT_SUCCEED == DriveBh.getDriveBhParameter(parameter)){
			byte[]  sensorSwitchArray = parameter.sensorSwitchArray;
			sensorRateArray = parameter.sensorRateArray;

			delayMillis = parameter.intervalForHandler;
			if (delayMillis <= 0) {
				delayMillis = 1000;
			} else if (delayMillis > 60 * 60 * 1000) {
				delayMillis = 60 * 60 * 1000;
			}
			handler.removeCallbacks(myRunnable);
			handler.postDelayed(myRunnable, delayMillis);

			//注册监听
			if(DriveBh.SENSOR_OPEN == sensorSwitchArray[0] && mGyroscopSensor != null) {
				mSensorManager.registerListener(mySensorEventListener, mGyroscopSensor,getRateByLevel(sensorRateArray[0]));
			}
			if(mLineAcceSensor == null) {
				if(null != sensorArray && DriveBh.SENSOR_EXIST == sensorArray[10]) {	//如果当前设备没有线"加"度传感器，但有加速度传感器，则改为启用加速度传感"
					mLineAcceSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // 加""
				}
			}
			if(DriveBh.SENSOR_OPEN == sensorSwitchArray[1] && mLineAcceSensor != null) {
				mSensorManager.registerListener(mySensorEventListener, mLineAcceSensor,getRateByLevel(sensorRateArray[1]));
			}
			if(DriveBh.SENSOR_OPEN == sensorSwitchArray[2] && mGravitySensor != null) {
				mSensorManager.registerListener(mySensorEventListener, mGravitySensor,getRateByLevel(sensorRateArray[2]));
			}
			if(DriveBh.SENSOR_OPEN == sensorSwitchArray[3] && mOrientationSensor != null) {
				mSensorManager.registerListener(mySensorEventListener, mOrientationSensor,getRateByLevel(sensorRateArray[3]));
			}
		}
   }
	  // 重新注册传感器监"
	boolean resetRegisterListener(Sensor sensorName,int level) {
			mSensorManager.unregisterListener(mySensorEventListener, sensorName);
			return mSensorManager.registerListener(mySensorEventListener, sensorName, getRateByLevel(level));
	}

	//获得传感器数组，标志某个传感器是否可"
    byte[] getSensorArray() {
			allSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);// 获得传感器列"
			if (allSensors == null || allSensors.size() == 0) {
				return sensorArray;
			}
			sensorArray = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			Sensor sensor;
		for (int i = 0, size = allSensors.size(); i < size; i++) {
				sensor = allSensors.get(i);
				switch (sensor.getType()) {
				case Sensor.TYPE_GYROSCOPE:
					Log.d(TAG, "陀螺仪感应器");
					sensorArray[0] = DriveBh.SENSOR_EXIST;
					break;
				case Sensor.TYPE_LINEAR_ACCELERATION:
					Log.d(TAG, "线加度感应器");
					sensorArray[1] = DriveBh.SENSOR_EXIST;
					break;
				case Sensor.TYPE_GRAVITY:
					Log.d(TAG, "重力感应");
					sensorArray[2] = DriveBh.SENSOR_EXIST;
					break;
				case Sensor.TYPE_ORIENTATION:
					Log.d(TAG, "方向感应");
					sensorArray[3] = DriveBh.SENSOR_EXIST;
					break;
				case Sensor.TYPE_LIGHT:
					Log.d(TAG, "光线感应");
					sensorArray[4] = DriveBh.SENSOR_EXIST;
					break;
				case Sensor.TYPE_MAGNETIC_FIELD:
					Log.d(TAG, "磁力感应");
					sensorArray[5] = DriveBh.SENSOR_EXIST;
					break;
				case Sensor.TYPE_PRESSURE:
					Log.d(TAG, "压力感应");
					sensorArray[6] = DriveBh.SENSOR_EXIST;
					break;
				// case Sensor.TYPE_AMBIENT_TEMPERATURE:
				case Sensor.TYPE_TEMPERATURE:
					Log.d(TAG, "温度感应");
					sensorArray[7] = DriveBh.SENSOR_EXIST;
					break;
				case Sensor.TYPE_ROTATION_VECTOR:
					Log.d(TAG, "旋转矢量感应");
					sensorArray[8] = DriveBh.SENSOR_EXIST;
					break;
				case Sensor.TYPE_PROXIMITY:
					Log.d(TAG, "接近感应");
					sensorArray[9] = DriveBh.SENSOR_EXIST;
					break;
				case Sensor.TYPE_ACCELEROMETER:
					Log.d(TAG, "加速度感应器");
					sensorArray[10] = DriveBh.SENSOR_EXIST;
					break;
				// case Sensor.TYPE_RELATIVE_HUMIDITY:
//					Log.d(TAG, "湿度感应");
//					sensorArray[11] = 1;
				// break;
				}
			}
			return sensorArray;
		}
		
	//根据sensorRateArray里的level得到对应的工作频"
	int getRateByLevel(int level) {
			switch(level) {
			case DriveBh.SENSOR_SLOW:
				return SensorManager.SENSOR_DELAY_NORMAL;
			case DriveBh.SENSOR_NORMAL:
				return SensorManager.SENSOR_DELAY_UI;
			case DriveBh.SENSOR_FASTER:
				return SensorManager.SENSOR_DELAY_GAME;
			case DriveBh.SENSOR_FASTEST:
				return SensorManager.SENSOR_DELAY_FASTEST;
			}
			return 0;
	}
	
	@Override
	public void onReceiveLocation(BDLocation location) {
//		System.out.println("------service location"+ location.getLatitude()+ "  "+ location.getLongitude());
//		gpsData = new GPSData();
//		gpsData.time = System.currentTimeMillis();
//		gpsData.direction = location.getDirection();
//		gpsData.speed = location.getSpeed();
//		gpsData.radius = location.getRadius();
//		gpsData.longitude = location.getLongitude();
//		gpsData.latitude = location.getLatitude();
//		gpsData.altitude = location.getAltitude();
//		gpsDataList.add(gpsData);
//		gpsData = null;
	}

	// 将数据归"
	private void cleanData() {
			startTime = 0;
			mySensorEventListener = null;	
			mLineAcceSensor = null; 
			mGravitySensor = null; 
		    mGyroscopSensor = null; 
		    mOrientationSensor = null; 
			mSensorManager = null; 
	}

	/**
	 * 停止时调用此方法
	 */
	private void stopDriveTest(){
		// 解除传感器监"
		mSensorManager.unregisterListener(mySensorEventListener);
		handler.removeCallbacks(myRunnable);
		DriveBh.uninitDriveBhLib();
		cleanData();
	}

	@Override
	public void onDestroy() {
        Log.i(TAG, "-----cyx_DriveTestService stop");
		stopDriveTest();
		super.onDestroy();
	}
	
}
