package com.cwits.cyx_drive_sdk.exception;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.userInfo.IExternalInterfaceAR;
import com.cwits.cyx_drive_sdk.util.FileUtil;

public class LogUtil {
	private static IExternalInterfaceAR mIExternalInterfaceAR;
	private static final String TAG = "LogUtil";
	public static final int DEBUG = 0;
	public static final int ERROR = 1;
	public static final int INFO = 2;
	public static final int VERBOSE = 3;
	public static final int WARN = 4;
	public static boolean logTypeAR = true;
	
	private static List<String> list = new ArrayList<String>();
	private static String SUBDIR = "logs";
	
	public static void printLog(int Type, String tag, String msg) {
		switch (Type) {
		case LogUtil.DEBUG:
			Log.d(tag, msg);
			break;
		case LogUtil.ERROR:
			Log.e(tag, msg);
			break;
		case LogUtil.INFO:
			Log.i(tag, msg);
			break;
		case LogUtil.VERBOSE:
			Log.v(tag, msg);
			break;
		case LogUtil.WARN:
			Log.w(tag, msg);
			break;
		}
		 
//		list.add(content);
		if(true){
		String content = "[" + getDateTime() + "]" + tag + ": " + msg;
		saveSingleLog(CYX_Drive_SDK.getSavedContext(), content);
		}
		
		
//		
	}

	
	public static void TraceLn(String msg){
		 mIExternalInterfaceAR = CYX_Drive_SDK
					.getInstance().getExternalInterface();
		if (mIExternalInterfaceAR != null)
			mIExternalInterfaceAR.TraceLn(msg);	
	}
	
	public static List<String> getList() {
		return list;
	}

	public static void setList(List<String> list) {
		LogUtil.list = list;
	}
	
	public static boolean addData(String str) {
		return list.add(str);
	}
	
	public static boolean saveSingleData(Context context, String str) {
		return saveSingleLog(context, str);
	}
	
	public static List<String> getHistoryFileNames(Context context) {
//		return FileUtil.getAllFiles(context, SUBDIR);
		return null;
	}
	
	public static String getDetailByFileName(Context context, String fileName) {
//		if(FileUtil.isFileExist(cyx_MyApplication.getInstance().getApplicationContext(), SUBDIR, fileName)) {
//			return FileUtil.readJsonStrFromSD(context, SUBDIR, fileName);
//		}
		return "";
	}
	
	public static boolean deleteFile(Context context, String fileName) {
//		try {
//			return FileUtil.deleteFile(context, SUBDIR, fileName);
//		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return false;
	}

	/**
	 * 批量写日志
	 * @param context
	 * @return
	 */
	public static boolean saveLog(Context context) {
		if(list == null || list.size() == 0) {
			return false;
		}
//		//先判断文件是否存在
//		if(!FileUtil.isFileExist(context, SUBDIR,  getDate())) {
//			//创建行程历史数据文件
//			try {
//				FileUtil.createFileInSDCard(context, SUBDIR, getDate());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				LogUtil.printLog(LogUtil.DEBUG, TAG, "创建log文件失败");
//				return false;
//			}
//		}
		String str = "";
		for(int i=0, size=list.size(); i<size; i++) {
			str += list.get(i) + "\n";
		}
		return false;
	}

	/**
	 * 写单条日志
	 * @param context
	 * @return
	 */
	public static boolean saveSingleLog(Context context, String content) {
		if(CYX_Drive_SDK.getInstance().getSdkFlag()==1){
			//先判断文件是否存在
			if(!FileUtil.isFileExist(context, SUBDIR,  getDate())) {
				//创建行程历史数据文件
				try {
					FileUtil.createFileInSDCard(context, SUBDIR, getDate());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LogUtil.printLog(LogUtil.DEBUG, TAG, "创建log文件失败");
					return false;
				}
			}
			return FileUtil.writeJsonStr2SD(context, SUBDIR, getDate(), content + "\n");
		}
		else{
			if(logTypeAR){
			TraceLn(content);
				return true;
			}else{
			if(TextUtils.isEmpty(content)) {
				return false;
				}
			}
		}
		return true;
	}

    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;
    }
	
	public static String getDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;
    }
}
