package com.cwits.cyx_drive_sdk.integrate;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BaiduNaviManager;
import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.connection.cyx_XMPPService;
import com.cwits.cyx_drive_sdk.data.AlarmInfoManage;
import com.cwits.cyx_drive_sdk.data.IAlarmInfoManage;
import com.cwits.cyx_drive_sdk.exception.CrashHandler;
import com.cwits.cyx_drive_sdk.exception.LogUtil;
import com.cwits.cyx_drive_sdk.ui.MResource;
import com.cwits.cyx_drive_sdk.ui.cyx_CarFortifyActivity;
import com.cwits.cyx_drive_sdk.ui.cyx_CarLocationActivity;
import com.cwits.cyx_drive_sdk.ui.cyx_CarVipHintActivity;
import com.cwits.cyx_drive_sdk.ui.cyx_DriveRecodeActivity;
import com.cwits.cyx_drive_sdk.ui.cyx_HintCarFortifyActivity;
import com.cwits.cyx_drive_sdk.ui.cyx_LoginActivity;
import com.cwits.cyx_drive_sdk.ui.cyx_MainActivity;
import com.cwits.cyx_drive_sdk.ui.cyx_MyApplication;
import com.cwits.cyx_drive_sdk.ui.cyx_RegisterActivity;
import com.cwits.cyx_drive_sdk.ui.cyx_SettingsActivity;
import com.cwits.cyx_drive_sdk.ui.cyx_TripHistoryMapActivity;
import com.cwits.cyx_drive_sdk.ui.whb_ShopLocationActivity;
import com.cwits.cyx_drive_sdk.userInfo.APPWHBListener;
import com.cwits.cyx_drive_sdk.userInfo.Constant;
import com.cwits.cyx_drive_sdk.userInfo.IExternalInterfaceAR;
import com.cwits.cyx_drive_sdk.userInfo.IExternalRecevier;
import com.cwits.cyx_drive_sdk.userInfo.IUserManager;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;
import com.cwits.cyx_drive_sdk.userInfo.UserManager;
import com.netwise.ematch.OpenWHBParam;
import com.netwise.ematch.WHBClient;

public class CYX_Drive_SDK {
    private static CYX_Drive_SDK instance;       //应用程序上下文对象
    private static Context mContext=null;
	private IExternalInterfaceAR ExternalInterfaceAR;
	private UserInfo userInfo;
	public boolean hasInit = false;	//标志是否已经初始化
	private IUserManager userManager; 			// 用户信息管理类
	private IConfiguration mConfiguration;		// 程序配置类
	private IAlarmInfoManage alarmInfoManage ; 	// 报警管理
	private Connection conn;
	public static CYX_Drive_SDK getInstance(){
		if(instance==null)
			instance = new CYX_Drive_SDK();
		return instance;
	}
	private CYX_Drive_SDK(){
	}
	
	public static Context getSavedContext() {
		return mContext;
	}
	
	/**
	 * 初始化SDK相关
	 */
	public void Initialize(Context context){
		WHBClient.getInstance().init(mContext);
		APPWHBListener listener = new APPWHBListener();
		WHBClient.getInstance().setWHBListener(listener);
		WHBClient.getInstance().setWxAppId("wxa668f426b2e9ca21");
		if(!hasInit) {
			mContext = context;
			CrashHandler crashHandler = CrashHandler.getInstance();
			crashHandler.init();						
			
			//启动服务
			Intent intent = new Intent(mContext, cyx_XMPPService.class);
			mContext.startService(intent);
			getUserManager().initUserInfo(context);
			
		}
	}
	/**
	 * 初始化SDK相关  SDK 使用
	 * @param context 
	 * @param flag 是否为SDK
	 */
	public void Initialize(Context context,int flag){
		
		if(!hasInit) {
			mContext = context;
			CrashHandler crashHandler = CrashHandler.getInstance();
			crashHandler.init();
			//启动服务
			Intent intent = new Intent(mContext, cyx_XMPPService.class);
			mContext.startService(intent);
			getUserManager().initUserInfo(context);
			mContext.getSharedPreferences(Constant.SDK_FLAG, Context.MODE_PRIVATE).edit().putInt(Constant.SDK_FLAG, flag).commit();
		}
	}
	public int getSdkFlag(){
		return mContext.getSharedPreferences(Constant.SDK_FLAG, Context.MODE_PRIVATE).getInt(Constant.SDK_FLAG,0);
	}
	
	public void setExternalInterface(IExternalInterfaceAR listener){
		   ExternalInterfaceAR=listener;
		
	    }
	   
	   public IExternalInterfaceAR getExternalInterface(){
		   return ExternalInterfaceAR;
		   
	   }
	   
	   
	   public void getExternalReceiver(IExternalRecevier receiver){
		   
	   }
	
	public Connection getConnection(){
		if(conn==null)
			conn = Connection.getInstance();
		return conn;
	}
	
	public UserInfo getUserInfo() {
		if(userInfo == null);
			userInfo = new UserInfo(mContext,getUserManager().getCurrentUserName());
		return userInfo;
	}
	
