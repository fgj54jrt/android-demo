package com.cwits.cyx_drive_sdk.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 获得设置的参数工具类
 * @author Administrator
 *
 */
public class SettingParameterUtil {
	private static final String SP_NAME = "com.cwits.cyx_drive_test_settings";
	public static final String PATH_PLAN_CONDITION = "PATH_PLAN_CONDITION";	 //路径规划条件
	public static final String NAVI_REPORT = "NAVI_REPORT";	 //导航播报
	
	/**
	 * 获得某个参数
	 * @param context, 
	 * @param key : 需要获得的参数key
	 * @param defaultVaule : 默认值
	 * @return 
	 */
	public static int getSettingParam(Context context, String key, int defaultVaule) {
		SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		return preferences.getInt(key, defaultVaule);
	}
	
	/**
	 * 设置某个参数的值
	 * @param context
	 * @param key
	 * @param value
	 */
	public static boolean setSettingParam(Context context, String key, int value) {
		SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, value);
		return editor.commit();
	}
}
