package com.cwits.cyx_drive_sdk.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cwits.cyx_drive_sdk.bean.AlarmData;
import com.cwits.cyx_drive_sdk.data.AlarmType;
import com.cwits.cyx_drive_sdk.data.IAlarmInfoManage;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;


public class cyx_FortifyAlarmActivity extends Activity implements SensorEventListener{
	
	public static final String ALARM_FIX_TIME = "alarm_fix_time";
	public static final String ALARM_DESCRIBE = "alarm_describe";
	private Button mButton01, mButton02;
	private TextView mTextView;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ListView listview;
	private PowerManager mypower;
	private WakeLock mywakelock;
	
	private static final int FORCE_THRESHOLD = 100;
	private static final int TIME_THRESHOLD = 100;
	private static final int SHAKE_TIMEOUT = 500;
	private static final int SHAKE_DURATION = 1200;
	private static final int SHAKE_COUNT = 2;

	private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
	private long mLastTime = 0;
	private int mShakeCount = 0;// 震动次数
	private long mLastShake;
	private Handler mhandler;

	private boolean SDK_VERSION = false;
	private List<GensorValue> Lx;
	private List<GensorValue> Ly;
	private List<GensorValue> Lz;
	private final int MXVALUE = 8;// 加速度门限值
	private final int CYTIME = 4;// 采样次数
	clickListener mcliListener = null;
	private IAlarmInfoManage obj;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		cyx_MyApplication.getInstance().addActivity(this);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_car_fortify_alarm_layout"));
		init();
	}
    private void init(){
    	listview = (ListView) findViewById(MResource.getID(getApplicationContext(), "alarmList"));
		mTextView = (TextView) findViewById(MResource.getID(getApplicationContext(),"alarm_title"));
		String brandName = getString(MResource.getStringId(getApplicationContext(), "app_name"));
		mTextView.setText(brandName + getString(MResource.getStringId(getApplicationContext(), "alarm_info")));
		mcliListener = new clickListener();
		obj = CYX_Drive_SDK.getInstance().getAlarmInfoManage();
		List<AlarmData> mlist = obj.getAlarmList();
		System.out.println("mlist.size:"+mlist.size());
		/*List<AlarmData> mlist = new ArrayList<AlarmData>();
		mlist.add(new AlarmData("", 1, AlarmType.FOTIFY_ALARM, 1,
				TimeUtil.dateLongFormatString(System.currentTimeMillis(),TimeUtil.format1)));*/
		updateData(mlist);
		mButton01 = (Button) findViewById(MResource.getID(getApplicationContext(), "alarmOk"));
		mButton02 = (Button) findViewById(MResource.getID(getApplicationContext(),"alarmCancel"));
		mButton01.setOnClickListener(mcliListener);
		mButton02.setOnClickListener(mcliListener);

		// 亮屏
		mypower = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mywakelock = mypower.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "MFA");
		mywakelock.acquire(1000 * 60);
		mywakelock.setReferenceCounted(false);

		Lx = new ArrayList<GensorValue>();
		Ly = new ArrayList<GensorValue>();
		Lz = new ArrayList<GensorValue>();
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		int sdk_int = android.os.Build.VERSION.SDK_INT;
		if (sdk_int < 9) {
			SDK_VERSION = false;
			mAccelerometer = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		} else {
			mAccelerometer = mSensorManager
					.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			SDK_VERSION = true;
			if (null == mAccelerometer) {
				SDK_VERSION = false;
				// 无线性加速度传感器 用加速度传感器
				mAccelerometer = mSensorManager
						.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			}
		}
		if (null == mAccelerometer) {
			// 不支持摇一摇功能
			Toast.makeText(this, getString(MResource.getStringId(getApplicationContext(), "alarm_nunsupport")), Toast.LENGTH_LONG).show();
		}
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI);
		// 第一次参数初始化
		mLastX = 0;
		mLastY = 0;
		mLastZ = 0;
    }
    private void updateData(final List<AlarmData> alarm) {

		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < alarm.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (AlarmType.FOTIFY_ALARM == alarm.get(i).getAlarmType()) {
				// 设防报警
				if (alarm.get(i).getAccStatus()==1) {
					map.put(ALARM_DESCRIBE,
							getString(MResource.getStringId(getApplicationContext(), "alarm_start")));
				} else{
					map.put(ALARM_DESCRIBE, getString(MResource.getStringId(getApplicationContext(), "alarm_Vibration")));
			    } 
			}else if(AlarmType.CRASH_ALARM == alarm.get(i).getAlarmType()){
				//碰撞报警
				map.put(ALARM_DESCRIBE, getString(MResource.getStringId(getApplicationContext(), "CRASH_ALARM")));
			}else if(AlarmType.ROLLOVER_ALARM == alarm.get(i).getAlarmType())
			{
				//侧翻报警
				map.put(ALARM_DESCRIBE, getString(MResource.getStringId(getApplicationContext(), "ROLLOVER_ALARM")));
			}
			map.put(ALARM_FIX_TIME, alarm.get(i).getFixTime());
			list.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(cyx_FortifyAlarmActivity.this,
				list, MResource.getLayoutId(getBaseContext(), "cyx_fortify_alarm_list"), new String[] {
						ALARM_DESCRIBE, ALARM_FIX_TIME },
				new int[] {MResource.getID(getApplicationContext(), "alarmDescribe"),
			MResource.getID(getApplicationContext(),"alarmTime") });

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(cyx_FortifyAlarmActivity.this, cyx_CarLocationActivity.class);
				startActivity(intent);
			}
		});

	}
    @Override
	protected void onResume() {
		super.onResume();
		if (0 != obj.getAlarmList().size()) {
			 obj.resumeMediaplay();
			// 启动定时器 报警声音为一分钟
			if (null == mhandler) {
				mhandler = new Handler();
			} else {
				mhandler.removeCallbacks(runable);
			}
			mhandler.postDelayed(runable, 60 * 1000);
		}
	}

	Runnable runable = new Runnable() {

		@Override
		public void run() {
			// 关闭声音
			obj.CancelMediaPlayer();
			mhandler = null;
		}
	};
    
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		long now = System.currentTimeMillis();
		if (SDK_VERSION) {
			for (int i = 0; i < 3; i++) {
				GensorValue gv = new GensorValue();
				gv.setVa(event.values[i]);
				gv.setC_time(now);
				switch (i) {
				case 0:
					if (processValue(gv, event.values[i], Lx, now)) {
						return;
					}
					break;
				case 1:
					if (processValue(gv, event.values[i], Ly, now)) {
						return;
					}
					break;
				case 2:
					if (processValue(gv, event.values[i], Lz, now)) {
						return;
					}
					break;

				default:
					break;
				}
			}
		} else {

			if ((now - mLastTime) > SHAKE_TIMEOUT) {
				mShakeCount = 0;
			}
			if ((now - mLastTime) > TIME_THRESHOLD) {
				long diff = now - mLastTime;
				float speed = (Math.abs(event.values[SensorManager.DATA_X]
						- mLastX)
						+ Math.abs(event.values[SensorManager.DATA_Y] - mLastY) + Math
						.abs(event.values[SensorManager.DATA_Z] - mLastZ))
						/ diff * 1000;
				if (speed > FORCE_THRESHOLD) {
					if ((++mShakeCount >= SHAKE_COUNT)
							&& (now - mLastShake > SHAKE_DURATION)) {
						mLastShake = now;
						mShakeCount = 0;
						if (obj.getMediaPlayerStatus()) // 如果正在播放音乐 关闭音乐
							obj.stopMediaplay();
						// 结束界面
						finish();
					}
				}
				mLastTime = now;
				mLastX = event.values[SensorManager.DATA_X];
				mLastY = event.values[SensorManager.DATA_Y];
				mLastZ = event.values[SensorManager.DATA_Z];
			}
		}
	}
	private boolean processValue(GensorValue gv, float value,
			List<GensorValue> lv, long now) {
		boolean ret = false;
		if (Math.abs(value) > MXVALUE) {
			if (0 == lv.size()) {
				lv.add(gv);
				return false;
			} else {
				if (fanxiang(value, lv.get(lv.size() - 1).getVa())) {
					lv.add(gv);
				}
			}

		}
		// 判断是否达到条件 退出
		if (lv.size() >= CYTIME) {
			if ((now - lv.get(0).getC_time()) < 3 * 1000) {
				// 满足要求
				if (obj.getMediaPlayerStatus()) // 如果正在播放音乐 关闭音乐
					obj.stopMediaplay();
				// 结束界面
				finish();
				ret = true;
			} else {
				lv.remove(0);
			}
		}
		return ret;
	}

	private boolean fanxiang(float a, float b) {
		boolean result = false;
		if (a > 0 && b < 0) {
			result = true;
		} else if (b > 0 && a < 0) {
			result = true;
		}
		return result;
	}

	public class GensorValue {
		float va;
		long c_time;

		public float getVa() {
			return va;
		}

		public void setVa(float va) {
			this.va = va;
		}

		public long getC_time() {
			return c_time;
		}

		public void setC_time(long c_time) {
			this.c_time = c_time;
		}

	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		cyx_MyApplication.getInstance().removeActivity(this);
		obj.CancelMediaPlayer();
//		// 清除报警列表
		obj.removeAllAlarmList();
		mSensorManager.unregisterListener(this);
		if (null != mhandler) {
			mhandler.removeCallbacks(runable);
			mhandler = null;
		}
		// 注：此处不能把mywakelock移动到onPause下，否则会出现设防报警点不亮屏问题
		if (null != mywakelock && mywakelock.isHeld()) {
			mywakelock.release();
		}
		super.onDestroy();
	}
	class clickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == MResource.getID(getApplicationContext(), "alarmOk")) {
				obj.CancelMediaPlayer();
			} else if (id == MResource.getID(getApplicationContext(), "alarmCancel")) {
				obj.CancelMediaPlayer();
				obj.removeAllAlarmList();
				finish();
			} else {
			}
			
		}
		
	}
	protected void onPause() {
		// 得到当前是否是锁屏状态 此处代码不能移到OnDestroy中
		if (null != mypower && !mypower.isScreenOn()) {
			if (null != mywakelock && mywakelock.isHeld()) {
				mywakelock.acquire(60 * 1000);
			}
		}

		super.onPause();
	}
	
}
