package com.cwits.cyx_drive_sdk.ui;

import java.util.Stack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.findPassword.cyx_FindPasswordActivity;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.userInfo.IExternalInterfaceAR;
import com.cwits.cyx_drive_sdk.userInfo.IUserInfo;
import com.cwits.cyx_drive_sdk.data.ConstantContext;

public class cyx_LoginActivity extends Activity {

	

	private Button btn_login;
	private EditText edt_account, edt_password;
	private TextView title, find_password, tv_to_register;
	private ImageView img_back;
	private ProgressDialog mProgressDialog;
	Handler mHandler;
	private String id,token;
	private int flag, area;
	public static final int LOGIN_SUCCESS = 1;
	public static final int ALREADY_LOGIN = 2;
	private String toastText = "";
	private IExternalInterfaceAR ExternalInterfaceAR;
	private String account_number, password_number;
	private IUserInfo userInfo;
	private boolean autoLogin = false;
	private IExternalInterfaceAR mIExternalInterfaceAR;
	private Stack<Activity> mActivityStack;
	private String temp_account, temp_passwork;
	private int isAutoLogin = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_login"));
		cyx_MyApplication.getInstance().addActivity(this);
		mIExternalInterfaceAR = CYX_Drive_SDK.getInstance().getExternalInterface();
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == LOGIN_SUCCESS) {
					if (ExternalInterfaceAR != null)
						ExternalInterfaceAR.OnUserLogin(0,1, "");
					Toast.makeText(cyx_LoginActivity.this, getResources().getString(
									MResource.getStringId(getApplicationContext(),"login_success")), Toast.LENGTH_SHORT).show();
					cyx_LoginActivity.this.finish();
				} else if (msg.what == ALREADY_LOGIN) {
					Toast.makeText(cyx_LoginActivity.this,getResources().getString(
									MResource.getStringId(getApplicationContext(), "userAlreadylogin")), Toast.LENGTH_SHORT).show();
				}
			}
		};

		init();
	}

	private void init() {
		CYX_Drive_SDK.getInstance().getConnection().start();
		if(getIntent().getExtras() != null)
			autoLogin = getIntent().getExtras().getBoolean("autoLogin", false);
		ExternalInterfaceAR = CYX_Drive_SDK.getInstance().getExternalInterface();
		IUserInfo IuserInfo = CYX_Drive_SDK.getInstance().getUserInfo();
		if (!IuserInfo.getName().equals("temp")) {
			account_number = IuserInfo.getName();
			password_number = IuserInfo.getPassWord();
		} else if(IuserInfo.getName().equals("temp")) {
			temp_account = IuserInfo.getUserID();
			temp_passwork = IuserInfo.getPassWord();
			isAutoLogin = IuserInfo.getAutoLoginFlag();
		}
		btn_login = (Button) findViewById(MResource.getID(getApplicationContext(), "btn_login"));
		btn_login.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_nonpoint"));
		edt_account = (EditText) findViewById(MResource.getID(getApplicationContext(), "input_account"));
		edt_account.setText(account_number);
		edt_account.addTextChangedListener(new MaxLengthWatcher());
		edt_password = (EditText) findViewById(MResource.getID(getApplicationContext(), "input_password"));
		edt_password.addTextChangedListener(new MaxLengthWatcher());
		edt_password.setText(password_number);
		title = (TextView) findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		img_back = (ImageView) findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		title.setText(MResource.getStringId(getApplicationContext(), "cyx_login"));
		find_password = (TextView) findViewById(MResource.getID(getApplicationContext(), "find_password"));
		find_password.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(cyx_LoginActivity.this,
						cyx_FindPasswordActivity.class);
				startActivity(intent);
			}
		});
		mProgressDialog = new ProgressDialog(cyx_LoginActivity.this);
		mProgressDialog.setTitle(getResources().getString(
				MResource.getStringId(getApplicationContext(), "notice")));
		mProgressDialog.setMessage(getResources().getString(
				MResource.getStringId(getApplicationContext(), "wait")));
		mProgressDialog.setCanceledOnTouchOutside(false);
		// 登陆
		btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edt_account.getText().toString().trim().equals("")) {
					return;
				}
				account_number = edt_account.getText().toString();
				password_number = edt_password.getText().toString();

				String hintStr = "";
				if (TextUtils.isEmpty(account_number)) {
					hintStr += getResources().getString(
							MResource.getStringId(getApplicationContext(), "phoneNum_isNUll"));
				}
				if (TextUtils.isEmpty(password_number)) {
					if (!TextUtils.isEmpty(hintStr))
						hintStr += "\n" + getResources().getString(
										MResource.getStringId(getApplicationContext(), "password_isNull"));
				}
				if (!TextUtils.isEmpty(hintStr)) {
					Toast.makeText(cyx_LoginActivity.this, hintStr, Toast.LENGTH_SHORT).show();
					return;
				}
				mProgressDialog.show();
