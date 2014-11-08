package com.cwits.cyx_drive_sdk.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ConstantContext;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;

public class cyx_RegisterInputPwdActivity extends Activity {

    private	Button btn_finish;
    private EditText edt_Pwd, edt_rePwd;
    private TextView title;
    private ImageView img_back;
    private TextView input_pwd_notice;
    String pwd, rePwd;
    ProgressDialog mProgressDialog;
	Handler mHandler;
	cyx_CustomAlertDialog mDialog;
	private String hintContent = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_register_inputpwd"));
		cyx_MyApplication.getInstance().addActivity(this);
		init();
	}

	private void init(){
		mHandler = new Handler();
		btn_finish = (Button)findViewById(MResource.getID(getApplicationContext(), "btn_finish"));
		edt_Pwd = (EditText)findViewById(MResource.getID(getApplicationContext(), "edt_pwd"));
		edt_rePwd = (EditText)findViewById(MResource.getID(getApplicationContext(), "edt_rePwd"));
		input_pwd_notice = (TextView)findViewById(MResource.getID(getApplicationContext(), "input_pwd_notice"));
		edt_Pwd.setOnFocusChangeListener(new MyEdtFocusChangeListener());
		edt_rePwd.setOnFocusChangeListener(new MyEdtFocusChangeListener());
		edt_Pwd.addTextChangedListener(new PasswordWatcher());
		edt_rePwd.addTextChangedListener(new PasswordWatcher());
		mProgressDialog = new ProgressDialog(cyx_RegisterInputPwdActivity.this);
		mProgressDialog.setTitle(getResources().getString(MResource.getStringId(getApplicationContext(), "notice")));
		mProgressDialog.setMessage(getResources().getString(MResource.getStringId(getApplicationContext(), "wait")));
	    mProgressDialog.setCanceledOnTouchOutside(false);
		btn_finish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pwd	= edt_Pwd.getText().toString();
				rePwd  = edt_rePwd.getText().toString();
				if(pwd.contains(" ")||rePwd.contains(" ")){
					Toast.makeText(cyx_RegisterInputPwdActivity.this, 
							getResources().getString(MResource.getStringId(getApplicationContext(), 
									"input_pwd_error1")), Toast.LENGTH_SHORT).show();
				}else if(!pwd.equals(rePwd)){
					Toast.makeText(cyx_RegisterInputPwdActivity.this, 
							getResources().getString(MResource.getStringId(getApplicationContext(),
									"input_pwd_inconformity")), Toast.LENGTH_SHORT).show();
				}else if(pwd.length()<6||rePwd.length()<6){
					Toast.makeText(cyx_RegisterInputPwdActivity.this, 
							getResources().getString(MResource.getStringId(getApplicationContext(), 
									"password_less_6")), Toast.LENGTH_SHORT).show();
				}
				else if(pwd.equals(rePwd)&&pwd.length()==6&&rePwd.length()==6){
					String UserID=CYX_Drive_SDK.getInstance().getUserInfo().getTempID();
					Connection.getInstance().sendExtData(new ExtraDataProcess().getCreatPassword(UserID, pwd), 
							new RequestCallback(){

								@Override
								public void onSuccess(String bizJsonData) {
									// TODO Auto-generated method stub
									if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_RegisterInputPwdActivity.this.getClass().getName())) {
									try {
										JSONObject jsonObj = new JSONObject(bizJsonData);
										switch (jsonObj.getInt("result")) {
										case ConstantContext.SUCCESS:
											hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"setting_success" ));
											Intent intent = new Intent(cyx_RegisterInputPwdActivity.this, cyx_LoginActivity.class);
											startActivity(intent);
											cyx_RegisterInputPwdActivity.this.finish();
											break;
										//用户ID为空
										case ConstantContext.ERROR_1:
											hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"user_id_null" ));
											break;
										//用户密码为空
										case ConstantContext.ERROR_2:
											hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"password_isNull" ));
											break;
										//用户不存�?
										case ConstantContext.ERROR_3:
											hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"no_user" ));
											break;
										//未绑定帐号信�?
										case ConstantContext.ERROR_4:
											hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"no_bind_phone" ));
											break;
										//已经绑定密码
										case ConstantContext.ERROR_5:
											hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"already_bind_phone" ));
											break;
											//程序错误
										case ConstantContext.ERROR_6:
											hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"procedure_error" ));
											break;
											//密码格式不正确
										case ConstantContext.ERROR_7:
											hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"passwd_format_error" ));
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
									// TODO Auto-generated method stub
									if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_RegisterInputPwdActivity.this.getClass().getName())) {
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
								}
						
					});
					mProgressDialog.show();
				}
			}
		});
		title = (TextView)findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		title.setText(MResource.getStringId(getApplicationContext(), "input_pwd"));
		img_back = (ImageView)findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		img_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_RegisterInputPwdActivity.this.finish();
			}
		});
	}
	@Override
	protected void onDestroy() {
		if(mDialog!=null&&mDialog.isShowing())
			mDialog.dismiss();
		cyx_MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
//	//自定义对话框
//    private void createDialog(){
//    	if(mDialog!=null&&mDialog.isShowing()){
//    		mDialog.dismiss();
//    		mDialog=null;
//    	}
//    	mDialog = new cyx_CustomAlertDialog(cyx_RegisterInputPwdActivity.this);
//		mDialog.setTitle(getString(MResource.getStringId(getApplicationContext(), "notice")));
//		mDialog.setMessage(getString(MResource.getStringId(getApplicationContext(), "notice_registSuccess")));
//		mDialog.setPositiveButton( new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						mDialog.dismiss();
//						Intent intent = new Intent(cyx_RegisterInputPwdActivity.this, cyx_SettingsActivity.class);
//						startActivity(intent);
//						cyx_RegisterInputPwdActivity.this.finish();
//					}
//				});
//    }
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	
	public class PasswordWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if(edt_Pwd.length()==6&&edt_rePwd.length()==6){
				btn_finish.setClickable(true);
				btn_finish.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "register_btn_bg"));
			}else{
				
					btn_finish.setClickable(false);
					btn_finish.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_nonpoint"));
				
			}
		}
		
	}

	class MyEdtFocusChangeListener implements OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(hasFocus){
				input_pwd_notice.setVisibility(View.VISIBLE);
			}else{
				input_pwd_notice.setVisibility(View.GONE);
			}
		}
		
	}
	
	private void showHintContent() {
		if(!TextUtils.isEmpty(hintContent)) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					if(mProgressDialog!=null&&mProgressDialog.isShowing())
						mProgressDialog.dismiss();
					Toast.makeText(cyx_RegisterInputPwdActivity.this,hintContent,Toast.LENGTH_SHORT).show(); 
				}
			});
		}
	}
}
