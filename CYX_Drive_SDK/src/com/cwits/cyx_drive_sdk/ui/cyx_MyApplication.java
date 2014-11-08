package com.cwits.cyx_drive_sdk.ui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.search.MKPoiResult;
import com.cwits.cyx_drive_sdk.bean.ARAwardInfo;
import com.cwits.cyx_drive_sdk.bean.AlarmData;
import com.cwits.cyx_drive_sdk.bean.PositionResult;
import com.cwits.cyx_drive_sdk.bean.StrokeResult;
import com.cwits.cyx_drive_sdk.data.PersistentStorageProvider;
import com.cwits.cyx_drive_sdk.exception.LogUtil;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;


public class cyx_MyApplication extends Application{
	private static final String TAG = "cyx_MyApplication";
	private static cyx_MyApplication mInstance = null;
    //用于存放�?��日志模式的year、month、day
    public static int year;
    public static int month;
    public static int day;
    public static boolean isLogMode = false; //是否�?��了日志模�?
    
    public static List<StrokeResult>  mStrokeDataList;
    public static List<ARAwardInfo>  mARAwardInfoList;
    
    private static Stack<Activity> activityStack;
    public static MKPoiResult myPoiResult;
    public static List<PositionResult> positionResultList;
    public static BMapManager mBMapManager = null;
    public static List<AlarmData> mAlarmList;
	@Override
    public void onCreate() {
	    super.onCreate();
	    Log.d("--------"+TAG, "app oncreate");
		mInstance = this;
		
	}
	
	public static cyx_MyApplication getInstance() {
		if (null == mInstance) {
			mInstance = new cyx_MyApplication();
		}
		
		return mInstance;
	}
	/**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity){
        if(activityStack ==null){
            activityStack =new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    
    public Stack<Activity> getActivityStack() {
    	if(activityStack ==null)
    		return new Stack<Activity>();
    	return activityStack;
    }
    
    /**
     * 移除指定的Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity = null;
        }
        if(activityStack.size() == 0) {
        	if(isLogMode) {
	        	if(LogUtil.saveLog(this.getApplicationContext())) {
	        		Log.d(TAG, "保存log数据成功");
	        	} else {
	        		Log.d(TAG, "保存log数据失败");
	        	}
        	}
//        	Intent intent = new Intent(this, cyx_XMPPService.class);
//        	stopService(intent);
        }
    }
    
    
    public void exitApplication() {
    	if(activityStack != null) {
    		for(int i=0,size=activityStack.size(); i<size; i++) {
    			activityStack.get(i).finish();
    		}
    	}
    	if(CYX_Drive_SDK.getInstance().getExternalInterface()!=null)
    		CYX_Drive_SDK.getInstance().getExternalInterface().OnExitApplication();
//    	System.exit(0);
    }
    
	public static MKPoiResult getMyPoiResult(){
		if(null == myPoiResult){
			myPoiResult = new MKPoiResult();
		}
		return myPoiResult;
	}
	public static void setMyPoiResult(MKPoiResult result){
		if(null != result) {
			if(null == myPoiResult) {
				myPoiResult = new MKPoiResult();
			}else{
				myPoiResult = null;
			}
		}
		myPoiResult = result ;
	}
	
	public static List<StrokeResult> getStrokeDataList() {
		if(null == mStrokeDataList)
			mStrokeDataList = Collections.synchronizedList(new LinkedList<StrokeResult>());
		return mStrokeDataList;
	}
	
	public static void setStrokeDataList(List<StrokeResult> list) {
		if(null == list) {
			return ;
		} else {
			mStrokeDataList = list;
		}
	}

	public static List<PositionResult> getPositionResultList(){
		if(null==positionResultList)
			positionResultList = new LinkedList<PositionResult>();
		return positionResultList;
	}
	public static void setPositonResultList(List<PositionResult> list){
		if(null == list) {
			return ;
		} else {
			positionResultList = list;
		}
	}

	public static List<ARAwardInfo> getmARAwardInfoList() {
		if(null == mARAwardInfoList)
			mARAwardInfoList = new LinkedList<ARAwardInfo>();
		return mARAwardInfoList;
	}

	public static void setmARAwardInfoList(List<ARAwardInfo> mARAwardInfoList) {
		if(null == mARAwardInfoList) {
			return ;
		} else {
			cyx_MyApplication.mARAwardInfoList = mARAwardInfoList;
		}
	}
	
	public static List<AlarmData> getAlarmList(){
		if(null==mAlarmList)
			mAlarmList = new LinkedList<AlarmData>();
		return mAlarmList;
	}
	public static void setAlarmList(List<AlarmData> alarmList){
		if(null==mAlarmList)
			return;
		else 
			cyx_MyApplication.mAlarmList = alarmList;
	}
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(new MyGeneralListener())) {
        }
	}
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
            }
        }
        @Override
        public void onGetPermissionState(int iError) {
        	//非零值表示key验证未通过
            if (iError != 0) {
            }
            else{
            }
        }
    }
    
}
