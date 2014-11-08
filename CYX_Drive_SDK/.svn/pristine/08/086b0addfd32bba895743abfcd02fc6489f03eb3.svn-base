package com.cwits.cyx_drive_sdk.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.connection.cyx_XMPPService;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.userInfo.IExternalInterfaceAR;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;

public class cyx_SettingsActivity extends Activity implements OnClickListener {
	private static final String TAG = "cyx_SettingsActivity";
	private TextView title, change_tv, binding_tv;
	private Button logout_btn, login_btn, exit_pop, logout_pop, cancel_pop;
	private View good_layout, inviter_layout, comments_layout, about_layout, mView, binding_layout;
	private WindowManager windowManager;
	private boolean isShowingInviterView = false;	//表示是否正在显示邀请好友界面
	private WindowManager.LayoutParams params;//邀请界面的参数
	private String version_number;	  			 //版本号
	private String hintContent = "";
	private Handler handler = new Handler();
	private ProgressDialog mProgressDialog; 
	private ImageView img_back;
	private TextView phoneNo_tv;
	private View phoneNo_layout, view;
	private PopupWindow  pop = null;
	private IExternalInterfaceAR ExternalInterfaceAR;
	private boolean isExitApp = false;
	Context context;
	private UserInfo userInfo;
	private boolean isVIP = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context=this;
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_settings_layout"));
		cyx_MyApplication.getInstance().addActivity(this);
		
		userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
		ExternalInterfaceAR=CYX_Drive_SDK.getInstance().getExternalInterface();
		
		view = findViewById(MResource.getID(getApplicationContext(), "settings"));
		
		title = (TextView) findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		title.setText(getResources().getString(MResource.getStringId(getApplicationContext(), "setting")));
		
		phoneNo_tv = (TextView) findViewById(MResource.getID(getApplicationContext(), "phone_no"));
		
		phoneNo_layout = findViewById(MResource.getID(getApplicationContext(), "phoneNo_layout"));
		
		logout_btn = (Button) findViewById(MResource.getID(getApplicationContext(), "logout_btn"));
		logout_btn.setOnClickListener(this);
		
		login_btn = (Button) findViewById(MResource.getID(getApplicationContext(), "login_btn"));
		login_btn.setOnClickListener(this);
		
//		change_tv = (TextView) findViewById(MResource.getID(getApplicationContext(), "change_tv"));
//		change_tv.setOnClickListener(this);
		
		good_layout = findViewById(MResource.getID(getApplicationContext(), "good_layout"));
		good_layout.setOnClickListener(this);
		
		inviter_layout = findViewById(MResource.getID(getApplicationContext(), "inviter_layout"));
		inviter_layout.setOnClickListener(this);
		
		comments_layout = findViewById(MResource.getID(getApplicationContext(), "comments_layout"));
		comments_layout.setOnClickListener(this);
		
		about_layout = findViewById(MResource.getID(getApplicationContext(), "about_layout"));
		about_layout.setOnClickListener(this);
		
		binding_layout = findViewById(MResource.getID(getApplicationContext(), "binding_layout"));
		binding_layout.setOnClickListener(this);
		
		img_back = (ImageView) findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		img_back.setOnClickListener(this);
		
		binding_tv = (TextView) findViewById(MResource.getID(getApplicationContext(), "binding_tv"));
		
		windowManager = (WindowManager) getApplicationContext().getSystemService("window");
				
				mProgressDialog = new ProgressDialog(cyx_SettingsActivity.this);
				mProgressDialog.setTitle(getResources().getString(MResource.getStringId(getApplicationContext(), "notice")));
				mProgressDialog.setMessage(getResources().getString(MResource.getStringId(getApplicationContext(), "wait")));
				mProgressDialog.setCanceledOnTouchOutside(false);
				
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == MResource.getID(getApplicationContext(), "logout_btn")) {
			createMenu();
		} 
