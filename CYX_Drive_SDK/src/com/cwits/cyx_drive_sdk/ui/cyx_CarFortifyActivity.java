package com.cwits.cyx_drive_sdk.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ConstantContext;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;

public class cyx_CarFortifyActivity extends Activity {
    private TextView carFortify_notice;
    private TextView close_fortify_tv;
    private Button btn_fortify;
    private TextView title;
    private ImageView btn_back;
    int fortifyState = -1;
    UserInfo userInfo;
    Connection conn;
    ProgressDialog mProgressDialog; 
    Handler handler;
    String hintContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_vip_car_fortify"));
		cyx_MyApplication.getInstance().addActivity(this);
		init();
	}
	private void init(){
		handler = new Handler();
		userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
		conn = CYX_Drive_SDK.getInstance().getConnection();
		title = (TextView)findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		title.setText(MResource.getStringId(getApplicationContext(), "car_fortify")); 
		btn_back = (ImageView)findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_CarFortifyActivity.this.finish();
			}
		});
		btn_fortify = (Button)findViewById(MResource.getID(getApplicationContext(), "btn_fortify"));
		carFortify_notice = (TextView)findViewById(MResource.getID(getApplicationContext(), "car_fortify_notice"));
		close_fortify_tv = (TextView)findViewById(MResource.getID(getApplicationContext(), "colse_fortify_tv"));
		initPorogressDialog();
		btn_fortify.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				getFortifyState();
				if(userInfo.getFortify()==1){
					closeCarFortify();
				}else {
					openCarFortify();
				}
			}
		});
		if(userInfo.getFortify()==1){
			fortify();
		}else{
			unfortify();
		}
	}
	private void initPorogressDialog(){
		mProgressDialog = new ProgressDialog(cyx_CarFortifyActivity.this);
		mProgressDialog.setTitle(getResources().getString(MResource.getStringId(getApplicationContext(), "notice")));
		mProgressDialog.setMessage(getResources().getString(MResource.getStringId(getApplicationContext(), "wait")));
		mProgressDialog.setCanceledOnTouchOutside(false);
	}
	
	//开启设防功能
	private String openCarFortify(){
	  return conn.sendExtData(new ExtraDataProcess().getCarFortifyData(userInfo.getUserID(), "1"), new RequestCallback(){
			@Override
			public void onSuccess(String bizJsonData) {
				try {
					JSONObject jsonObj = new JSONObject(bizJsonData);
					int resultCode = jsonObj.getInt("result");
					Log.i("lxh", "openCarFortify result:"+resultCode);
					switch (resultCode) {
					case ConstantContext.SUCCESS:
						hintContent = "设防成功！";
						fortify();
						userInfo.setFortifyState(1);
						break;
					case ConstantContext.ERROR_1:
						hintContent = "用户Id为空";
						break;
					case ConstantContext.ERROR_2:
						hintContent = "设防状态为空!";	
						break;
					case ConstantContext.ERROR_3:
						hintContent = "未绑定任何设备!";
						break;
					case ConstantContext.ERROR_4:
						hintContent = "数据库错误";
						break;
					}
					showHintContent();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onFailed(int reason) {
				switch(reason) {
				case RequestCallback.REASON_NO_NETWORK:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(), "network_switch_off"));
					break;
				case RequestCallback.REASON_NO_SIGNAL:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(), "no_network_signal"));
					break;
				case RequestCallback.REASON_NOT_AUTHENTICATED:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(), "no_auth"));
					break;
				case RequestCallback.REASON_TIMEOUT:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(), "request_timeOut"));
					break;
				case RequestCallback.REASON_DATA_INCRECT:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(), "data_increct"));
					break;
			}
			showHintContent();
			}
		});
	}
	//关闭设防功能
	private String closeCarFortify(){
		return conn.sendExtData(new ExtraDataProcess().getCarFortifyData(userInfo.getUserID(), "1"), new RequestCallback(){
			
			@Override
			public void onSuccess(String bizJsonData) {
				try {
					JSONObject jsonObj = new JSONObject(bizJsonData);
					int resultCode = jsonObj.getInt("result");
					Log.i("lxh", "closeCarFortify result:"+resultCode);
					switch (resultCode) {
					case ConstantContext.SUCCESS:
						hintContent = "取消设防成功！";
						unfortify();
						userInfo.setFlag(0);
						break;
					case ConstantContext.ERROR_1:
						hintContent = "用户Id为空";
						break;
					case ConstantContext.ERROR_2:
						hintContent = "设防状态为空!";	
						break;
					case ConstantContext.ERROR_3:
						hintContent = "未绑定任何设备!";
						break;
					case ConstantContext.ERROR_4:
						hintContent = "数据库错误";
						break;
					}
					showHintContent();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onFailed(int reason) {
				switch(reason) {
				case RequestCallback.REASON_NO_NETWORK:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(), "network_switch_off"));
					break;
				case RequestCallback.REASON_NO_SIGNAL:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(), "no_network_signal"));
					break;
				case RequestCallback.REASON_NOT_AUTHENTICATED:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(), "no_auth"));
					break;
				case RequestCallback.REASON_TIMEOUT:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(), "request_timeOut"));
					break;
				case RequestCallback.REASON_DATA_INCRECT:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(), "data_increct"));
					break;
			}
				showHintContent();
		  }
		});
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		cyx_CarFortifyActivity.this.finish();
		super.onBackPressed();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		cyx_MyApplication.getInstance().removeActivity(this);
		if(mProgressDialog!=null&&mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
			}
		mProgressDialog = null;
		super.onDestroy();
	}
	private void showHintContent() {
		if(!TextUtils.isEmpty(hintContent)) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mProgressDialog!=null&&mProgressDialog.isShowing())
						mProgressDialog.dismiss();
					Toast.makeText(cyx_CarFortifyActivity.this,hintContent,Toast.LENGTH_SHORT).show(); 
					hintContent = "";
				}
			});
		}
	}
	
	private String  getFortifyState(){
		if(mProgressDialog!=null)
			mProgressDialog.show();
		return conn.sendExtData(new ExtraDataProcess().getMyCarData(userInfo.getUserID()), new RequestCallback(){
			@Override
			public void onSuccess(String bizJsonData) {
				if(mProgressDialog!=null&&mProgressDialog.isShowing())
					mProgressDialog.dismiss();
				try {
					JSONObject jsonObj = new JSONObject(bizJsonData);
					int resultCode = jsonObj.getInt("result");
					switch (resultCode) {
					case 0:
						break;
					case 1:
						fortifyState = -1;
						//用户id为空
						Toast.makeText(cyx_CarFortifyActivity.this, "用户id为空！", Toast.LENGTH_SHORT).show();
						unfortify();
						break;
					case 2:
						fortifyState = -1;
						Toast.makeText(cyx_CarFortifyActivity.this, "您未绑定任何设备！", Toast.LENGTH_SHORT).show();
						//未绑定任何设备
						unfortify();
						break;
					case 3:
						fortifyState = -1;
						Toast.makeText(cyx_CarFortifyActivity.this, "设备状态未获取！", Toast.LENGTH_SHORT).show();
						//未知错误
						unfortify();
						break;
					
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onFailed(int reason) {
				if(mProgressDialog!=null&&mProgressDialog.isShowing())
					mProgressDialog.dismiss();
				// 查询设备状态失败
				fortifyState = -1;
				unfortify();
				Toast.makeText(cyx_CarFortifyActivity.this, "设备状态获取失败！", Toast.LENGTH_SHORT).show();
				Log.i("lxh", "------getFortifyState Failed reason: " +reason);
			}
		});
	}
	public void unfortify(){
		close_fortify_tv .setVisibility(View.VISIBLE);
		close_fortify_tv.getBackground().setAlpha(190);
		carFortify_notice.setVisibility(View.INVISIBLE);
		btn_fortify.setText(getString(MResource.getStringId(getApplicationContext(), "open_fortify")));
	}
	public void fortify(){
		close_fortify_tv .setVisibility(View.GONE);
		carFortify_notice.setVisibility(View.VISIBLE);
		btn_fortify.setText(getString(MResource.getStringId(getApplicationContext(), "close_fortify")));
	}
	
}
