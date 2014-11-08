package com.cwits.cyx_drive_sdk.connection;

import java.io.ByteArrayInputStream;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.cwits.cyx_drive_sdk.bean.AlarmData;
import com.cwits.cyx_drive_sdk.data.AlarmType;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.exception.LogUtil;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.integrate.IConfiguration;
import com.cwits.cyx_drive_sdk.ui.cyx_KickoutHintActivity;
import com.cwits.cyx_drive_sdk.userInfo.Constant;
import com.cwits.cyx_drive_sdk.userInfo.IExternalInterfaceAR;
import com.cwits.cyx_drive_sdk.userInfo.IUserManager;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;
import com.cwits.cyx_drive_sdk.util.TimeUtil;

public class cyx_XMPPService extends Service implements BDLocationListener,
		IConnStateListener, MessageListener{
	private LocationClient poiClient;
	private SharedPreferences adrPreference; // 储存地址
	NetworkChangedReceiver netChgReceiver = new NetworkChangedReceiver();
	Connection conn;
	Thread th; // 创建临时账号线程
	boolean tempAccount = false; // 临时账号是否创建完毕
	ExtraDataProcess ed;
	String androidId; // 手机特征码
	String area; // 用户所在区域
	private static IExternalInterfaceAR mIExternalInterfaceAR;
	Chat mChat;
	private static final String CYX_MSG_NAME = "cyxmsg";
	private static final String MSGTAG_NAMESPACE = "com:cwits:cyxmobilegate";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		// TODO Auto-generated method stub
		IntentFilter intentFilter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(netChgReceiver, intentFilter);
		CYX_Drive_SDK.getInstance().Initialize(cyx_XMPPService.this);
		adrPreference = getSharedPreferences(Constant.ADDRESS_INFO,
				MODE_PRIVATE);
		conn = CYX_Drive_SDK.getInstance().getConnection();
		conn.addConnStateListener(this);
		mIExternalInterfaceAR = CYX_Drive_SDK.getInstance()
				.getExternalInterface();
		initLocation();
		createConnect();
		super.onCreate();
	}

	private void initLocation() {
		poiClient = new LocationClient(getApplicationContext());
		poiClient.registerLocationListener(this);
		LocationClientOption option2 = new LocationClientOption();
		option2.setCoorType("bd09ll");
		option2.setScanSpan(1000);
		option2.setNeedDeviceDirect(true);
		option2.setIsNeedAddress(true);
		option2.setLocationMode(LocationMode.Hight_Accuracy);
		poiClient.setLocOption(option2);
		poiClient.start();
		poiClient.requestLocation();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(netChgReceiver);
		CYX_Drive_SDK.getInstance().getConnection().stop();
	}

	// 服务启动时开始定位，搜索当前省份和城市
	@Override
	public void onReceiveLocation(BDLocation location) {
		if(location != null) {
			adrPreference.edit().putString(Constant.LAT, String.valueOf(location.getLatitude())).commit();
			adrPreference.edit().putString(Constant.LON, String.valueOf(location.getLongitude())).commit();
		}
		String province = "";
		String city = "";
		if (!TextUtils.isEmpty(location.getProvince())) {
			Log.i("lxh", "province:" + location.getProvince() + "   city:"
					+ location.getCity());
			province = location.getProvince();
			saveProvince(province);
			if (!TextUtils.isEmpty(location.getCity())) {
				city = location.getCity();
				adrPreference.edit().putString(Constant.ADDRESS_CITY, city)
						.commit();
			}
			poiClient.stop();
		} else {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "30")
					.commit();
			adrPreference.edit().putString(Constant.ADDRESS_CITY, "深圳")
					.commit();
		}

	}

	// 保存区域编码
	private void saveProvince(String provinceString) {

		if (provinceString.equals("广东省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "30")
					.commit();
		} else if (provinceString.equals("安徽省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "150")
					.commit();
		} else if (provinceString.equals("澳门")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "20")
					.commit();
		} else if (provinceString.equals("北京")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "240")
					.commit();
		} else if (provinceString.equals("重庆")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "130")
					.commit();
		} else if (provinceString.equals("福建省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "60")
					.commit();
		} else if (provinceString.equals("甘肃省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "310")
					.commit();
		} else if (provinceString.equals("广西省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "90")
					.commit();
		} else if (provinceString.equals("贵州省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "120")
					.commit();
		} else if (provinceString.equals("海南省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "10")
					.commit();
		} else if (provinceString.equals("河北省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "230")
					.commit();
		} else if (provinceString.equals("河南省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "190")
					.commit();
		} else if (provinceString.equals("黑龙江省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "290")
					.commit();
		} else if (provinceString.equals("湖北省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "140")
					.commit();
		} else if (provinceString.equals("湖南省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "80")
					.commit();
		} else if (provinceString.equals("吉林省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "280")
					.commit();
		} else if (provinceString.equals("江苏省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "180")
					.commit();
		} else if (provinceString.equals("江西省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "70")
					.commit();
		} else if (provinceString.equals("辽宁省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "270")
					.commit();
		} else if (provinceString.equals("内蒙古省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "300")
					.commit();
		} else if (provinceString.equals("宁夏省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "210")
					.commit();
		} else if (provinceString.equals("青海省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "320")
					.commit();
		} else if (provinceString.equals("四川省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "110")
					.commit();
		} else if (provinceString.equals("山东省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "250")
					.commit();
		} else if (provinceString.equals("上海市")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "170")
					.commit();
		} else if (provinceString.equals("山西省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "220")
					.commit();
		} else if (provinceString.equals("陕西省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "200")
					.commit();
		} else if (provinceString.equals("天津市")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "260")
					.commit();
		} else if (provinceString.equals("台湾省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "50")
					.commit();
		} else if (provinceString.equals("香港")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "40")
					.commit();
		} else if (provinceString.equals("新疆省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "340")
					.commit();
		} else if (provinceString.equals("西藏省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "330")
					.commit();
		} else if (provinceString.equals("云南省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "100")
					.commit();
		} else if (provinceString.equals("浙江省")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "160")
					.commit();
		} else if (provinceString.equals("未知")) {
			adrPreference.edit().putString(Constant.ADDRESS_PROVINCE, "135")
					.commit();
		}
	}

	private void createConnect() {
		IConfiguration config = CYX_Drive_SDK.getInstance().getConfiguration();
		config.InitConfig(this);
		CYX_Drive_SDK.getInstance().getUserManager().initUserInfo(this);
		UserInfo userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
		if (!config.isTestServer()) {
			conn.setServerAddrPort(config.getServerIP(), config.getServerPort());
		} else {
			conn.setServerAddrPort(config.getTestServerIP(),
					config.getTestServerPort());
		}
		if (userInfo.getUserID().equals("")
				|| userInfo.getUserID().equals("NULL")) {
			conn.setStartType(Connection.START_TYPE_CONNECT_ONLY);
			
		} else if (!TextUtils.isEmpty(userInfo.getName())
				&& !TextUtils.isEmpty(userInfo.getPassWord())
				&& userInfo.getAutoLoginFlag() == 1) {
			conn.setStartType(Connection.START_TYPE_DEFAULT);
			conn.setUsernamePasswd(userInfo.getUserID(), userInfo.getPassWord());
		} else if (!TextUtils.isEmpty(userInfo.getName())
				&& userInfo.getAutoLoginFlag() == 0) {
			conn.setStartType(Connection.START_TYPE_CONNECT_ONLY);
		}
		conn.start();
	}

	@Override
	public void onConnStateChanged(int oldState, int newState) {
		// TODO Auto-generated method stub
		UserInfo us = CYX_Drive_SDK.getInstance().getUserInfo();
		switch (newState) {
		case Connection.CONN_STATE_CONNECTED:
			if (us.getUserID().equals("") || us.getUserID().equals("NULL")) {
				getTempAndLogin();
			}
			break;
		case Connection.CONN_STATE_LOGIN_OK:
			if (mIExternalInterfaceAR != null) {
				mIExternalInterfaceAR.OnUserLogin(0, 0, "");
			}
			CYX_Drive_SDK.getInstance().getUserManager()
					.setDefaultUser(us.getName());
			CYX_Drive_SDK.getInstance().getUserManager()
					.createAllFilesForDirector(us.getName());
			// 如果是VIP用户登录，登录成功之后查询一下设备状态,并创建一个会话
			if (us.getFlag() == 2) {
				getFortifyState(us);
				mChat = ChatManager.getInstanceFor(conn.getConn()).createChat(
						us.getUserID()+"@chainwayits", cyx_XMPPService.this);
				ChatManager chatManager = ChatManager.getInstanceFor(conn.getConn());
				chatManager.addChatListener(new ChatManagerListener() {  
		            public void chatCreated(Chat chat, boolean arg1) {  
		            	
		                chat.addMessageListener(cyx_XMPPService.this);  
		            }  
		        });  
			}
			break;
		case Connection.CONN_STATE_KICKOUT:
			Intent intent = new Intent(cyx_XMPPService.this,
					cyx_KickoutHintActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		}
	}

	private void getTempAndLogin() {
		androidId = Secure.getString(CYX_Drive_SDK.getSavedContext()
				.getContentResolver(), Secure.ANDROID_ID);
		area = CYX_Drive_SDK
				.getSavedContext()
				.getSharedPreferences(Constant.ADDRESS_INFO,
						Context.MODE_PRIVATE)
				.getString(Constant.ADDRESS_PROVINCE, "30");
		ed = new ExtraDataProcess();
		if (th == null) {
			th = new Thread(new Runnable() {

				@Override
				public void run() {
					while (!tempAccount) {
						Log.d("lxh", "--------注册临时账号");
						conn.sendExtData(
								ed.getTempAccountData(androidId, area),
								new RequestCallback() {
									public void onSuccess(String bizJsonData) {
										tempAccount = true;
										String resultCode = "";
										try {
											JSONObject jsonObj = new JSONObject(bizJsonData);
											resultCode = jsonObj.getString("result");
											Log.d("lxh",
													"------注册临时账号 resultCode： "
															+ resultCode);
											if ("0".equals(resultCode)|| ("1".equals(resultCode))) {
												Log.d("lxh",
														"--------注册临时账号成功！");
												String id = jsonObj
														.getString("id");
												CYX_Drive_SDK.getInstance().getUserManager().setDefaultUser("temp");
												UserInfo userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
												userInfo.setName("temp");
												userInfo.setFlag(IUserManager.TEMP_ACCOUNT);
												userInfo.setUserID(jsonObj.getString("id"));
												userInfo.setPassword(jsonObj.optString("password"));
												conn.sendExtData(
														ed.getLoginData("3",id,id,jsonObj.optString("password")),
														new RequestCallback() {

															@Override
															public void onSuccess(
																	String bizJsonData) {
																JSONObject jsonObj_login;
																try {
																	jsonObj_login = new JSONObject(bizJsonData);
																	String resultCode_login = jsonObj_login.getString("result");
																	if (resultCode_login.equals("0")) {
																		// 临时账号登陆平台成功
																		Log.d("lxh",
																				"--------临时账号登陆平台成功！");
																		UserInfo user = CYX_Drive_SDK.getInstance().getUserInfo();
																		user.setToken(jsonObj_login.getString("token"));
																		conn.setStartType(Connection.START_TYPE_DEFAULT);
																		conn.login(user.getUserID(),user.getPassWord(),
																				new RequestCallback() {
																					public void onSuccess(
																							String bizJsonData) {

																						UserInfo user = CYX_Drive_SDK.getInstance().getUserInfo();
																						user.setAutoLoginFlag(1);
																						CYX_Drive_SDK.getInstance().getUserManager().addUser(
																										"temp",1,user.getPassWord(),0);
																						CYX_Drive_SDK.getInstance().getUserManager().setDefaultUser(
																										"temp");
																						LogUtil.saveSingleData(
																								cyx_XMPPService.this,
																								"-----temp token:"
																										+ user.getToken());
																						if (mIExternalInterfaceAR != null) {
																							mIExternalInterfaceAR
																									.OnUserLogin(0,0,"");
																						}
																						System.out.println("------临时账号登录XMPP成功");
																					};

																					@Override
																					public void onFailed(
																							int reason) {
																						System.out.println("------临时账号登录XMPP失败 reason: "+ reason);
																					}
																				});
																	}
																} catch (JSONException e) {
																	e.printStackTrace();
																}

															}

															@Override
															public void onFailed(
																	int reason) {

																Log.d("lxh","-------登录平台失败 reason: "+ reason);
															}

														});
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}

									public void onFailed(int reason) {
										Log.d("lxh", "-------注册失败 reason: "
												+ reason);
									}
								});
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			});
		}
		if (th != null && !th.isAlive())
			th.start();
	}

	// 如果是VIP用户登录后查询设防状态
	private void getFortifyState(final UserInfo user) {

		conn.sendExtData(new ExtraDataProcess().getMyCarData(user.getUserID()),
				new RequestCallback() {
					@Override
					public void onSuccess(String bizJsonData) {

						JSONObject jsonObj;
						try {
							jsonObj = new JSONObject(bizJsonData);
							int resultCode = jsonObj.getInt("result");
							Log.i("lxh", "------getFortifyState resultCode: "+ resultCode);
							switch (resultCode) {
							case 0:
								user.setFortifyState(jsonObj.getInt("fortifyState"));
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
						super.onFailed(reason);
					}
				});
	}

	@Override
	public void processMessage(Chat chat, Message message) {
		praseMessage(message.getBody());
	}

	private void praseMessage(String message) {
		String jsonData = "";
		boolean isTagCyxmsg = false;
		ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(message.getBytes());
		XmlPullParser xmlData = Xml.newPullParser();
		try {
			xmlData.setInput(tInputStringStream, "UTF-8");
			int eventType = xmlData.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (null != xmlData.getName()
							&& xmlData.getName().equalsIgnoreCase(CYX_MSG_NAME)) {
						isTagCyxmsg = true;
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					if (null != xmlData.getName()
							&& xmlData.getName().equalsIgnoreCase(CYX_MSG_NAME)) {
						isTagCyxmsg = false;
						break;
					}
				} else if (eventType == XmlPullParser.TEXT) {
					if (isTagCyxmsg) {
						jsonData = (null != xmlData.getText() ? xmlData.getText()
								: "");
					}
				}

				try {
					eventType = xmlData.next();
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("----------------Message jsonData: "+ jsonData);
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(jsonData);
			AlarmData alarm = new AlarmData();
			int type = jsonObj.getInt("type");
			System.out.println("---------------AlarmType:"+type);
			if(type==AlarmType.FOTIFY_ALARM)
				alarm.setPriority(1);
			alarm.setAccStatus(jsonObj.getInt("accState"));
			alarm.setUserId(jsonObj.optString("id"));
			alarm.setFixTime(TimeUtil.parseToLocal(jsonObj.getString("time")));
			alarm.setAlarmType(type);
			CYX_Drive_SDK.getInstance().getAlarmInfoManage().receivedAlarm(alarm);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	
}