	/**
	 * 跳转到开始驾驶页面
	 * 1:造的
	 * 0：真实
	 */
	public void startToDrive(int  simulate){
		Intent intent =  new Intent((Context)getSavedContext(), cyx_MainActivity.class);
		intent.putExtra("simulate", simulate);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getSavedContext().startActivity(intent);
		cyx_MyApplication.getInstance().initEngineManager(mContext.getApplicationContext());
	}
	/**
	 *跳转到注册界面
	 */
	public void IntentToRegister(){
		Intent intent = new Intent((Context)getSavedContext(), cyx_RegisterActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getSavedContext().startActivity(intent);
	}
	/**
     * 跳转到登录界面
     */
	public void startToLogin(){
		Intent intent = new Intent((Context)getSavedContext(), cyx_LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getSavedContext().startActivity(intent);
	}
	/**
     * 微惠宝地图界面
     *  @param lat：经度 ，lon：纬度	 content：地址
     */
	public void startWHBMap(double lat,double lon,String content){
		Intent intent = new Intent((Context)getSavedContext(), whb_ShopLocationActivity.class);
		intent.putExtra("lat", lat);
		intent.putExtra("lon", lon);
		intent.putExtra("content", content);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getSavedContext().startActivity(intent);
	}
	/**
     * 增加金币值
     */
	public void notifyAddGold(final int addNum){
		cyx_MainActivity.coinNumber(addNum);
	}
	/**
     * 退出所有服务
     */
	public void exitSDK(){
		Intent intent2 = new Intent(mContext, cyx_XMPPService.class);
		mContext.stopService(intent2);
		cyx_MyApplication.getInstance().exitApplication();
	}
	
	/**
	 * 跳转到设置界面
	 */
	public void startToSettings(){
		Intent intent = new Intent((Context)getSavedContext(),cyx_SettingsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getSavedContext().startActivity(intent);
	}
	
	/**
	 * 跳转到历史行程列表界面
	 *  * 1:造的
	 * 0：真实
	 */
	public void startToHistoryTrip(int simulate){
		Intent intent = new Intent((Context)getSavedContext(),cyx_DriveRecodeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("simulate", simulate);
		getSavedContext().startActivity(intent);
	}
	
	/**
	 * 跳转到行程详情界面
	 * @param jsonStr json字符串
	 */
	public void startToTripDetail(String jsonStr) {
		Intent intent = new Intent((Context)getSavedContext(),cyx_TripHistoryMapActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("jsonStr", jsonStr);
		getSavedContext().startActivity(intent);
	}
	
	//导航初始化监听
	private static NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {
		  			public void engineInitSuccess() {
		  				System.out.println("初始化导航引擎成功！");
		  			}
		  			public void engineInitStart() {
		  				System.out.println("初始化导航引擎开始");
		  			}
		  			public void engineInitFail() {
		  				System.out.println("初始化导航引擎失败");
		  			}
		  		};
	public static String getSdcardDir() {
		  		if (Environment.getExternalStorageState().equalsIgnoreCase(
		  				Environment.MEDIA_MOUNTED)) {
		  			return Environment.getExternalStorageDirectory().toString();
		  		}
		  		return null;
		 }
	/**
	 * 初始化导航引擎
	 * @param context
	 */
	public static void initBaiduNavi(Context context){
				BaiduNaviManager.getInstance().initEngine((Activity) context, 
						getSdcardDir(), mNaviEngineInitListener,context.getResources().getString(
						MResource.getStringId(context, "BMAP_ACCESS_KEY")),null);
			}
	
	public IUserManager getUserManager(){
		if(userManager == null)
			userManager = new UserManager();
		return userManager;
	}
	
	public IConfiguration getConfiguration(){
		if(mConfiguration == null)
			mConfiguration = new cyx_Configuration();
		return mConfiguration;
	}
	//返回程序文件夹下app_CYX_DRIVE 路径
	public String getConfigDir(){
		return getConfiguration().getConfigDir();
	}
	
	public IAlarmInfoManage getAlarmInfoManage(){
		if(alarmInfoManage==null)
			alarmInfoManage = new AlarmInfoManage();
		return alarmInfoManage;
	}
	//跳转至车辆设防界面
	public void startToCarFortify(Context context){
		Intent intent;
		if(getUserInfo().getFlag()==2){
			intent = new Intent(context, cyx_CarLocationActivity.class);
		}else{
			intent = new Intent(context, cyx_CarVipHintActivity.class);
		}
		context.startActivity(intent);
	}
	//跳转至车辆位置
	public void startToCarLocation(Context context){
		Intent intent;
		if(getUserInfo().getFlag()==2){
			intent = new Intent(context, cyx_CarFortifyActivity.class);
		}else{
			intent = new Intent(context, cyx_HintCarFortifyActivity.class);
		}
		context.startActivity(intent);
	}
	//跳转至微惠宝
	public void startToWHB(){
		String city=mContext.getSharedPreferences(Constant.ADDRESS_INFO, Context.MODE_PRIVATE).getString(Constant.ADDRESS_CITY, "广州");
		int location = city.indexOf("市");
        String newStr = city.substring(0, location);
		OpenWHBParam param = new OpenWHBParam();
		param.area =  Integer.valueOf(userInfo.getArea()).intValue();
		param.uid = userInfo.getUserID();//"1404294898790644";
		param.token = userInfo.getToken();//"344072916";
		param.phoneNumber = userInfo.getName();//"15818718966";
		param.currentCity = newStr;
		param.currentLongitude = Double.parseDouble(mContext.getSharedPreferences(Constant.ADDRESS_INFO,Context.MODE_PRIVATE).getString(Constant.LON, "122"));
		param.currentLatitude = Double.parseDouble(mContext.getSharedPreferences(Constant.ADDRESS_INFO, Context.MODE_PRIVATE).getString(Constant.LAT, "22"));
		if (false == WHBClient.getInstance().open(param)) {
			
		}
		Log.e("lxh","param=="+param.area+"  "+param.uid+"  "
				+param.token+"  "+param.currentCity+"  "+param.currentLatitude+"  "
				+param.currentLongitude);
		

	}
	
	//重新连接
	public void retryConnect(){
		getConnection().stop();
		getConnection().setStartType(Connection.START_TYPE_DEFAULT);
		getConnection().start();
	}
}
