package com.cwits.cyx_drive_sdk.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ConstantContext;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.findPassword.cyx_FindPasswordCheckCodeActivity;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class cyx_UnBindingActivity extends Activity {
	private TextView titleView, messageView, btnOk, btnCancel, cyx_phonenumber;
	private View line;
	private UserInfo userInfo;
	private String phone_pre, phone_beh;
	private ProgressDialog mProgressDialog; 
	private String hintContent = "";
	private Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_dialog"));
		cyx_MyApplication.getInstance().addActivity(this);
		titleView = (TextView)findViewById(MResource.getID(getApplicationContext(), "cyx_dialog_title"));
		messageView = (TextView)findViewById(MResource.getID(getApplicationContext(),"cyx_dialog_content"));
		cyx_phonenumber = (TextView)findViewById(MResource.getID(getApplicationContext(),"cyx_phonenumber"));
        btnCancel = (TextView)findViewById(MResource.getID(getApplicationContext(), "cyx_dialog_ok"));
        btnOk = (TextView)findViewById(MResource.getID(getApplicationContext(), "cyx_dialog_cancel"));
        btnOk.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        line = findViewById(MResource.getID(getApplicationContext(), "cyx_dialog_line"));
        
        init();
	}
	
	
	private void init() {
		userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
		if(userInfo!=null && !TextUtils.isEmpty(userInfo.getName())) {
			mHandler = new Handler();
			mProgressDialog = new ProgressDialog(cyx_UnBindingActivity.this);
			mProgressDialog.setTitle(getResources().getString(MResource.getStringId(getApplicationContext(), "notice")));
			mProgressDialog.setMessage(getResources().getString(MResource.getStringId(getApplicationContext(), "wait")));
			mProgressDialog.setCanceledOnTouchOutside(false);
	    	phone_pre = userInfo.getName().substring(0, 3);
	    	phone_beh = userInfo.getName().substring(7,11);
	    	titleView.setText(MResource.getStringId(getApplicationContext(), "unbinding"));
	    	messageView.setText(getResources().getString(MResource.getStringId(getApplicationContext(), "unbinding_hint")));
	    	cyx_phonenumber.setText(phone_pre+"****" + phone_beh);
	    	line.setVisibility(View.VISIBLE);
	    	btnCancel.setText(getResources().getString
					(MResource.getStringId(getApplicationContext(), "cancel")));
	    	btnCancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					cyx_UnBindingActivity.this.finish();
				}
			});
	    	btnOk.setText(getResources().getString
					(MResource.getStringId(getApplicationContext(), "ensure")));
	    	btnOk.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mProgressDialog.show();
					sendSMS();
				}
			});
		} else {
			Toast.makeText(cyx_UnBindingActivity.this, MResource.getStringId(getApplicationContext(), "get_user_info_error"), Toast.LENGTH_SHORT).show();
			cyx_UnBindingActivity.this.finish();
		}
	}
	
	private void sendSMS() {
		CYX_Drive_SDK.getInstance().getConnection().sendExtData(new ExtraDataProcess().getPhoneVerifyData(2, userInfo.getUserID(), 
				userInfo.getName()), new RequestCallback(){
			@Override
			public void onSuccess(String bizJsonData) {
				if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_UnBindingActivity.this.getClass().getName())) {
				if(mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				JSONObject jsonObj;
				try {
					jsonObj = new JSONObject(bizJsonData);
					switch(jsonObj.getInt("result")) {
					case ConstantContext.SUCCESS:
						mProgressDialog.dismiss();
						Toast.makeText(cyx_UnBindingActivity.this,
									getResources().getString(MResource.getStringId(getApplicationContext(),"sent_checkCode" )),Toast.LENGTH_SHORT).show(); 
						Intent intent = new Intent(cyx_UnBindingActivity.this, cyx_UnbindingCheckCodeActivity.class);
						intent.putExtra("phoneNum", userInfo.getName());	
						startActivity(intent);
						cyx_UnBindingActivity.this.finish();
				    break;
					 case ConstantContext.ERROR_1://id为空
						 hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "user_id_null"));
					    break;
				    case ConstantContext.ERROR_2://手机号为空
				    	hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "null_phonenumber"));
				    break;
				    case ConstantContext.ERROR_3://两次获取验证码时间小于5秒
				    	hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "apply_too_often"));
				    break;
				    case ConstantContext.ERROR_4://短信发送失败
				    	hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "send_sms_error"));
				    break;
				    case ConstantContext.ERROR_5:	//程序错误
						hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "procedure_error"));
						break;
					case ConstantContext.ERROR_6:	//手机号格式不正确
						hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "format_error"));
						break;
					 case ConstantContext.ERROR_7:	//用户未注册
						 hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "find_password_message"));
						 break;
					 case ConstantContext.ERROR_8:	//用户已注册
						 hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "user_already_resister"));
							break;
					 case ConstantContext.ERROR_9:	//类型错误
						 hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "type_error"));
							break;
					}
					showHintContent();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				
			}
			@Override
			public void onFailed(int reason) {
				if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_UnBindingActivity.this.getClass().getName())) {
				switch (reason) {
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
			}
		});
	}

	private void showHintContent() {
		if(!TextUtils.isEmpty(hintContent)) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					if(mProgressDialog!=null&&mProgressDialog.isShowing())
						mProgressDialog.dismiss();
					Toast.makeText(cyx_UnBindingActivity.this,hintContent,Toast.LENGTH_SHORT).show(); 
					hintContent = "";
					cyx_UnBindingActivity.this.finish();
				}
			});
		}
}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		cyx_MyApplication.getInstance().removeActivity(this);
	}
	
	

}