//		else if(v.getId() == MResource.getID(getApplicationContext(), "change_tv")) {
//			Intent intent = new Intent(cyx_Settings2Activity.this, cyx_LoginActivity.class);
//			startActivity(intent);
//		}
		else if(v.getId() == MResource.getID(getApplicationContext(), "good_layout")) {
		} else if(v.getId() == MResource.getID(getApplicationContext(), "inviter_layout")) {
//			initInviterView();
//			isShowingInviterView = true;
			//qyb begin
			Intent intent = new Intent(cyx_SettingsActivity.this, cyx_InviteFriends.class);
			startActivity(intent);
			//qyb end
		} else if(v.getId() == MResource.getID(getApplicationContext(), "comments_layout")) {
			if(ExternalInterfaceAR != null) 
				ExternalInterfaceAR.openFeedBack();
		} else if(v.getId() == MResource. getID(getApplicationContext(), "about_layout")) {
			version_number = getAppVersionName(this);
			Intent intent = new Intent(cyx_SettingsActivity.this, cyx_AboutUsActivity.class);
			intent.putExtra("version", version_number);
			startActivity(intent);
		}  else if(v.getId() == MResource.getID(getApplicationContext(), "login_btn")) {
			Intent intent = new Intent(cyx_SettingsActivity.this, cyx_LoginActivity.class);
			startActivity(intent);
//			cyx_SettingsActivity.this.finish();
		} else if(v.getId() == MResource.getID(getApplicationContext(), "btn_back")) {
			cyx_SettingsActivity.this.finish();
		} else if(v.getId() == MResource.getID(getApplicationContext(), "binding_layout")) {
			if(CYX_Drive_SDK.getInstance().getConnection().getConnectionState()!=Connection.CONN_STATE_LOGIN_OK ||  userInfo.getName().equals("temp")) {
				Toast.makeText(cyx_SettingsActivity.this,getResources().getString(MResource.getStringId(getApplicationContext(), "no_login")), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(cyx_SettingsActivity.this, cyx_LoginActivity.class);
				startActivity(intent);
				cyx_SettingsActivity.this.finish();
				return;
			}
			if(isVIP) {
				Intent intent = new Intent(cyx_SettingsActivity.this, cyx_UnBindingActivity.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(cyx_SettingsActivity.this, cyx_BindingCXBActivity.class);
				startActivity(intent);
				cyx_SettingsActivity.this.finish();
			}
		}
		
	}
	
	private void createMenu() {
		if(pop == null) {
			LayoutInflater inflater = LayoutInflater.from(this);
			View popMenu = inflater.inflate(MResource.getLayoutId(getApplicationContext(), "cyx_setting_menu"), null);
			pop = new PopupWindow(popMenu, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
			pop.setOutsideTouchable(false);
			exit_pop = (Button) popMenu.findViewById(MResource.getID(getApplicationContext(), "exit"));
			exit_pop.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//先登出
					isExitApp = true;
					mProgressDialog.show();
//					chatClient.logout(sp.getString(Constant.USER_ID, "0"));
					dealLogout();
				}
			});
			logout_pop = (Button) popMenu.findViewById(MResource.getID(getApplicationContext(), "logout"));
			logout_pop.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mProgressDialog.show();
//					chatClient.logout(sp.getString(Constant.USER_ID, "0"));
					dealLogout();
				}
			});
			cancel_pop = (Button) popMenu.findViewById(MResource.getID(getApplicationContext(), "cancel"));
			cancel_pop.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pop.dismiss();
				}
			});
		}
		pop.showAtLocation(view, Gravity.BOTTOM, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	private void initInviterView() {
		params = new WindowManager.LayoutParams();
		params.type = 2003;
		params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;

		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.RGBA_8888;
		params.gravity = Gravity.BOTTOM | Gravity.CENTER;
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(MResource.getLayoutId(cyx_SettingsActivity.this, "cyx_inviter_layout"), null);
		windowManager.addView(mView, params);
		Button cancelBtn = (Button) mView.findViewById(MResource.getID(getApplicationContext(), "cancel_btn"));
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				windowManager.removeView(mView);
				isShowingInviterView = false;
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
		isVIP = (userInfo.getFlag() == 2);
		if(isVIP && CYX_Drive_SDK.getInstance().getConnection().getConnectionState()==Connection.CONN_STATE_LOGIN_OK) {
			binding_tv.setText(getResources().getString(MResource.getStringId(getApplicationContext(), "unbinding_cxb")));
		} else {
			binding_tv.setText(getResources().getString(MResource.getStringId(getApplicationContext(), "binding_cxb")));
		}
		if(isShowingInviterView)
			windowManager.addView(mView, params);
		if(CYX_Drive_SDK.getInstance().getConnection().getConnectionState()==Connection.CONN_STATE_LOGIN_OK){
			if(!TextUtils.isEmpty(userInfo.getName())&&!userInfo.getName().equals("temp")) {
				phoneNo_tv.setText(userInfo.getName());
			} 
		}
		if(CYX_Drive_SDK.getInstance().getConnection().getConnectionState()==Connection.CONN_STATE_LOGIN_OK&& 
				!userInfo.getName().equals("temp")) {
			logout_btn.setVisibility(View.VISIBLE);
			login_btn.setVisibility(View.GONE);
			phoneNo_layout.setVisibility(View.VISIBLE);
		} else {
			logout_btn.setVisibility(View.GONE);
			phoneNo_layout.setVisibility(View.GONE);
			login_btn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(isShowingInviterView)
			windowManager.removeView(mView);
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mProgressDialog!=null&&mProgressDialog.isShowing())
			mProgressDialog.cancel();
		mProgressDialog = null;
		cyx_MyApplication.getInstance().removeActivity(this);
	}

		//获取版本号
		public static String getAppVersionName(Context context) {    
		    String versionName = "";    
		    try {       
		        PackageManager pm = context.getPackageManager();    
		        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);    
		        versionName = pi.versionName;    
		        if (versionName == null || versionName.length() <= 0) {    
		            return "";    
		        }    
		    } catch (Exception e) {    
		          
		    }    
		    return versionName;    
		}
		
		private void showHintContent() {
			if(!TextUtils.isEmpty(hintContent)) {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(pop!= null && pop.isShowing())
							pop.dismiss();
						if(mProgressDialog!=null&&mProgressDialog.isShowing())
							mProgressDialog.dismiss();
						Toast.makeText(cyx_SettingsActivity.this,hintContent,Toast.LENGTH_SHORT).show(); 
						hintContent = "";
					}
				});
			}
		}
		
		private void dealLogout() {
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mProgressDialog != null && mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
					Connection.getInstance().stop();
					CYX_Drive_SDK.getInstance().getUserManager().setAutoLogin(userInfo.getName(), 0);
					userInfo.setAutoLoginFlag(0);
					userInfo.setPassword("");
					Toast.makeText(cyx_SettingsActivity.this,getResources().getString(MResource.getStringId(getApplicationContext(),"logout_success" )),Toast.LENGTH_SHORT).show(); 
					pop.dismiss();
					cyx_MyApplication.getStrokeDataList().clear();
					if(!isExitApp) {
						if(ExternalInterfaceAR!=null)
							ExternalInterfaceAR.OnUserLogout(0, "");
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(CYX_Drive_SDK.getInstance().getConnection().getConnectionState()!=Connection.CONN_STATE_LOGIN_OK) {
									logout_btn.setVisibility(View.GONE);
									phoneNo_layout.setVisibility(View.GONE);
									login_btn.setVisibility(View.VISIBLE);
								} else {
									login_btn.setVisibility(View.GONE);
									phoneNo_layout.setVisibility(View.VISIBLE);
								}
								Connection.getInstance().setStartType(Connection.START_TYPE_CONNECT_ONLY);
							}
						});
						Intent intent=new Intent(cyx_SettingsActivity.this, cyx_LoginActivity.class);
						startActivity(intent);
					} else {
						Intent intent2 = new Intent(cyx_SettingsActivity.this, cyx_XMPPService.class);
			        	stopService(intent2);
						CYX_Drive_SDK.getInstance().getConnection().stop();
						cyx_MyApplication.getInstance().exitApplication();
					}
				}
			}, 1*1000);
			/*Connection.getInstance().sendExtData(new ExtraDataProcess().getLogoutData(userInfo.getUserID()), 
					new RequestCallback() {

						@Override
						public void onSuccess(String bizJsonData) {
							// TODO Auto-generated method stub
							 * if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_SettingsActivity.this.getClass().getName())) {
							if(mProgressDialog != null && mProgressDialog.isShowing()) {
								mProgressDialog.dismiss();
							}
							try {
								JSONObject jsonObj = new JSONObject(bizJsonData);
								switch(jsonObj.getInt("result")) {
								case ConstantContext.SUCCESS:
									ConnectionManager.getInstance().setUserStatus(UserStatus.LOGOUT);
									CYX_Drive_SDK.getInstance().getUserManager().setAutoLogin(userInfo.getName(), 0);
									userInfo.setAutoLoginFlag(0);
									userInfo.setPassword("");
									Toast.makeText(cyx_SettingsActivity.this,getResources().getString(MResource.getStringId(getApplicationContext(),"logout_success" )),Toast.LENGTH_SHORT).show(); 
									pop.dismiss();
									if(!isExitApp) {
										if(ExternalInterfaceAR!=null)
											ExternalInterfaceAR.OnUserLogout(0, "");
										ConnectionManager.needReConn = false; //标志不需要自动重连
										ConnectionManager.getInstance().closeConnection();
										handler.post(new Runnable() {
											
											@Override
											public void run() {
												// TODO Auto-generated method stub
												if(UserStatus.LOGIN != ConnectionManager.getInstance().getUserStatus()) {
													logout_btn.setVisibility(View.GONE);
													phoneNo_layout.setVisibility(View.GONE);
													login_btn.setVisibility(View.VISIBLE);
												} else {
													login_btn.setVisibility(View.GONE);
													phoneNo_layout.setVisibility(View.VISIBLE);
												}
											}
										});
									} else {
										Intent intent2 = new Intent(cyx_SettingsActivity.this, cyx_XMPPService.class);
							        	stopService(intent2);
										CYX_Drive_SDK.getInstance().getConnection().stop();
										cyx_MyApplication.getInstance().exitApplication();
									}
									break;
								case ConstantContext.ERROR_1:
									hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"user_id_null" ));
									if(ExternalInterfaceAR!=null)
										ExternalInterfaceAR.OnUserLogout(1, "");
									break;
								case ConstantContext.ERROR_2:
									hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"logout_id_type_null" ));
									if(ExternalInterfaceAR!=null)
										ExternalInterfaceAR.OnUserLogout(2, "");
									break;
								case ConstantContext.ERROR_3:
									hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"procedure_error" ));
									if(ExternalInterfaceAR!=null)
										ExternalInterfaceAR.OnUserLogout(3, "");
									break;
								case ConstantContext.ERROR_4:
									hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"user_error" ));
									if(ExternalInterfaceAR!=null)
										ExternalInterfaceAR.OnUserLogout(4, "");
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
							 * if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_SettingsActivity.this.getClass().getName())) {
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
					});*/
		}
		
}