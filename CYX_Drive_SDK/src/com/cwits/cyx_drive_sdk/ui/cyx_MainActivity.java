package com.cwits.cyx_drive_sdk.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MKMapStatusChangeListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cwits.cyx_drive_sdk.bean.Crowd;
import com.cwits.cyx_drive_sdk.bean.Position;
import com.cwits.cyx_drive_sdk.bean.Stroke;
import com.cwits.cyx_drive_sdk.bean.StrokeResult;
import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ConstantContext;
import com.cwits.cyx_drive_sdk.data.DriveBhHandler;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.data.INotifyDriveBhListener;
import com.cwits.cyx_drive_sdk.exception.LogUtil;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.libDriveBh.DriveBh;
import com.cwits.cyx_drive_sdk.libDriveBh.DriveBhlibParameter;
import com.cwits.cyx_drive_sdk.libDriveBh.GPSData;
import com.cwits.cyx_drive_sdk.libDriveBh.MotionData;
import com.cwits.cyx_drive_sdk.libDriveBh.SensorData;
import com.cwits.cyx_drive_sdk.userInfo.Constant;
import com.cwits.cyx_drive_sdk.userInfo.IExternalInterfaceAR;
import com.cwits.cyx_drive_sdk.userInfo.IUserInfo;
import com.cwits.cyx_drive_sdk.util.Coordinate;
import com.cwits.cyx_drive_sdk.util.JourneyTool;
import com.cwits.cyx_drive_sdk.util.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class cyx_MainActivity extends Activity implements
		INotifyDriveBhListener {
	private static final String TAG = "MainActivity";
	private int total_acctime = 0;
	private int total_dectime = 0;
	private int total_turntime = 0;
	private int total_overtime = 0;
	private int total_distance = 0;
	cyx_CustomAlertDialog finishDialog;
	// 数据发送相关
	private Stroke strokes;
	private Stroke strokesAR;
	private JSONObject Jstrokes_a; // 发送的实时位置中的行程数据
	private List<JSONObject> Jstrokes_list;

	private int isSimulate;// 是否为模拟数据 0为模拟
	private double simulation_distance = 2000;// 两公里一个数据
	private Position position;
	private JSONObject Jposition_a; // 实时位置点数据

	private List<JSONObject> Jposition_list;
	private List<JSONObject> end_position_list;
	private List<JSONObject> end_Jstrokes_list;
	private JSONObject eventPosition, eventSenser;
	private JSONObject eventPosition_a, eventSenser_a;
	private List<JSONObject> eventPosition_list, eventSenser_list;
	private int type_a = 0;
	private List<String> type_list;

	private List<JSONObject> positionAll_list;// 全部位置点
	private List<JSONArray> positionAry_list;// 补偿点
	private JSONArray positionAry_a;

	private List<JSONObject> DataToAR_list;
	private JSONObject DataToAR_a;
	private int isfirsttime_a = 0;
	// 行程短通知相关

	private long delayTime = 60 * 1000;// 每隔60s发送一次数据

	int sid = 0;// toAR行程段id
	String uuid;// 行程id
	private double totleDistance = 0.0;// 当前行程
	private double historyDistance = 0.0;// 上一段的行程
	JSONObject dataToAR, allDataToAR;
	double strokes_mileage = 0;			// 服务器端间隔里程
	double strokes_mileage_ar = 0; 		// AR端间隔里程
	int strokes_seconds = 0;// 累计时间
	postWorker mPostWorker;
    private static TextView gold_coin;//金币数
    private static int coin_num=0;
	dataCollectionTime mDataCollectionTime; // 60s采集一次数据
	dataCollectionPosition mDataCollectionPosition; // 根据条件采集数据

	boolean isFirstTime_post = true; // 实时位置上报（第一次插入数据不要延时60S）
	boolean isThreadalive = true; // 是否发送实时位置点

	boolean isJournyEnd = false; // 行程是否结束

	// 判断去除无用位置点
	boolean bool_radius = true; // 精确度多少
	boolean bool_speed = true; // 速度小于多少
	boolean bool_direction = true; // 角度变化

	private boolean isFinish = false;
	boolean swervel = false; // 转弯
	Runnable mTimeOutRunnable;
	// 定位相关
	private IExternalInterfaceAR ExternalInterfaceAR;
	public String JsonMessage;
	LocationClient mLocClient;
	LocationData locData = null;
	JourneyTool journeyTool;
	MyLocationListener myListener = new MyLocationListener();
	// 定位图层
	MyLocationOverlay myLocationOverlay = null;
	// 地图相关
	MapView mMapView = null; // 地图View
	MapController mMapController = null;
	private MyOverlay mOverlay = null; // 自定义overlay
	private MyTrafficConditionOverlay mTrafficConditionOverlay; // 交通众包overlay
	private ArrayList<OverlayItem> mTCItems = null;// 保存交通众包数据

	private ArrayList<OverlayItem> mItems = null;
	private Handler handler;
	private MyRunnable myRunnable;
	private MyTrafficSourceRunnable mTrafficSourceRunnable;
	private long startTime = 0; // 记录开始时间
	
	// 传感器相关
	private Sensor mLineAcceSensor; // 线性加速度传感器
	private Sensor mGravitySensor; // 重力传感器
	private Sensor mGyroscopSensor; // 陀螺仪
	private Sensor mOrientationSensor; // 方向感应器
	private SensorManager mSensorManager; // 传感器管理类
	private MySensorEventListener mySensorEventListener; // 传感器监听器

	// 装数据的list和数组
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

	private List<GPSData> gpsDataListToAr;
	private List<GPSData> gpsDataListToSend;
	private int ListToAr_num = 0;
	private GPSData avalableGPSData;
	private float MaxSpeed = 0;// 最高速度
	private List<GPSData> gpsDataList;

	float[] mDirection = null;
	float[] mGspeed = null;
	double[] mLongitude = null;
	double[] mLatitude = null;
	MediaPlayer mMediaPlayer;
	Button btnZoomin, btnZoomout;
	private long delayMillis = 1000; // 多长时间调用一次采集库，默认1000ms
	private byte[] sensorRateArray; // 用来装设定的每个传感器的工作频率
	private boolean ifNeedAdjust = true; // 标志传感器的工作频率是否需要调整
	private List<Sensor> allSensors; // 用于装当前设备支持的所有传感器
	private byte[] sensorArray; // 根据传感器按照预定的顺序用0、1表示是否可用
//	BMapManager mBMapManager = null;
	private boolean isLineAccFirstData = true; // 标志是否是线性加速度第一次获取的数据

	private boolean isCollectGPS = false; // 标志是否收集gps点集：开启驾驶模式时收集
	private boolean isFirstRun = true;

	// 众包信息
	private BDLocation preLocation;

	private boolean isFirstLoc = true;
	private boolean isShowTraffic = false;
	private Button btn_getLocation; // 定位按钮
	private Button btn_stop_driving; // 结束驾驶
	private Button btn_traffic; // 交通路况按钮
	private Button report_btn;
	public String hostip;
	private BDLocation preCenter; // 滑动前的中心位置
	private String city = "深圳";
	LinearLayout search_layout;

	boolean isRequest = true;

	private IUserInfo userInfo;
	private cyx_CustomAlertDialog mDialog;
	private Runnable mapMoveRunnable;
	private double[] owg; // 真实坐标
	private Integer mSyncObj = new Integer(0);
	boolean isEndFlag = false;
	Connection conn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		isSimulate = intent.getIntExtra("simulate", 1);
		journeyTool = new JourneyTool();
		ExternalInterfaceAR = CYX_Drive_SDK.getInstance()
				.getExternalInterface();
		userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 设置屏幕常亮
		if (cyx_MyApplication.mBMapManager == null) {
			cyx_MyApplication.mBMapManager = new BMapManager(getApplicationContext());
			cyx_MyApplication.mBMapManager.init(new cyx_MyApplication.MyGeneralListener());
		}
		setContentView(MResource.getLayoutId(getApplicationContext(),
				"cyx_activity_main"));
		cyx_MyApplication.getInstance().addActivity(this);

		new DriveBhHandler().addNotifyDriveBhListener(this);

		myRunnable = new MyRunnable();
		mTrafficSourceRunnable = new MyTrafficSourceRunnable();
		if (isSimulate == 0) {
			simulation_distance = 0;
		} else {
			simulation_distance = 2000;
		}
		init();
		startDriving();

	}

	public static void coinNumber(int number){
		coin_num+=number;
		Log.e("lxh","coin number ++");
		gold_coin.setText(""+coin_num);
	}
    private void initmap(){
    	mMapView = (MapView) findViewById(MResource.getID(
				getApplicationContext(), "mMapView"));
		mMapController = mMapView.getController();
		mMapController.setZoom(15);
		mMapController.enableClick(true);
		mMapController.setScrollGesturesEnabled(true);
		mMapView.setEnabled(true);
		initLocation();
		mOverlay = new MyOverlay(getResources()
				.getDrawable(
						MResource.getDrawableId(getApplicationContext(),
								"ic_flag_end")), mMapView);
		mTrafficConditionOverlay = new MyTrafficConditionOverlay(getResources()
				.getDrawable(
						MResource.getDrawableId(getApplicationContext(),
								"ic_flag_end")), mMapView);
		mItems = new ArrayList<OverlayItem>();
		mTCItems = new ArrayList<OverlayItem>();

		if (myLocationOverlay == null)
			myLocationOverlay = new MyLocationOverlay(mMapView);
		myLocationOverlay.enableCompass();
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		mMapView.regMapViewListener(cyx_MyApplication.mBMapManager, new MyMKMapViewListener());
		mMapView.regMapStatusChangeListener(new MyMKMapStatusChangeListener());
		mMapView.getOverlays().add(mOverlay);
		SharedPreferences sh = getSharedPreferences("lastLocation",
				Activity.MODE_PRIVATE);
		String lat = sh.getString("lat", "");
		String lon = sh.getString("lon", "");
		if (!TextUtils.isEmpty(lon) && !TextUtils.isEmpty(lat)) {
			GeoPoint point = new GeoPoint(
					(int) (Double.parseDouble(lat) * 1e6),
					(int) (Double.parseDouble(lon) * 1e6));
			mMapController.animateTo(point);
			locData.latitude = Double.parseDouble(lat);
			locData.longitude = Double.parseDouble(lon);
			myLocationOverlay.setData(locData);
		}
    }
	private void init() {
		handler = new Handler();
		conn = CYX_Drive_SDK.getInstance().getConnection();
		gold_coin=(TextView) findViewById(MResource.getID(
				getApplicationContext(), "gold_coin"));
		
		btnZoomin = (Button) findViewById(MResource.getID(
				getApplicationContext(), "btn_zoomin"));
		btnZoomout = (Button) findViewById(MResource.getID(
				getApplicationContext(), "btn_zoomout"));
		btnZoomin.setOnClickListener(clickListener);
		btnZoomout.setOnClickListener(clickListener);
		info = (TextView) findViewById(MResource.getID(getApplicationContext(),
				"info"));
		positionAll_list = new ArrayList<JSONObject>();// 全部位置点

		Jstrokes_list = new ArrayList<JSONObject>();
		Jposition_list = new ArrayList<JSONObject>();

		end_Jstrokes_list = new ArrayList<JSONObject>();
		end_position_list = new ArrayList<JSONObject>();
		
		DataToAR_a = new JSONObject();
		DataToAR_list = new ArrayList<JSONObject>();

		/* 补偿点相关 */
		positionAry_a = new JSONArray();
		positionAry_list = new ArrayList<JSONArray>();

		Jstrokes_a = new JSONObject();
		Jposition_a = new JSONObject();
		eventPosition = new JSONObject();
		eventSenser = new JSONObject();

		eventPosition_a = new JSONObject();
		eventSenser_a = new JSONObject();

		eventPosition_list = new ArrayList<JSONObject>();
		eventSenser_list = new ArrayList<JSONObject>();
		type_list = new ArrayList<String>();
		gpsDataList = new ArrayList<GPSData>();
		gpsDataListToAr = new ArrayList<GPSData>();
		gpsDataListToSend = new ArrayList<GPSData>();
		initSensor();

		btn_traffic = (Button) findViewById(MResource.getID(
				getApplicationContext(), "map_traffic"));
		btn_traffic.setOnClickListener(clickListener);

		search_layout = (LinearLayout) findViewById(MResource.getID(
				getApplicationContext(), "search_search_layout"));
		search_layout.setOnClickListener(clickListener);

		btn_getLocation = (Button) findViewById(MResource.getID(
				getApplicationContext(), "btn_getLocation"));
		btn_getLocation.setOnClickListener(clickListener);
		btn_stop_driving = (Button) findViewById(MResource.getID(
				getApplicationContext(), "btn_stop_driving"));
		btn_stop_driving.setOnClickListener(clickListener);
		report_btn = (Button) findViewById(MResource.getID(
				getApplicationContext(), "main_btn_report"));
		report_btn.setOnClickListener(clickListener);

		// 当前城市
		city = getSharedPreferences(Constant.ADDRESS_INFO, MODE_PRIVATE)
				.getString(Constant.ADDRESS_CITY, "");
		avalableGPSData = new GPSData();
		position = new Position();
		strokes = new Stroke();
		strokesAR = new Stroke();
		coinNumber(coin_num);
		if (!ifGPSOpen()) {
			createDialog();
		}
		mapMoveRunnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				isRequest = true;
				if (myLocationOverlay != null)
					myLocationOverlay
							.setLocationMode(com.baidu.mapapi.map.MyLocationOverlay.LocationMode.FOLLOWING);
				mMapController.animateTo(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
			}
		};
	}

	// 初始化传感器及相关工具
	private void initSensor() {
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mLineAcceSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); // 线性加速度
		mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY); // 重力
		mGyroscopSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_GYROSCOPE); // 陀螺仪
		mOrientationSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION); // 方向
		mySensorEventListener = new MySensorEventListener();
	}

	// 初始化定位
	private void initLocation() {
		mLocClient = new LocationClient(getApplicationContext());
		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(LocationClientOption.MIN_SCAN_SPAN); // 最小间隔为1秒
		option.setNeedDeviceDirect(true);
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		mLocClient.setLocOption(option);
		mLocClient.start();
		mLocClient.requestLocation();
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			Intent intent;
			if (id == MResource.getID(getApplicationContext(), "btn_zoomin")) {
				mMapController.zoomIn();
				handler.removeCallbacks(mTrafficSourceRunnable);
				handler.post(mTrafficSourceRunnable);

			} else if (id == MResource.getID(getApplicationContext(),
					"btn_zoomout")) {
				mMapController.zoomOut();
				handler.removeCallbacks(mTrafficSourceRunnable);
				handler.post(mTrafficSourceRunnable);
			} else if (id == MResource.getID(getApplicationContext(),
					"map_traffic")) {
				if (isShowTraffic) {
					mMapView.setTraffic(false);
					btn_traffic.setBackgroundResource(MResource.getDrawableId(
							getApplicationContext(), "lukuang"));
					isShowTraffic = false;
					Toast.makeText(
							cyx_MainActivity.this,
							getResources().getString(
									MResource.getStringId(
											getApplicationContext(),
											"close_traffic")),
							Toast.LENGTH_SHORT).show();
				} else {
					mMapView.setTraffic(true);
					btn_traffic.setBackgroundResource((MResource.getDrawableId(
							getApplicationContext(), "lukuang_press")));
					isShowTraffic = true;
					Toast.makeText(
							cyx_MainActivity.this,
							getResources().getString(
									MResource.getStringId(
											getApplicationContext(),
											"open_traffic")),
							Toast.LENGTH_SHORT).show();
				}

			} else if (id == MResource.getID(getApplicationContext(),
					"search_search_layout")) {
				intent = new Intent(cyx_MainActivity.this,
						cyx_NaviSearchActivity.class);
				if (city == null || city.equals(""))
					city = "深圳";
				intent.putExtra("city", city);
				startActivity(intent);

			} else if (id == MResource.getID(getApplicationContext(),
					"btn_getLocation")) {
				if (locData != null) {
					mMapController.animateTo(new GeoPoint(
							(int) (locData.latitude * 1e6),
							(int) (locData.longitude * 1e6)));
				}
				isRequest = true;
			} else if (id == MResource.getID(getApplicationContext(),
					"btn_stop_driving")) {
				finishDialog();

			} else if (id == MResource.getID(getApplicationContext(),
					"main_btn_report")) {
				// 交通信息上报
				intent = new Intent(cyx_MainActivity.this,
						cyx_TrafficReport.class);
				intent.putExtra("lon", locData.longitude);
				intent.putExtra("lat", locData.latitude);
				startActivity(intent);
			}
		}
	};

	// 自定义对话框
	private void finishDialog() {
		if (finishDialog != null && finishDialog.isShowing()) {
			finishDialog.dismiss();
			finishDialog = null;
		}
		if (mDialog != null)
			mDialog = null;
		finishDialog = new cyx_CustomAlertDialog(cyx_MainActivity.this);
		finishDialog.setTitle(getString(MResource.getStringId(
				getApplicationContext(), "notice")));
		finishDialog.setMessageVisible(false);
		finishDialog.showLine();
		finishDialog.setNumber(getString(MResource.getStringId(
				getApplicationContext(), "is_finish_driving")));
		finishDialog
				.setPositiveButton(getString(MResource.getStringId(
						getApplicationContext(), "yes")),
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								finishDialog.dismiss();
								stopDriving();
							}
						});
		finishDialog
				.setNegativeButton(getString(MResource.getStringId(
						getApplicationContext(), "no")), new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finishDialog.dismiss();
					}
				});
	}

	TextView info;

	// 百度定位监听
	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (location == null)
				return;
			preLocation = location;
			owg = Coordinate.baidutowg(location.getLongitude(),
					location.getLatitude());
			if (isFirstRun) {
				handler.removeCallbacks(mTrafficSourceRunnable);
				handler.post(mTrafficSourceRunnable);
				preCenter = location;
				isFirstRun = false;
				strokes.setSlon(owg[0]);// 记录起点经纬度，时间
				strokes.setSlat(owg[1]);
				strokesAR.setSlon(owg[0]);// 记录AR起点经纬度，时间
				strokesAR.setSlat(owg[1]);
			}

			if (isCollectGPS) {
				gpsData = new GPSData();
				gpsData.time = TimeUtil.stringTolong(TimeUtil.parseToUTC(location.getTime())) / 1000;
				gpsData.direction = location.getDirection();
				gpsData.speed = location.getSpeed();
				gpsData.radius = location.getRadius();
				gpsData.longitude = owg[0];
				gpsData.latitude = owg[1];
				gpsData.altitude = location.getAltitude();

				strokes.setElon(owg[0]);// 随时记录终点经纬度，时间
				strokes.setElat(owg[1]);
				strokes.setEnd_time((int) (TimeUtil.stringTolong(TimeUtil.parseToUTC(location.getTime())) / 1000));

				strokesAR.setElon(owg[0]);// 随时记录AR终点经纬度，时间
				strokesAR.setElat(owg[1]);
				strokesAR.setEnd_time((int) (TimeUtil.stringTolong(TimeUtil.parseToUTC(location.getTime())) / 1000));
				if (isSimulate == 0) {
					bool_radius = gpsData.radius < 200;// 精确度多少
					bool_speed = gpsData.speed >= 0;// 速度小于多少
				} else {
					bool_radius = gpsData.radius < 100;// 精确度多少
					bool_speed = gpsData.speed >= 0;// 速度小于多少
				}

				gpsDataListToAr.add(gpsData);
				gpsDataList.add(gpsData);
				if (ListToAr_num > 0) {
					bool_direction = Math.abs(gpsDataListToAr
							.get(ListToAr_num - 1).direction
							- gpsData.direction) < 360;// 角度变化、
					swervel = Math
							.abs(gpsDataListToAr.get(ListToAr_num - 1).direction
									- gpsData.direction) > 5;// 拐弯事件
					if (gpsData.speed > MaxSpeed) {
						MaxSpeed = gpsData.speed;
					}
				}
				if (ListToAr_num == 0) {
					MaxSpeed = gpsData.speed;

					avalableGPSData = gpsData;
					if (mPostWorker == null) {
						mPostWorker = new postWorker();
					}
					if (!mPostWorker.isAlive()) {
						mPostWorker.start();
					}

					if (mDataCollectionPosition == null) {
						mDataCollectionPosition = new dataCollectionPosition();
					}
					if (!mDataCollectionPosition.isAlive()) {
						mDataCollectionPosition.start();
					}

					if (mDataCollectionTime == null) {
						mDataCollectionTime = new dataCollectionTime();

					}
					if (!mDataCollectionTime.isAlive()) {
						mDataCollectionTime.start();
					}

				} else if (bool_radius && bool_speed && bool_direction) {

					avalableGPSData = gpsData;
				}
				String strInfo = "g lat:" + gpsData.latitude + "; g lon:"
						+ gpsData.longitude + "; g rad:" + gpsData.radius
						+ "; g spd" + gpsData.speed + "\n" + "a lat:"
						+ avalableGPSData.latitude + "; a lon:"
						+ avalableGPSData.longitude + "; a rad:"
						+ avalableGPSData.radius + "; a spd"
						+ avalableGPSData.speed + "\n" + " distance: "
						+ DriveBh.getDistance() * 1000 + "\n" + " MaxSpeed: "
						+ MaxSpeed;

				info.setText(strInfo + "\n" + strokesInfo);
				//strokes.setMileages(DriveBh.getDistance() * 1000);

				if (bool_radius && bool_speed && swervel) { // 判断拐弯事件，发送给客户端
					gpsDataListToSend.add(gpsData);
				}
				ListToAr_num++;
				gpsData = null;

			}

			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			if((!location.hasSpeed()||location.getSpeed()==0)&&mOrientationList!=null && mOrientationList.size()>0) {
				locData.direction = mOrientationList.get(0).mx;
			} else {
				locData.direction = location.getDirection();
			}
			if (isFirstLoc || isRequest) {
				isFirstLoc = false;
				isRequest = false;
				btn_getLocation.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "is_location"));
				btn_getLocation.setEnabled(false);
				if (myLocationOverlay != null)
					myLocationOverlay
							.setLocationMode(com.baidu.mapapi.map.MyLocationOverlay.LocationMode.FOLLOWING);
				mMapController.animateTo(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
			}
			if(myLocationOverlay!=null&&mMapView!=null){
				myLocationOverlay.setData(locData);
				mMapView.refresh();
			}
		}

	}

	public GPSData getPosition() {
		return avalableGPSData;
	}

	private class postWorker extends Thread {
		@Override
		public void run() {
			synchronized (mSyncObj) {
				while (isThreadalive) {
					// 发送实时位置点
					int size = 0;
					if (Jstrokes_list != null && Jstrokes_list.size() > 0)
						size = Jstrokes_list.size();
					if (size > 0) {
						Log.e("lxh", "实时位置上传");
						Jstrokes_a = Jstrokes_list.get(0);
						Jposition_a = Jposition_list.get(0);
						setUploadPosition(Jstrokes_a, Jposition_a);
					}

					/* 事件上传 */
					int eventSize = 0;
					if (eventPosition_list != null
							&& eventPosition_list.size() > 0)
						eventSize = eventPosition_list.size();
					if (eventSize > 0) {
						eventPosition_a = eventPosition_list.get(0);
						type_a = Integer.parseInt(type_list.get(0));
						eventSenser_a = eventSenser_list .get(0);
						setUploadEvent(uuid, eventPosition_a, type_a,
								eventSenser_a);
					}

					/* 补偿点 */
					int positionarySize = 0;
					if (positionAry_list != null && positionAll_list.size() > 0)
						positionarySize = positionAry_list.size();
					if (positionarySize > 0) {
						positionAry_a = positionAry_list.get(0);
						setUploadTurns(uuid, positionAry_a);

					}

					// AR行程段通知
					int sizeAR = DataToAR_list.size();
					if (sizeAR > 0) {
						DataToAR_a = DataToAR_list.get(0);
						JSONObject jsNotice = new ExtraDataProcess()
								.getNoticeJourneyPartData(userInfo.getUserID(),
										DataToAR_a);
						if(conn.getConnectionState()==Connection.CONN_STATE_LOGIN_OK){
							conn.sendPersistentData(jsNotice);
							int size_ar = DataToAR_list.size();
							if (size_ar > 0) {
								for (int i = 0; i < size; i++) {
									DataToAR_list.remove(i);
									--size;
								}
						}
					  }
					}
					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 
				}
				System.out.println("----------before size_end:"+ end_position_list.size());
				// 行程结束
				if (isFinish) {
					// 行程总结（AR、服务器），结束标记
					if (isfirsttime_a == 0) {
						// 只添加一次
						Log.i("lxh", "--------行程总结");
						setDataToUpload();
						if (isSimulate == 0) {
							setAllDataToAR_simulator();
						} else {
							setAllDataToAR();
						}
						isfirsttime_a++;
					}
					int size_end = end_position_list.size();
					System.out.println("----------after size_end:"+ size_end);
					
					if (size_end > 0) {
						Log.e("lxh", "--------行程結束");
						// 行程总结 (服务器)
						Jposition_a = end_position_list.get(0);
						Jstrokes_a  = end_Jstrokes_list.get(0);
						setUploadPosition(Jstrokes_a, Jposition_a);
						if(size_end>0){
							for (int i = 0; i < size_end; i++) {
								end_Jstrokes_list.remove(i);
								end_position_list.remove(i);
							}
						}
					}
					if(isEndFlag){
                    	Log.e("lxh", "-------结束标记");
						// 行程结束标记
						JSONObject jsendFlag = new ExtraDataProcess()
								.getUploadJourneyEndFlagData(userInfo.getUserID(),
										uuid, "1");
						if(conn.getConnectionState()==Connection.CONN_STATE_LOGIN_OK){
							conn.sendPersistentData(jsendFlag);
							isEndFlag = false;
						}
					}
					// AR行程总结
					if(allDataToAR!=null){
						Log.e("lxh", "-------AR行程总结");
						JSONObject jsEndToAR = new ExtraDataProcess()
								.getNoticeJourneyAllData(userInfo.getUserID(),
										allDataToAR);
						if(conn.getConnectionState()==Connection.CONN_STATE_LOGIN_OK){
							conn.sendPersistentData(jsEndToAR);
							allDataToAR = null;
						}
					}
				}

			}

		}
	}

	// 60S添加一次数据
	private class dataCollectionTime extends Thread {
		@Override
		public void run() {
			while (isThreadalive) {

				/* 实时位置上传通知 */
				datasToUpload();

				setDataToUpload();
				/* 补偿点 */
				uploadTurns();
				if (isFirstTime_post) {
					
					isFirstTime_post = false;
				} else {
					try {
						Thread.sleep(delayTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}

	}

	// 添加数据线程
	private class dataCollectionPosition extends Thread {
		@Override
		public void run() {
		while (isThreadalive) {
			// 添加行程段通知到AR
			totleDistance = DriveBh.getDistance() * 1000;
			double distance = totleDistance - historyDistance;
			if (isSimulate == 0) {
				distance = 3000;
			}
			if (distance >= simulation_distance) {
				sid++;
				historyDistance = totleDistance;
				datasToUpload_AR();
				if (isSimulate == 0) {
					setDataToAR_simulate();
				} else {
					setDataToAR(distance);
				}
			}

			if (isSimulate == 0) {
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	   }
	}

	public boolean journeyPartdistance() {
		totleDistance = DriveBh.getDistance() * 1000;
		if (totleDistance - historyDistance >= simulation_distance) {
			return true;
		}
		return false;
	}

	public float getMaxSpeed() {

		return MaxSpeed;

	}

	private void datasToUpload() {
		int strokes_vtime;
		String last_time = "";
		if (position.getTime() == null) {
			strokes_vtime = 0;
			position.setTime(TimeUtil.parseToUTC(TimeUtil.dateLongFormatString(
					System.currentTimeMillis(), TimeUtil.format1)));
		} else {
			last_time = position.getTime();
			position.setTime(TimeUtil.dateLongFormatString(
					getPosition().time * 1000, TimeUtil.format1));
			strokes_vtime = (int) (((TimeUtil.stringTolong(position.getTime()) - TimeUtil
					.stringTolong(last_time))) / 1000);
		}
		// Log.d("stringTolong","stringTolong------getPosition().time = "+getPosition().time);
		position.setLon(getPosition().longitude);
		position.setLat(getPosition().latitude);
		position.setSpeed(getPosition().speed);
		position.setHeight(getPosition().altitude);
		position.setDirection(getPosition().direction);
		if(isFinish){
			if(preLocation!=null){
				double[] point = Coordinate.baidutowg(preLocation.getLongitude(), preLocation.getLatitude());
				position.setLon(point[0]);
				position.setLat(point[1]);
				position.setSpeed(preLocation.getSpeed());
				position.setHeight(preLocation.getAltitude());
				position.setDirection(preLocation.getDirection());
				position.setState(1);
				position.setTime(TimeUtil.parseToUTC(TimeUtil.dateLongFormatString(System.currentTimeMillis(), TimeUtil.format1)));
			}
		}
		else if (position.getTime() == last_time) {
			position.setState(0);// 定位无效，状态为0
			position.setTime(TimeUtil.parseToUTC(TimeUtil.dateLongFormatString(
					System.currentTimeMillis(), TimeUtil.format1)));
		} else {
			position.setState(1);// 定位有效，状态为1
		}
		strokes.setVtime(strokes_vtime);

		strokes.setSeconds(strokes_seconds);
		strokes_seconds += 60;

		strokes_mileage = DriveBh.getDistance() * 1000 - strokes.getMileages();
		strokes.setMileage(strokes_mileage);
		strokes.setMileages(DriveBh.getDistance() * 1000);

		strokes.setVspeed((float) ((strokes.getMileages() / 1000)
				/ ((double) strokes.getSeconds()) * 3600));
		strokes.setMax(getMaxSpeed());
		strokes.setStroke(uuid);// 本次行程的编号
		strokesInfo = "stroke time: " + strokes.getSeconds()
				+ "   stroke mileages  " + strokes.getMileages() / 1000
				+ "     avg: " + strokes.getVspeed();
	}

	String strokesInfo = "";

	private void datasToUpload_AR() {

		if(isFinish) {
			strokesAR.setSeconds(strokes.getSeconds());
			strokesAR.setMileage(strokes.getMileage());
			strokesAR.setMileages(strokes.getMileages());
			strokesAR.setVspeed(strokes.getVspeed());
		} else {
			int strokesAR_seconds = (int) ((System.currentTimeMillis() / 1000) - strokesAR
					.getStart_time());
			strokesAR.setSeconds(strokesAR_seconds);
			strokes_mileage_ar = DriveBh.getDistance() * 1000
					- strokesAR.getMileages();
			strokesAR.setMileage(strokes_mileage_ar);
			strokesAR.setMileages(DriveBh.getDistance() * 1000);

			strokesAR.setVspeed((float) ((strokesAR.getMileages() / 1000)
					/ ((double) strokesAR.getSeconds()) * 3600));
		}
		strokesAR.setMax(getMaxSpeed());
		strokesAR.setStroke(uuid);// 本次行程的编号

	}

	// 实时位置上报数据
	private void setDataToUpload() {
		JSONObject Jposition = new JSONObject();
		JSONObject Jstrokes = new JSONObject();

		try {
			Jposition.put("times", position.getTime());
			Jposition.put("lon", position.getLon());
			Jposition.put("lat", position.getLat());
			Jposition.put("speed", position.getSpeed());
			Jposition.put("height", position.getHeight());
			Jposition.put("direction", position.getDirection());
			Jposition.put("state", position.getState());
			Jstrokes.put("mileages", strokes.getMileages());
			Jstrokes.put("seconds", strokes.getSeconds());
			Jstrokes.put("mileage", strokes.getMileage());
			Jstrokes.put("vtime", strokes.getVtime());
			Jstrokes.put("vspeed",
					strokes.getVspeed() >= 0 ? strokes.getVspeed() : 0);
			Jstrokes.put("max", strokes.getMax());
			Jstrokes.put("stroke", strokes.getStroke());
			Jstrokes.put("acc", strokes.getAcc());
			Jstrokes.put("dec", strokes.getDec());
			Jstrokes.put("turn", strokes.getTurn());
			Jstrokes.put("overspeed", strokes.getSpeeds());
			Jstrokes.put("tired", strokes.getTires());
			Jstrokes.put("slide", 0);	//目前尚未实现
			Jstrokes_list.add(Jstrokes);
			Jposition_list.add(Jposition);
			positionAll_list.add(Jposition);
			if(isFinish){
				end_Jstrokes_list.add(Jstrokes);
				end_position_list.add(Jposition);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// AR行程段数据 模拟数据
	private void setDataToAR_simulate() {
		int area = Integer.parseInt(userInfo.getArea());
		double times = 100 * Math.random();
		double times1 = 100 * Math.random();
		double times2 = 100 * Math.random();
		double times3 = 100 * Math.random();

		int acctime, dectime, turntime, overtime;
		if (times < 10) {
			acctime = 1;
			total_acctime++;
		} else {
			acctime = 0;
		}
		if (times1 < 10) {
			total_dectime++;
			dectime = 1;
		} else {
			dectime = 0;
		}
		if (times2 < 10) {
			total_turntime++;
			turntime = 1;
		} else {
			turntime = 0;
		}
		if (times3 < 10) {
			total_overtime++;
			overtime = 1;
		} else {
			overtime = 0;
		}
		total_distance += 2000;
		dataToAR = new JSONObject();
		try {
			// JSONData.put("id", userId);
			dataToAR.put("area", area);
			dataToAR.put("flag", 1);
			dataToAR.put("rid", uuid);
			dataToAR.put("sid", sid);
			dataToAR.put("len", 2000);
			dataToAR.put("acc", acctime);
			dataToAR.put("dec", dectime);
			dataToAR.put("turn", turntime);
			dataToAR.put("over", overtime);
			dataToAR.put("tired", 0);
			dataToAR.put("slide", 0);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DataToAR_list.add(dataToAR);
	}

	// AR行程段数据 真实数据
	private void setDataToAR(double distance) {
		int area = Integer.parseInt(userInfo.getArea());
		dataToAR = new JSONObject();
		try {
			// JSONData.put("id", userId);
			dataToAR.put("area", area);
			dataToAR.put("flag", userInfo.getFlag());
			dataToAR.put("rid", uuid);
			dataToAR.put("sid", sid);
			dataToAR.put("len", distance);
			dataToAR.put("acc", strokesAR.getAcc());
			dataToAR.put("dec", strokesAR.getDec());
			dataToAR.put("turn", strokesAR.getTurn());
			dataToAR.put("over", strokesAR.getSpeeds());
			dataToAR.put("tired", strokesAR.getTires());
			dataToAR.put("slide", strokesAR.getChanges());
			//设置完行程段数据之后，将事件数据重置以重新计算
			strokesAR.setAcc(0);
			strokesAR.setDec(0);
			strokesAR.setTurn(0);
			strokesAR.setSpeeds(0);
			strokesAR.setTires(0);
			strokesAR.setChanges(0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DataToAR_list.add(dataToAR);
	}

	// AR总行程数据
	private String setAllDataToAR_simulator() {
		String UserID = userInfo.getUserID();

		// 创建json格式的数据
		allDataToAR = new JSONObject();
		JSONObject data1 = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			allDataToAR.put("area", 30); // int32，用户区域。
			allDataToAR.put("flag", 2);// int32，用户标志，0，临时账号，1，正式账号，2，VIP账号
			allDataToAR.put("rid", uuid);// 行程id
			allDataToAR.put("len", total_distance);// 单位米，总里程数。
			allDataToAR.put("tm", 2000);// int32，单位秒，行驶耗时。
			allDataToAR.put("avgspd", 60);// int32，单位米/秒，平均速度。
			allDataToAR.put("mxspd", 120);// 单位米/秒，最大速度
			allDataToAR.put("stm", strokes.getStart_time());// 单位秒，行程开始时间戳
			allDataToAR.put("etm", strokes.getEnd_time());// 单位秒，行程结束时间戳
			allDataToAR.put("slo", 120.00);// 行程开始经纬度
			allDataToAR.put("sla", 25.0);//
			allDataToAR.put("elo", 122.00);//
			allDataToAR.put("ela", 15.00);
			allDataToAR.put("acc", total_acctime);// 本行程急加速次数总和
			allDataToAR.put("dec", total_dectime);// 本行程急减速次数总和
			allDataToAR.put("turn", total_turntime);// 本行程急转弯次数总和
			allDataToAR.put("over", total_overtime);// 本行程超速次数总和。
			allDataToAR.put("tired", 0);// 本行程疲劳驾驶次数总和。
			allDataToAR.put("slide", 0);// 本行程急变道次数总和。
			data1 = allDataToAR;
			data1.put("id", UserID);
			data.put("data", data1);
			return data.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

	// AR总行程数据
	private String setAllDataToAR() {
		int area = Integer.parseInt(userInfo.getArea());
		String UserID = userInfo.getUserID();
		// 创建json格式的数据
		allDataToAR = new JSONObject();
		JSONObject data1 = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			allDataToAR.put("area", area); // int32，用户区域。
			allDataToAR.put("flag", userInfo.getFlag()); // int32，用户标志，0，临时账号，1，正式账号，2，VIP账号
			allDataToAR.put("rid", uuid); // 行程id
			allDataToAR.put("len", strokesAR.getMileages()); // 单位米，总里程数。
			allDataToAR.put("tm", strokesAR.getSeconds()); // int32，单位秒，行驶耗时。
			allDataToAR.put("avgspd", strokesAR.getVspeed()); // int32，单位米/秒，平均速度。
			allDataToAR.put("mxspd", strokesAR.getMax()); // 单位米/秒，最大速度
			allDataToAR.put("stm", strokesAR.getStart_time()); // 单位秒，行程开始时间戳
			allDataToAR.put("etm", strokesAR.getEnd_time()); // 单位秒，行程结束时间戳
			allDataToAR.put("slo", strokesAR.getSlon()); // 行程开始经纬度
			allDataToAR.put("sla", strokesAR.getSlat());
			allDataToAR.put("elo", strokesAR.getElon());
			allDataToAR.put("ela", strokesAR.getElat());
			allDataToAR.put("acc", strokes.getAcc()); // 本行程急加速次数总和
			allDataToAR.put("dec", strokes.getDec()); // 本行程急减速次数总和
			allDataToAR.put("turn", strokes.getTurn()); // 本行程急转弯次数总和
			allDataToAR.put("over", strokes.getSpeeds()); // 本行程超速次数总和。
			allDataToAR.put("tired", strokes.getTires()); // 本行程疲劳驾驶次数总和。
			allDataToAR.put("slide", 0); // 本行程急变道次数总和。
			data1 = allDataToAR;
			data1.put("id", UserID);
			data.put("data", data1);
			return data.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

	// 获取UUID
	private String getMyUUID() {
		UUID uuid = UUID.randomUUID();
		String uniqueId = uuid.toString();
		return uniqueId;
	}

	private class MyMKMapViewListener implements MKMapViewListener {

		@Override
		public void onClickMapPoi(MapPoi arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetCurrentMap(Bitmap arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMapAnimationFinish() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMapLoadFinish() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onMapMoveFinish() {
			// TODO Auto-generated method stub
			GeoPoint movedCenter = mMapView.getMapCenter();
			if (preCenter != null
					&& JourneyTool.getDistance(preCenter.getLatitude(),
							preCenter.getLongitude(),
							movedCenter.getLatitudeE6() / 1E6,
							movedCenter.getLongitudeE6() / 1E6) > 2) {
				Log.d(TAG, "滑动的距离大于2km，重新查一次众包数据");
				handler.removeCallbacks(mTrafficSourceRunnable);
				handler.post(mTrafficSourceRunnable);
				preCenter.setLatitude(movedCenter.getLatitudeE6() / 1E6);
				preCenter.setLongitude(movedCenter.getLongitudeE6() / 1E6);
			}
			GeoPoint loc = new GeoPoint((int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6));
			myLocationOverlay
					.setLocationMode(MyLocationOverlay.LocationMode.NORMAL);
			if (movedCenter != loc) {
				btn_getLocation.setEnabled(true);
				btn_getLocation.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "btn_getlocation_bg"));
				if (mapMoveRunnable != null)
					handler.removeCallbacks(mapMoveRunnable);
				handler.postDelayed(mapMoveRunnable, 10 * 1000);
			}
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		handler.removeCallbacks(mTrafficSourceRunnable);
        mMapView.getOverlays().remove(myLocationOverlay);
        myLocationOverlay = null;
		mMapView.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		isFirstLoc = savedInstanceState.getBoolean("isFirstLoc");
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		isFirstLoc = false;
		outState.putBoolean("isFirstLoc", isFirstLoc);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		initmap();
		mMapView.onResume();
		mMapView.setEnabled(true);
		super.onResume();
		if(mLocClient!=null){
			mLocClient.start();
			mLocClient.requestLocation();
		 }
		if (!ifGPSOpen()) {
			createDialog();
		}
		if (isFirstLoc||isRequest) {
			mMapController.animateTo(new GeoPoint(
					(int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6)));
		}
		handler.removeCallbacks(mTrafficSourceRunnable);
		handler.postDelayed(mTrafficSourceRunnable, 3 * 1000);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mLocClient != null) {
			mLocClient.stop();
			mLocClient = null;
		}
		super.onDestroy();
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		if (mapMoveRunnable != null) {
			handler.removeCallbacks(mapMoveRunnable);
			mapMoveRunnable = null;
		}
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
		preCenter = null;
		preLocation = null;
		if(allSensors!=null) {
			allSensors.clear();
			allSensors = null;
		}
		if(mItems!=null) {
			mItems.clear();
			mItems = null;
		}
		if(mTCItems!=null) {
			mTCItems.clear();
			mTCItems = null;
		}
		myLocationOverlay = null;
		gpsDataList = null;
		mLineAcceList = null;
		mGravityList = null;
		mGyroscopList = null;
		mOrientationList = null;
		if(gpsDataListToAr!=null) {
			gpsDataListToAr.clear();
			gpsDataListToAr = null;
		}
		gpsDataListToSend = null;
		handler.removeCallbacks(mTimeOutRunnable);
		SharedPreferences sh = getSharedPreferences("lastLocation",
				Activity.MODE_PRIVATE);
		sh.edit().putString("lat", locData.latitude + "").commit();
		sh.edit().putString("lon", locData.longitude + "").commit();
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (mMapView != null) {
			mMapView.destroy();
			mMapView = null;
		}
		cyx_MyApplication.getInstance().removeActivity(this);
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
			// LogUtil.printLog(LogUtil.DEBUG, TAG, "MySensorEventListener");
			MotionData data = new MotionData();
			data.mx = event.values[0];
			data.my = event.values[1];
			data.mz = event.values[2];
			data.time = (int) ((System.currentTimeMillis() - startTime));
			switch (event.sensor.getType()) {
			case Sensor.TYPE_GYROSCOPE: // 陀螺仪
				if(mGyroscopList == null)
					mGyroscopList = new LinkedList<MotionData>();
				mGyroscopList.add(data);
				break;
			case Sensor.TYPE_LINEAR_ACCELERATION:
			case Sensor.TYPE_ACCELEROMETER:
				if (isLineAccFirstData
						&& (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)) { // 线性加速度第一次获取数据
					if (!(isLineAccDataAvailable(data.mx)
							&& isLineAccDataAvailable(data.my) && isLineAccDataAvailable(data.mz))) { // 如果出现不正常的数据，则将线性加速度改为加速度并重新注册
						mSensorManager.unregisterListener(
								mySensorEventListener, mLineAcceSensor);
						mLineAcceList.clear();
						if (null != sensorArray
								&& DriveBh.SENSOR_EXIST == sensorArray[10]) {
							mLineAcceSensor = mSensorManager
									.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // 加速度
						}
						mSensorManager.registerListener(mySensorEventListener,
								mLineAcceSensor,
								getRateByLevel(sensorRateArray[1]));
					}
					isLineAccFirstData = false;
				}
				if(mLineAcceList == null) 
					mLineAcceList= new LinkedList<MotionData>();
				mLineAcceList.add(data);
				break;
			case Sensor.TYPE_GRAVITY:
			  if(mGravityList!=null&&data!=null)
				  if(mGravityList == null) 
					  mGravityList = new LinkedList<MotionData>();
				 mGravityList.add(data);
				break;
			case Sensor.TYPE_ORIENTATION:
				if(mOrientationList == null)
					mOrientationList = new LinkedList<MotionData>();
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
			// case Sensor.TYPE_RELATIVE_HUMIDITY:
			// break;

			}
			data = null;
		}

		// 用于判断线性加速度的值是否正常
		private boolean isLineAccDataAvailable(float data) {
			if (data >= 0 && data < 50)
				return true;
			return false;
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

	}

	private class MyRunnable implements Runnable {
		public void run() {
			handler.postDelayed(this, delayMillis);
			// 将list转换为数组
			if (mLineAcceList.size() > 500)
				mLineAcceList = mLineAcceList.subList(0, 499);
			mLineAcce = mLineAcceList.toArray(new MotionData[mLineAcceList
					.size()]);
			if (mGyroscopList.size() > 500)
				mGyroscopList = mGyroscopList.subList(0, 499);
			mGyroscop = mGyroscopList.toArray(new MotionData[mGyroscopList
					.size()]);
			if (mOrientationList.size() > 500)
				mOrientationList = mOrientationList.subList(0, 499);
			mOrientation = mOrientationList
					.toArray(new MotionData[mOrientationList.size()]);
			if (mGravityList.size() > 500)
				mGravityList = mGravityList.subList(0, 499);
			mGravity = mGravityList
					.toArray(new MotionData[mGravityList.size()]);
			mGPSData = gpsDataList.toArray(new GPSData[gpsDataList.size()]);
			mGyroscopList.clear();
			mGravityList.clear();
			mOrientationList.clear();
			mLineAcceList.clear();
			gpsDataList.clear();
			callJNI();
			if (ifNeedAdjust) {
				boolean hasAdjust = false; // 用于标志是否有传感器刚修改过工作频率
				hasAdjust |= adjustRote(mGyroscop.length, 0, mGyroscopSensor); // 判断和调整陀螺仪的工作频率
				hasAdjust |= adjustRote(mLineAcce.length, 1, mLineAcceSensor); // 判断和调整加速度/线性加速度的工作频率
				hasAdjust |= adjustRote(mGravity.length, 2, mGravitySensor); // 判断和调整重力的工作频率
				hasAdjust |= adjustRote(mOrientation.length, 3,
						mOrientationSensor); // 判断和调整方向感应器的工作频率
				ifNeedAdjust &= hasAdjust; // 如果有修改过，则下次还需判断是否需要调整，如果无修改，下次则无需再调整
			}
			mLineAcce = null;
			mGyroscop = null;
			mOrientation = null;
			mGravity = null;
			mGPSData = null;
		}

	}

	/**
	 * 调整传感器工作频率
	 * 
	 * @param length
	 *            ，传感器对应的list的size
	 * @param position
	 *            ，传感器对应的sensorRateArray的位置
	 * @param sensor
	 *            ，传感器
	 * @return 有调整则返回true，无需调整返回false
	 */
	private boolean adjustRote(int length, int position, Sensor sensor) {
		if (sensor == null) // 如果该传感器为空，直接返回false
			return false;
		if (DriveBh.FASTER_THEN_DEFINE == getRateCompareToDefine(length,
				sensorRateArray[position])) {
			if (sensorRateArray[position] != DriveBh.SENSOR_SLOW) {
				sensorRateArray[position] -= 1;
				resetRegisterListener(sensor, sensorRateArray[position]);
				LogUtil.printLog(LogUtil.DEBUG, TAG, sensor.getName()
						+ "---降低了频率 到 " + sensorRateArray[position]);
				return true;
			} else {
				return false;
			}
		} else if (DriveBh.SLOWER_THEN_DEFINE == getRateCompareToDefine(length,
				sensorRateArray[position])) {
			if (sensorRateArray[position] != DriveBh.SENSOR_FASTEST) {
				sensorRateArray[position] += 1;
				resetRegisterListener(sensor, sensorRateArray[position]);
				LogUtil.printLog(LogUtil.DEBUG, TAG, sensor.getName()
						+ "---升高了频率 到 " + sensorRateArray[position]);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private void callJNI() {
		// 分别调用
		System.out.println("---------- mLineAcce " + mLineAcce.length);
		System.out.println("---------- mGravity " + mGravity.length);
		System.out.println("---------- mGyroscop " + mGyroscop.length);
		System.out.println("---------- mOrientation " + mOrientation.length);
		DriveBh.accDataHandler(mLineAcce);
		DriveBh.gravityDataHandler(mGravity);
		DriveBh.gyroDataHandler(mGyroscop);
		DriveBh.orientationDataHandler(mOrientation);
		DriveBh.gpsDataHandler(mGPSData);
	}

	// 驾驶行为图层
	private OverlayItem mCurItem = null;

	public class MyOverlay extends ItemizedOverlay<OverlayItem> {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index) {
			OverlayItem item = getItem(index);
			mCurItem = item;
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			return false;
		}

	}

	// 驾驶行为图层
	public class MyTrafficConditionOverlay extends ItemizedOverlay<OverlayItem> {

		public MyTrafficConditionOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index) {
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			return false;
		}

	}

	/**
	 * 驾驶过程中发生不良驾驶行为时在地图上插上小旗子
	 * 
	 * @param type
	 *            类型
	 * @param point
	 *            位置点
	 */
	private void setDBMarker(int type, GeoPoint point) {
		switch (type) {
		// 急加速
		case DriveBh.HARDACCL:
			if (point != null) {
				OverlayItem item = new OverlayItem(point,
						"", "");
				item.setMarker(getResources()
						.getDrawable(
								MResource.getDrawableId(
										getApplicationContext(), "jijiasu")));
				mOverlay.addItem(item);
			}
			mMediaPlayer = MediaPlayer
					.create(cyx_MainActivity.this, MResource.getRawID(
							getApplicationContext(), "speedupalarm"));
			mMediaPlayer.start();
			break;
		// 急减速
		case DriveBh.HARDBRAK:
			if (point != null) {
				OverlayItem item2 = new OverlayItem(point,
					"", "");
				item2.setMarker(getResources()
						.getDrawable(
								MResource.getDrawableId(
										getApplicationContext(), "jishache")));
				mOverlay.addItem(item2);
			}
			mMediaPlayer = MediaPlayer.create(cyx_MainActivity.this, MResource
					.getRawID(getApplicationContext(), "sloweddownalarm"));
			mMediaPlayer.start();
			break;
		// 急转弯
		case DriveBh.HARDTURN:
			if (point != null) {
				OverlayItem item3 = new OverlayItem(point,
						"", "");
				item3.setMarker(getResources().getDrawable(
						MResource.getDrawableId(getApplicationContext(),
								"jizhuanwan")));
				mOverlay.addItem(item3);
			}
			mMediaPlayer = MediaPlayer.create(cyx_MainActivity.this, MResource
					.getRawID(getApplicationContext(), "quickturnalarm"));
			mMediaPlayer.start();
			break;
			// 超速
			case DriveBh.OVERSPEED:
						if (point != null) {
							OverlayItem item4 = new OverlayItem(point,
									"", "");
							item4.setMarker(getResources().getDrawable(
									MResource.getDrawableId(getApplicationContext(),
											"chaosu")));
							mOverlay.addItem(item4);
						}
						mMediaPlayer = MediaPlayer.create(cyx_MainActivity.this, MResource
								.getRawID(getApplicationContext(), "overspeedalarm"));
						mMediaPlayer.start();
						break;
			// 疲劳驾驶
			case DriveBh.FATIGUEDRIVING:
				if (point != null) {
							OverlayItem item5 = new OverlayItem(point,
									"", "");
							item5.setMarker(getResources().getDrawable(
									MResource.getDrawableId(getApplicationContext(),
											"pilaojiashi")));
							mOverlay.addItem(item5);
					}
				mMediaPlayer = MediaPlayer.create(cyx_MainActivity.this, MResource
								.getRawID(getApplicationContext(), "fatiguedrivingalarm"));
				mMediaPlayer.start();
				break;
		}
		if (mOverlay.size() > 0) {
			mItems.addAll(mOverlay.getAllItem());
			mMapView.refresh();
		}
	}

	/**
	 * 在地图中添加交通状况图标
	 * 
	 * @param type
	 *            交通状况类型 0 堵塞 1 事故 2 施工 3 管制
	 * @param point
	 *            位置
	 */
	private void setTrafficCondition(Crowd[] crowds) {
		GeoPoint point;
		double[] crowdLocation;
		int type;
		for (int i = 0, length = crowds.length; i < length; i++) {
			crowdLocation = Coordinate.wgtobaidu(crowds[i].getLon(),
					crowds[i].getLat());
			point = new GeoPoint((int) (crowdLocation[1] * 1e6),
					(int) (crowdLocation[0] * 1e6));
			type = Integer.valueOf(crowds[i].getType());
			switch (type) {
			// 交通堵塞
			case ConstantContext.TRAFFIC_JAM:
				OverlayItem item = new OverlayItem(point,
						ConstantContext.TRAFFIC_JAM_DES, "");
				item.setMarker(getResources().getDrawable(
						MResource.getDrawableId(getApplicationContext(),
								"jam_pin")));
				mTrafficConditionOverlay.addItem(item);
				break;
			// 交通事故
			case ConstantContext.TRAFFIC_ACCIDENT:
				OverlayItem item2 = new OverlayItem(point,
						ConstantContext.TRAFFIC_ACCIDENT_DES, "");
				item2.setMarker(getResources().getDrawable(
						MResource.getDrawableId(getApplicationContext(),
								"accident_pin")));
				mTrafficConditionOverlay.addItem(item2);
				break;
			// 施工
			case ConstantContext.TRAFFIC_CONSTRUCTION:
				OverlayItem item3 = new OverlayItem(point,
						ConstantContext.TRAFFIC_CONSTRUCTION_DES, "");
				item3.setMarker(getResources().getDrawable(
						MResource.getDrawableId(getApplicationContext(),
								"work_pin")));
				mTrafficConditionOverlay.addItem(item3);
				break;
			// 交通管制
			case ConstantContext.TRAFFIC_MASTER:
				OverlayItem item4 = new OverlayItem(point,
						ConstantContext.TRAFFIC_MASTER_DES, "");
				item4.setMarker(getResources().getDrawable(
						MResource.getDrawableId(getApplicationContext(),
								"police_pin")));
				mTrafficConditionOverlay.addItem(item4);
				break;
			}
		}
		mTCItems.clear();
		mMapView.getOverlays().remove(mTrafficConditionOverlay);
		mMapView.refresh();
		mTCItems.addAll(mTrafficConditionOverlay.getAllItem());
		mMapView.getOverlays().add(mTrafficConditionOverlay);
		mMapView.refresh();
	}

	// 根据传感器设定的工作频率水平和预先设定的频率进行比较得出快慢。
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

	@Override
	public void onDriveBhHappened(int eventType, GPSData gpsData,
			SensorData sensorData) {
		if (eventType == 4 || eventType == 5 || eventType == 6 || eventType == 7 || eventType == 8 || eventType == 9) {	//7超速 8疲劳驾驶 9急变道(暂定)
			String type = "0";
			if (gpsData != null) {
				double[] baidus = Coordinate.wgtobaidu(gpsData.longitude,
						gpsData.latitude);
				GeoPoint point = new GeoPoint((int) (baidus[1] * 1e6),
						(int) (baidus[0] * 1e6));
				setDBMarker(eventType, point);
				if (isSimulate == 0) {
					eventType = 4;
				}
			} else {
				setDBMarker(eventType, new GeoPoint((int)(locData.latitude*1e6), (int)(locData.longitude*1e6)));
			}
			if (eventType == 4) {
				type = "8";// 急减速
				strokes.setDec(strokes.getDec() + 1);
				strokesAR.setDec(strokesAR.getDec() + 1);
			} else if (eventType == 5) {
				type = "6";// 急加速
				strokes.setAcc(strokes.getAcc() + 1);
				strokesAR.setAcc(strokesAR.getAcc() + 1);
			} else if (eventType == 6) {
				type = "7";// 急转弯
				strokes.setTurn(strokes.getTurn() + 1);
				strokesAR.setTurn(strokesAR.getTurn() + 1);
			} else if(eventType == 7) {
				type = "22";	//超速
				strokes.setSpeeds(strokes.getSpeeds() + 1);
				strokesAR.setSpeeds(strokesAR.getSpeeds() + 1);
			} else if(eventType == 8) {
				type = "21";	//疲劳驾驶
				strokes.setTires(strokes.getTires() + 1);
				strokesAR.setTires(strokesAR.getTires() + 1);
			} else if(eventType ==9) {
				type = "23";	//急变道
				strokes.setChanges(strokes.getChanges() + 1);
				strokesAR.setChanges(strokesAR.getChanges() + 1);
			}
			try {
				if (gpsData != null) {
					eventPosition.put("times", TimeUtil.dateLongFormatString(gpsData.time*1000, TimeUtil.format1));
					eventPosition.put("lon", gpsData.longitude);
					eventPosition.put("lat", gpsData.latitude);
					eventPosition.put("speed", gpsData.speed);
					eventPosition.put("height", gpsData.altitude);
					eventPosition.put("direction", gpsData.direction);
					eventPosition.put("state", 0);
				}else{
					if(locData != null) {
						double[] owg = Coordinate.baidutowg(locData.longitude,
								locData.latitude);
						eventPosition.put("times", TimeUtil.parseToUTC(TimeUtil.dateLongFormatString(System.currentTimeMillis(), TimeUtil.format1)));
						eventPosition.put("lon", owg[0]);
						eventPosition.put("lat", owg[1]);
						eventPosition.put("speed", 0);
						eventPosition.put("height", 0);
						eventPosition.put("direction", 0);
						eventPosition.put("state", 1);
					} else {
						//GPS没有数据,数据设为无效
						eventPosition.put("state", 0);
					}
				}
				if (sensorData != null) {
					eventSenser.put("s_type", sensorData.sensorType);
					eventSenser.put("motionDatas", sensorData.motionDatas);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			eventSenser_list.add(eventSenser);
			eventPosition_list.add(eventPosition);
			type_list.add(type);

		}
	}

	// 重新注册传感器监听
	boolean resetRegisterListener(Sensor sensorName, int level) {
		mSensorManager.unregisterListener(mySensorEventListener, sensorName);
		return mSensorManager.registerListener(mySensorEventListener,
				sensorName, getRateByLevel(level));
	}

	// 获得传感器数组，标志某个传感器是否可用
	byte[] getSensorArray() {
		allSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);// 获得传感器列表
		if (allSensors == null || allSensors.size() == 0) {
			return sensorArray;
		}
		sensorArray = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		Sensor sensor;
		for (int i = 0, size = allSensors.size(); i < size; i++) {
			sensor = allSensors.get(i);
			switch (sensor.getType()) {
			case Sensor.TYPE_GYROSCOPE:
				Log.d(TAG, "陀螺仪感应器");
				sensorArray[0] = DriveBh.SENSOR_EXIST;
				break;
			case Sensor.TYPE_LINEAR_ACCELERATION:
				Log.d(TAG, "线性加速度感应器");
				sensorArray[1] = DriveBh.SENSOR_EXIST;
				break;
			case Sensor.TYPE_GRAVITY:
				Log.d(TAG, "重力感应器");
				sensorArray[2] = DriveBh.SENSOR_EXIST;
				break;
			case Sensor.TYPE_ORIENTATION:
				Log.d(TAG, "方向感应器");
				sensorArray[3] = DriveBh.SENSOR_EXIST;
				break;
			case Sensor.TYPE_LIGHT:
				Log.d(TAG, "光线感应器");
				sensorArray[4] = DriveBh.SENSOR_EXIST;
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				Log.d(TAG, "磁力感应器");
				sensorArray[5] = DriveBh.SENSOR_EXIST;
				break;
			case Sensor.TYPE_PRESSURE:
				Log.d(TAG, "压力感应器");
				sensorArray[6] = DriveBh.SENSOR_EXIST;
				break;
			// case Sensor.TYPE_AMBIENT_TEMPERATURE:
			case Sensor.TYPE_TEMPERATURE:
				Log.d(TAG, "温度感应器");
				sensorArray[7] = DriveBh.SENSOR_EXIST;
				break;
			case Sensor.TYPE_ROTATION_VECTOR:
				Log.d(TAG, "旋转矢量感应器");
				sensorArray[8] = DriveBh.SENSOR_EXIST;
				break;
			case Sensor.TYPE_PROXIMITY:
				Log.d(TAG, "接近感应器");
				sensorArray[9] = DriveBh.SENSOR_EXIST;
				break;
			case Sensor.TYPE_ACCELEROMETER:
				Log.d(TAG, "加速度感应器");
				sensorArray[10] = DriveBh.SENSOR_EXIST;
				break;
			// case Sensor.TYPE_RELATIVE_HUMIDITY:
			// Log.d(TAG, "湿度感应器");
			// sensorArray[11] = 1;
			// break;
			}
		}
		allSensors = null;
		return sensorArray;
	}

	// 根据sensorRateArray里的level得到对应的工作频率
	int getRateByLevel(int level) {
		switch (level) {
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

	private void startDriving() {
		if (DriveBh.RESULT_SUCCEED != DriveBh.initDriveBhLib(getSensorArray())) {
			Toast.makeText(cyx_MainActivity.this, "初始化采集库失败",
					Toast.LENGTH_SHORT).show();
			return;
		}
		isThreadalive = true;
		isCollectGPS = true;
		startTime = System.currentTimeMillis();
		strokes.setStart_time((int) (startTime / 1000));
		strokesAR.setStart_time((int) (startTime / 1000));
		// 初始化list
		mLineAcceList = new ArrayList<MotionData>();
		mGravityList = new ArrayList<MotionData>();
		mGyroscopList = new ArrayList<MotionData>();
		mOrientationList = new ArrayList<MotionData>();

		// 查找到服务信息
		DriveBhlibParameter parameter = new DriveBhlibParameter();
		if (DriveBh.RESULT_SUCCEED == DriveBh.getDriveBhParameter(parameter)) {
			sensorRateArray = parameter.sensorRateArray;

			delayMillis = parameter.intervalForHandler;
			if (delayMillis <= 0) {
				delayMillis = 1000;
			} else if (delayMillis > 60 * 60 * 1000) {
				delayMillis = 60 * 60 * 1000;
			}

			handler.removeCallbacks(myRunnable);
			handler.postDelayed(myRunnable, delayMillis);

		}
		uuid = getMyUUID();
	}

	private void stopDriving() {
		isThreadalive = false ;
		isFinish = true;
		isCollectGPS = false;
		isEndFlag = true;
		isfirsttime_a = 0;
		coin_num = 0;
		
		String message;
		datasToUpload();
		datasToUpload_AR();
		if (isSimulate == 0) {
			message = setAllDataToAR_simulator();
		} else {
			message = setAllDataToAR();
		}
		if (mapMoveRunnable != null) {
			handler.removeCallbacks(mapMoveRunnable);
			mapMoveRunnable = null;
		}
		// 解除传感器监听
		mSensorManager.unregisterListener(mySensorEventListener);
		handler.removeCallbacks(myRunnable);
		mLocClient.stop();
		DriveBh.uninitDriveBhLib();
		if (isSimulate == 0) {
			saveStrokeResult_simulate(strokes);
		} else {
			if(strokes.getMileages() >= 2000)
			saveStrokeResult(strokes);
		}
	
		if (ExternalInterfaceAR != null)
			ExternalInterfaceAR.OnFinishDriving(message, "", 1);
		
		cyx_MainActivity.this.finish();
		
	}

	// 上传补偿点
	private void uploadTurns() {
		JSONArray positionAry = new JSONArray();
		try {
			if (gpsDataListToSend.size() > 0) {
				for (int i = 0; i < gpsDataListToSend.size(); i++) {
					Log.d("gadguiwaae", "gpsDataListToSend.size()==="
							+ gpsDataListToSend.size());

					JSONObject pos = new JSONObject();
					pos.put("height", gpsDataListToSend.get(i).altitude);
					pos.put("lat", gpsDataListToSend.get(i).latitude);
					pos.put("lon", gpsDataListToSend.get(i).longitude);

					pos.put("times", TimeUtil.dateLongFormatString(
							gpsDataListToSend.get(i).time * 1000,
							TimeUtil.format1));
					pos.put("speed", gpsDataListToSend.get(i).speed);
					pos.put("direction", gpsDataListToSend.get(i).direction);
					pos.put("state", 1);
					positionAry.put(i, pos);
				}
				positionAry_list.add(positionAry);
				gpsDataListToSend.clear();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private class MyTrafficSourceRunnable implements Runnable {

		@Override
		public void run() {
			handler.postDelayed(this, 5 * 60 * 1000);
			if (preLocation != null) {
				double[] mOwg = Coordinate.baidutowg(
						preLocation.getLongitude(), preLocation.getLatitude());
				Connection.getInstance()
						.sendExtData(
								new ExtraDataProcess().getTCSData(mOwg[0],
										mOwg[1], "0"), new RequestCallback() {

									@Override
									public void onSuccess(String bizJsonData) {
										// TODO Auto-generated method stub
										try {
											JSONObject jsonObj = new JSONObject(
													bizJsonData);
											switch(jsonObj.getInt("result")) {
											case ConstantContext.SUCCESS :
												String crowdsStr = jsonObj
														.getString("crowds");
												if (!TextUtils
														.isEmpty(crowdsStr)) {
													Gson gson = new Gson();
													Crowd[] crowds = gson
															.fromJson(
																	crowdsStr,
																	new TypeToken<Crowd[]>() {
																	}.getType());
													if (crowds.length == 0)
														return;
													setTrafficCondition(crowds);
												}
												break;
											case ConstantContext.ERROR_1:	//位置信息不完整
												break;
											case ConstantContext.ERROR_2:	//比例尺级别为空
												break;
											case ConstantContext.ERROR_3:	//比例尺级别错误
												break;
											case ConstantContext.ERROR_4:	//查询缓存异常
												break;
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}

									@Override
									public void onFailed(int reason) {
										// TODO Auto-generated method stub
									}

								});
			}
		}

	}

	// 保存行程数据到内存中
	private void saveStrokeResult(Stroke stroke) {
		StrokeResult result = new StrokeResult();
		result.setMileages(stroke.getMileages());
		result.setTimes(stroke.getSeconds());
		result.setAece(stroke.getAcc());
		result.setDece(stroke.getDec());
		result.setSpeeds(stroke.getSpeeds());
		result.setTires(stroke.getTires());
		result.setChanges(0);	//暂未实现
		result.setAve(stroke.getVspeed());
		result.setBegin(TimeUtil.dateLongFormatString(
				(long) stroke.getStart_time() * 1000, TimeUtil.format1));
		result.setEnd(TimeUtil.dateLongFormatString(
				(long) stroke.getEnd_time() * 1000, TimeUtil.format1));
		result.setStroke(stroke.getStroke());
		result.setStartlat(stroke.getSlat());
		result.setStartlon(stroke.getSlon());
		result.setEndlat(stroke.getElat());
		result.setEndlon(stroke.getElon());
		result.setMax(stroke.getMax());
		result.setTurns(strokes.getTurn());
		cyx_MyApplication.getStrokeDataList().add(0, result);
	}

	// 保存模拟行程数据到内存中
	private void saveStrokeResult_simulate(Stroke strokes) {
		StrokeResult result = new StrokeResult();
		result.setMileages(total_distance);
		result.setTimes(2400);
		result.setAece(total_acctime);
		result.setDece(total_dectime);
		result.setAve(total_overtime);
		result.setSpeeds(0);
		result.setTires(0);
		result.setChanges(0);	//暂未实现
		result.setBegin(TimeUtil.dateLongFormatString(
				(long) strokes.getStart_time() * 1000, TimeUtil.format1));
		result.setEnd(TimeUtil.dateLongFormatString(
				(long) strokes.getEnd_time() * 1000, TimeUtil.format1));
		result.setStroke(strokes.getStroke());
		result.setStartlat(120.00);
		result.setStartlon(25.0);
		result.setEndlat(122.00);
		result.setEndlon(15.00);
		result.setMax(120);
		result.setTurns(total_turntime);
		cyx_MyApplication.getStrokeDataList().add(result);
	}

	// 自定义对话框
	private void createDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = new cyx_CustomAlertDialog(cyx_MainActivity.this);
		mDialog.setTitle(getString(MResource.getStringId(
				getApplicationContext(), "notice")));
		mDialog.setMessage(getString(MResource.getStringId(
				getApplicationContext(), "open_gps_notice")));
		mDialog.setNumberVisible(false);
		mDialog.showLine();
		mDialog.setPositiveButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
				Intent intent = new Intent();
				intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					intent.setAction(android.provider.Settings.ACTION_SETTINGS);
					try {
						startActivity(intent);
					} catch (Exception e) {
					}
				}
			}
		});
		mDialog.setNegativeButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
				mDialog = null;
			}
		});
	}

	private boolean ifGPSOpen() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	class MyMKMapStatusChangeListener implements MKMapStatusChangeListener {

		@Override
		public void onMapStatusChange(MKMapStatus status) {
			// TODO Auto-generated method stub
			if (status.zoom == mMapView.getMaxZoomLevel()) {
				btnZoomin.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "ic_zoomin"));
				btnZoomin.setEnabled(false);
			} else {
				btnZoomin.setEnabled(true);
				btnZoomin.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "btn_zoom_in"));
			}

			if (status.zoom == mMapView.getMinZoomLevel()) {
				btnZoomout.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "ic_zoomout"));
				btnZoomout.setEnabled(false);
			} else {
				btnZoomout.setEnabled(true);
				btnZoomout.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "btn_zoom_out"));
			}
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finishDialog();
	}

	private void setUploadPosition(JSONObject stroke, JSONObject position) {
		JSONObject js = new ExtraDataProcess().getUploadPositionData(
				userInfo.getUserID(), stroke, position);
		conn.sendPersistentData(js);
		int size = Jstrokes_list.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				Jposition_list.remove(i);
				Jstrokes_list.remove(i);
				--size;
			}
		}
	}

	private void setUploadEvent(String stroke, JSONObject position, int type,
			JSONObject detail) {
		JSONObject js = new ExtraDataProcess().getUploadEventData(
				userInfo.getUserID(), stroke, position, type, detail);
		if(userInfo.getFlag()!=0&&conn.getConnectionState()==Connection.CONN_STATE_LOGIN_OK){
			conn.sendPersistentData(js);
			int size = eventPosition_list.size();
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					eventPosition_list.remove(i);
					eventSenser_list.remove(i);
					type_list.remove(i);
					size--;
				}
			}
		}
	}

	private void setUploadTurns(String stroke, JSONArray positions) {
		JSONObject js = new ExtraDataProcess().getUploadTurnsData(
				userInfo.getUserID(), stroke, positions);
		if(userInfo.getFlag()!=0&&conn.getConnectionState()==Connection.CONN_STATE_LOGIN_OK){
			conn.sendPersistentData(js);
			int size = positionAry_list.size();
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					positionAry_list.remove(i);
					size--;
				}
		}
	  }
	}
}
