package com.cwits.cyx_drive_sdk.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ConstantContext;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;
import com.cwits.cyx_drive_sdk.util.Coordinate;

public class cyx_TrafficReport extends Activity implements OnClickListener{
	private ImageView jamBtn, accidentBtn, workBtn, policeBtn;// 事件类型按钮
	private Button report_Btn; // 上报按钮
	private String type; // 上报类型,0=堵车�?=事故,2=施工�?=执法
	private String reportContent = null;// 文字内容
	private String fileString = null; // 多媒体文件base64编码
	private String fileType = null; // 多媒体文件类�?
	private double lon; // 经度
	private double lat; // 纬度
	private String toastText = "";
	private Handler handler;
	ProgressDialog mProgressDialog;
	String UserID;
	boolean networkState = false; // 网络状态
	NetworkDetector networkDetector;
    UserInfo userInfo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题�?

		setContentView(MResource.getLayoutId(getApplicationContext(),
				"cyx_traffic_report"));
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数�?
		p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的1.0
		p.gravity = Gravity.BOTTOM;

		// getWindow().setAttributes((android.view.WindowManager.LayoutParams)
		// p); // 设置生效
        cyx_MyApplication.getInstance().addActivity(this);
		init();
	}

	@Override
	public void onClick(View v) {
		networkState = networkDetector.detect(this);
		// TODO Auto-generated method stub
		if (v.getId() == MResource.getID(getApplicationContext(), "report_btn")) {
			this.finish();

		} else {
			if (!networkState) {
				toastText = getResources().getString(
						MResource.getStringId(getApplicationContext(),
								"traffic_disable_network"));
				showToast();
			} else {
				// 登录才能上报
				if (CYX_Drive_SDK.getInstance().getConnection().getConnectionState()==Connection.CONN_STATE_LOGIN_OK&&
						!userInfo.getName().equals("temp")) {
					
					if (lon == 0.0 && lat == 0.0) {// 定位失败提示

						toastText = getResources().getString(
								MResource.getStringId(getApplicationContext(),
										"traffic_fail_locate"));
						showToast();
					}

					else {// 上报
						if (v.getId() == MResource.getID(
								getApplicationContext(), "jam_btn")) {
							type = "0";// 堵车

						} else if (v.getId() == MResource.getID(
								getApplicationContext(), "accident_btn")) {
							type = "1";// 事故

						} else if (v.getId() == MResource.getID(
								getApplicationContext(), "work_btn")) {
							type = "2";// 施工

						} else if (v.getId() == MResource.getID(
								getApplicationContext(), "police_btn")) {
							type = "3";// 执法

						}
						report();
					}
					
				} else {
					toastText = getResources().getString(
							MResource.getStringId(getApplicationContext(),
									"traffic_unlogin"));
					showToast();
				}
			}
		}
	}

	// �?��上报
	private void report() {
//		trafficReport.requestTrafficReport("0", type, reportContent,
//				fileString, fileType, lon, lat);
		String id = CYX_Drive_SDK.getInstance().getUserInfo().getUserID();
		double[] baidus = Coordinate.baidutowg(lon, lat);
		CYX_Drive_SDK.getInstance().getConnection().
		sendExtData(new ExtraDataProcess().getTrafficReportData("0", type, reportContent,
				fileString, fileType, baidus[0], baidus[1]), new RequestCallback(){
			@Override
			public void onSuccess(String bizJsonData) {
				if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_TrafficReport.this.getClass().getName())) {
				mProgressDialog.cancel();
				JSONObject jsonObj;
				try {
					jsonObj = new JSONObject(bizJsonData);
					int result = jsonObj.getInt("result");
					if (result == ConstantContext.SUCCESS) {
						toastText = getResources().getString(
								MResource.getStringId(getApplicationContext(),
										"traffic_report_succeed"));
					}

					else if (result == ConstantContext.ERROR_1) {	//userId为空
						toastText = getResources().getString(
								MResource.getStringId(getApplicationContext(),
										"user_id_null"));
					} else if (result == ConstantContext.ERROR_2) {	//未选择类型
						toastText = getResources().getString(
								MResource.getStringId(getApplicationContext(),
										"traffic_unselect_type"));
					}

					else if (result == ConstantContext.ERROR_3) {	//经纬度不完整
						toastText = getResources().getString(
								MResource.getStringId(getApplicationContext(),
										"traffic_incomplete_location"));
					}

					else if (result == ConstantContext.ERROR_4) {	//数据库操作异常
						toastText = getResources().getString(
								MResource.getStringId(getApplicationContext(),
										"database_exception"));
					} else if (result == ConstantContext.ERROR_5) {	//重复上报
						toastText = getResources().getString(
								MResource.getStringId(getApplicationContext(),
										"traffic_repeat"));
					}
					showToast();
					cyx_TrafficReport.this.finish();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
			@Override
			public void onFailed(int reason) {
				if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_TrafficReport.this.getClass().getName())) {
				switch(reason) {
				case RequestCallback.REASON_NO_NETWORK:
					toastText += getResources().getString(
							MResource.getStringId(getApplicationContext(), "network_switch_off"));
					break;
				case RequestCallback.REASON_NO_SIGNAL:
					toastText += getResources().getString(
							MResource.getStringId(getApplicationContext(), "no_network_signal"));
					break;
				case RequestCallback.REASON_NOT_AUTHENTICATED:
					toastText += getResources().getString(
							MResource.getStringId(getApplicationContext(), "no_auth"));
					break;
				case RequestCallback.REASON_TIMEOUT:
					toastText += getResources().getString(
							MResource.getStringId(getApplicationContext(), "request_timeOut"));
					break;
				case RequestCallback.REASON_DATA_INCRECT:
					toastText += getResources().getString(
							MResource.getStringId(getApplicationContext(), "data_increct"));
					break;
			}
			showToast();
				}
		}
		});
		mProgressDialog.show();
	}

	private void init() {
		userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
		UserID = userInfo.getUserID();
		handler = new Handler();
		mProgressDialog = new ProgressDialog(cyx_TrafficReport.this);
		mProgressDialog.setTitle(getResources().getString(
				MResource.getStringId(getApplicationContext(),
						"traffic_reporting")));
		mProgressDialog.setMessage(getResources().getString(
				MResource.getStringId(getApplicationContext(), "wait")));
		mProgressDialog.setCanceledOnTouchOutside(false);

		jamBtn = (ImageView) findViewById(MResource.getID(
				getApplicationContext(), "jam_btn"));
		accidentBtn = (ImageView) findViewById(MResource.getID(
				getApplicationContext(), "accident_btn"));
		workBtn = (ImageView) findViewById(MResource.getID(
				getApplicationContext(), "work_btn"));
		policeBtn = (ImageView) findViewById(MResource.getID(
				getApplicationContext(), "police_btn"));
		report_Btn = (Button) findViewById(MResource.getID(
				getApplicationContext(), "report_btn"));

		jamBtn.setOnClickListener(this);
		accidentBtn.setOnClickListener(this);
		workBtn.setOnClickListener(this);
		policeBtn.setOnClickListener(this);
		report_Btn.setOnClickListener(this);

		lon = getIntent().getDoubleExtra("lon", 0.0);
		lat = getIntent().getDoubleExtra("lat", 0.0);
		networkDetector = new NetworkDetector();
		
		// Log.e(TAG,"longitude"+lon);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		cyx_MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}

	private void showToast() {
		if (!TextUtils.isEmpty(toastText)) {

			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mProgressDialog!=null&&mProgressDialog.isShowing())
						mProgressDialog.dismiss();
					Toast.makeText(cyx_TrafficReport.this, toastText,
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	public class NetworkDetector {

		public boolean detect(Activity act) {

			ConnectivityManager manager = (ConnectivityManager) act
					.getApplicationContext().getSystemService(
							Context.CONNECTIVITY_SERVICE);

			if (manager == null) {
				return false;
			}

			NetworkInfo networkinfo = manager.getActiveNetworkInfo();

			if (networkinfo == null || !networkinfo.isAvailable()) {
				return false;
			}

			return true;
		}
	}
}