//				Connection.getInstance().stop();
				Connection.getInstance().setStartType(Connection.START_TYPE_CONNECT_ONLY);
//				Connection.getInstance().start();
				Connection.getInstance().sendExtData(
						new ExtraDataProcess().getLoginData("2", id,
								account_number, password_number),
						new RequestCallback() {

							@Override
							public void onSuccess(String bizJsonData) {
//								cyx_LoginActivity.this.
								// TODO Auto-generated method stub
								try {
									JSONObject jsonObj = new JSONObject(bizJsonData);
									switch (jsonObj.getInt("result")) {
									case ConstantContext.SUCCESS:
										Log.d("--------------- cyx_drive_sdk", "login server success");
										try {
											 id = jsonObj.getString("id");
											 token = jsonObj.getString("token");
											 area = Integer.parseInt(jsonObj.getString("area"));
											 flag = jsonObj.getInt("flag");
											Connection.getInstance().setStartType(Connection.START_TYPE_DEFAULT);
											Connection.getInstance().login(id, password_number,
													new RequestCallback() {
														public void onSuccess(String bizJsonData) {
															if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_LoginActivity.this.getClass().getName())) {
															Log.d("------------ cyx_driving_sdk","login openfire success");
															CYX_Drive_SDK.getInstance().getUserManager()
															.addUser(account_number, 1, password_number, flag);
															CYX_Drive_SDK.getInstance().getUserManager()
															.setDefaultUser(account_number);
															userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
															userInfo.setName(account_number);
															userInfo.setPassword(password_number);
															userInfo.setArea(area + "");
															userInfo.setToken(token);
															userInfo.setUserID(id);
															userInfo.setFlag(flag);
															userInfo.setAutoLoginFlag(1);
															mProgressDialog.dismiss();
															mHandler.sendMessage(mHandler.obtainMessage(LOGIN_SUCCESS));
															}
														}

														public void onFailed(
																int reason) {
															if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_LoginActivity.this.getClass().getName())) {
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
															case RequestCallback.REASON_ERROR:
																switch(Connection.getInstance().getLoginErrReason()) {
																	case Connection.REASON_AUTH_USER_NON_EXIST:
																		toastText += getResources().getString(
																				MResource.getStringId(getApplicationContext(), "no_user"));
																		break;
																	case Connection.REASON_AUTH_USER_PASS_ERR:
																		toastText += getResources().getString(
																				MResource.getStringId(getApplicationContext(), "user_passwd_err"));
																		break;
																	case Connection.REASON_AUTH_UNKONW:
																		toastText += getResources().getString(
																				MResource.getStringId(getApplicationContext(), "login_fail"));
																		break;
																	case Connection.REASON_AUTH_TIMEOUT:
																		toastText += getResources().getString(
																				MResource.getStringId(getApplicationContext(), "request_timeOut"));
																		break;
																}
																break;
															}
															showToast();
														}
														}
													});

										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										break;
									case ConstantContext.ERROR_1:
										mProgressDialog.dismiss();
										if (ExternalInterfaceAR != null)
											ExternalInterfaceAR.OnUserLogin(1, 1,"");
										toastText += getResources().getString(
														MResource.getStringId(getApplicationContext(), "result_error1"));
										break;
									case ConstantContext.ERROR_2:
										mProgressDialog.dismiss();
										if (ExternalInterfaceAR != null)
											ExternalInterfaceAR.OnUserLogin(2,1,"");
										toastText += getResources().getString(MResource
																.getStringId(getApplicationContext(), "result_error2"));
										break;
									case ConstantContext.ERROR_3:
										mProgressDialog.dismiss();
										if (ExternalInterfaceAR != null)
											ExternalInterfaceAR.OnUserLogin(3,1, "");
										toastText += getResources().getString(
														MResource.getStringId(getApplicationContext(), "result_error3"));
										break;
									case ConstantContext.ERROR_4:
										mProgressDialog.dismiss();
										if (ExternalInterfaceAR != null)
											ExternalInterfaceAR.OnUserLogin(4, 1,"");
										toastText += getResources().getString(
														MResource.getStringId(getApplicationContext(), "result_error4"));
										break;
									case ConstantContext.ERROR_5:
										mProgressDialog.dismiss();
										if (ExternalInterfaceAR != null)
											ExternalInterfaceAR.OnUserLogin(5,1, "");
										toastText += getResources().getString(
														MResource.getStringId(getApplicationContext(), "result_error5"));
										break;
									case ConstantContext.ERROR_6:
										mProgressDialog.dismiss();
										if (ExternalInterfaceAR != null)
											ExternalInterfaceAR.OnUserLogin(6,1, "");
										toastText += getResources().getString(
														MResource.getStringId(getApplicationContext(), "result_error6"));
										break;
									case  ConstantContext.ERROR_7: 
										mProgressDialog.dismiss();
										if (ExternalInterfaceAR != null)
											ExternalInterfaceAR.OnUserLogin(7,1, "");
										toastText += getResources().getString(
														MResource.getStringId(getApplicationContext(), "result_error7"));
										break;
									}
									showToast();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							@Override
							public void onFailed(int reason) {
								// TODO Auto-generated method stub
								if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_LoginActivity.this.getClass().getName())) {
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
			}
		});

		tv_to_register = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "tv_to_register"));
		tv_to_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(cyx_LoginActivity.this, cyx_RegisterActivity.class);
				startActivity(intent);
				//cyx_LoginActivity.this.finish();
			}
		});
		img_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mIExternalInterfaceAR!=null)
					mIExternalInterfaceAR.clickOnBack(1);
				cyx_LoginActivity.this.finish();
			}
		});
		if(autoLogin) {
			img_back.setVisibility(View.GONE);
			btn_login.performClick();
		}
		//除了临时账号下的登录，其他登录界面去除返回键
		if(CYX_Drive_SDK.getInstance().getUserInfo().getLoginMode()!=0){
			img_back.setVisibility(View.GONE);
		}
}

	public class MaxLengthWatcher implements TextWatcher {

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
			Editable editable = edt_account.getText();
			Editable paseditable = edt_password.getText();
			int len = editable.length();
			int len1 = paseditable.length();
			if (len > 10 && len1 == 6) {
				btn_login.setClickable(true);
				btn_login.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "register_btn_bg"));
			} else {
				btn_login.setClickable(false);
				btn_login.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "btn_nonpoint"));
			}
			if (len == 0) {
				if (len1 > 0)
					edt_password.setText("");
			}
		}

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
			if(CYX_Drive_SDK.getInstance().getUserInfo().getLoginMode()!=0){
	        return true;
			}
	    }
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		CYX_Drive_SDK.getInstance().getConnection().start();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		cyx_MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}

	private void showToast() {
		if (!TextUtils.isEmpty(toastText)) {

			mHandler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					reConnAndLogin();
					if(mProgressDialog != null && mProgressDialog.isShowing())
						mProgressDialog.dismiss();
					Toast.makeText(cyx_LoginActivity.this, toastText, Toast.LENGTH_SHORT).show();
					toastText = "";
					//自动登录失败，如果上一个界面是开车驾驶界面，则将其finish
//					if(autoLogin)
						ifFinishDrivingActivity();
				}
			});
		}
	}
	
	/**
	 * 判断上一个activity是否是mainactivity，是的话则调用finish
	 */
	private void ifFinishDrivingActivity() {
		mActivityStack = cyx_MyApplication.getInstance().getActivityStack();
		if(mActivityStack.size() > 2) {
			if(mActivityStack.get(mActivityStack.size() - 2).getClass().getName().equals("com.cwits.cyx_drive_sdk.ui.cyx_MainActivity")) {
				mActivityStack.get(mActivityStack.size() - 2).finish();
			}
		}
			
	}
	
	/**
	 * 登录失败后调用，如果默认账号是临时账号并且自动登录，则自动重新连接并登录
	 */
	private void reConnAndLogin() {
		if(isAutoLogin == 1 && !TextUtils.isEmpty(temp_account) && !TextUtils.isEmpty(temp_passwork)) {
			Log.d("------ cyx_drive_sdk", "临时账号重新登录xmpp");
			Connection.getInstance().stop();
			Connection.getInstance().setStartType(Connection.START_TYPE_DEFAULT);
			Connection.getInstance().setUsernamePasswd(temp_account, temp_passwork);
			Connection.getInstance().start();
		}
	}
}
