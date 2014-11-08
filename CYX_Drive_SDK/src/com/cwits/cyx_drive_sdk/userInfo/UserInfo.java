package com.cwits.cyx_drive_sdk.userInfo;

import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo implements IUserInfo {
	
	public SharedPreferences preferences;

	public UserInfo(Context context,String userName){
		if(context!=null)
			preferences = context.getApplicationContext().getSharedPreferences(userName,Context.MODE_PRIVATE);
		
	}
	
	@Override
	public String getUserID() {
		// TODO Auto-generated method stub
		String UserID = "";
		if(preferences!=null)
			UserID = preferences.getString(Constant.USER_ID, "NULL");
		return UserID;
	}

	@Override
	public String getArea() {
		// TODO Auto-generated method stub
		return preferences.getString(Constant.AREA, "30");
	}

	@Override
	public String getCurrentArea() {
		// TODO Auto-generated method stub
		String CurrentArea=preferences.getString(Constant.ADDRESS_PROVINCE, "");
		return CurrentArea;
	}

	@Override
	public String getToken() {
		// TODO Auto-generated method stub
		String Token=preferences.getString(Constant.TOKEN, "");
		return Token;
	}

	@Override
	public String getLanguage() {
		// TODO Auto-generated method stub
		String Language=preferences.getString(Constant.LANGUAGE, "cn_ZH");
		return Language;
	}

	@Override
	public int getTimeZone() {
		// TODO Auto-generated method stub
		int TimeZone=preferences.getInt(Constant.TIME_ZONE, 8);
		return TimeZone;
	}

	@Override
	public boolean isVIP() {
		// TODO Auto-generated method stub
		boolean isVIP=preferences.getBoolean(Constant.IS_VIP, false);
		return isVIP;	
	}

	@Override
	public boolean isTemp() {
		// TODO Auto-generated method stub
		boolean isTemp=preferences.getBoolean(Constant.IS_TEMP, false);
		return isTemp;		
	}

	@Override
	public String getNickName() {
		// TODO Auto-generated method stub
		String NickName=preferences.getString(Constant.NICK_NAME, "USER");
		return NickName;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = preferences.getString(Constant.NAME, "");
		return name;
	}
	@Override
	public String getPassWord() {
		// TODO Auto-generated method stub
		String passwd = preferences.getString(Constant.PASSWORD, "");
		return passwd;
	}
	
	@Override
	public int getFlag() {
		return  preferences.getInt(Constant.FLAG, 1);
	}

	@Override
	public int getAutoLoginFlag() {
		// TODO Auto-generated method stub
		return preferences.getInt(Constant.IS_AUTOLOGIN, 0);
	}

	@Override
	public void setUserID(String userID) {
		// TODO Auto-generated method stub
		preferences.edit().putString(Constant.USER_ID, userID).commit();
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		preferences.edit().putString(Constant.NAME, name).commit();
	}

	@Override
	public void setNikeName(String nikeName) {
		// TODO Auto-generated method stub
		preferences.edit().putString(Constant.NICK_NAME, nikeName).commit();
	}

	@Override
	public void setIsVip(boolean isVip) {
		// TODO Auto-generated method stub
		preferences.edit().putBoolean(Constant.IS_VIP, isVip).commit();
	}

	@Override
	public void setToken(String token) {
		// TODO Auto-generated method stub
		preferences.edit().putString(Constant.TOKEN, token).commit();
	}

	@Override
	public void setTimeZone(int timeZone) {
		// TODO Auto-generated method stub
		preferences.edit().putInt(Constant.TIME_ZONE, timeZone).commit();
	}

	@Override
	public void setArea(String area) {
		// TODO Auto-generated method stub
		preferences.edit().putString(Constant.AREA, area).commit();
	}

	@Override
	public void setCurrentArea(String area) {
		// TODO Auto-generated method stub
		preferences.edit().putString(Constant.CURRENT_AREA, area).commit();
	}

	@Override
	public void setLanguage(String language) {
		// TODO Auto-generated method stub
		preferences.edit().putString(Constant.LANGUAGE, language).commit();
	}

	@Override
	public void setAutoLoginFlag(int isAutoLogin) {
		// TODO Auto-generated method stub
		preferences.edit().putInt(Constant.IS_AUTOLOGIN, isAutoLogin).commit();
	}

	@Override
	public void setPassword(String pwd) {
		// TODO Auto-generated method stub
		preferences.edit().putString(Constant.PASSWORD, pwd).commit();
	}

	@Override
	public void setSavePwdFlag(int flag) {
		// TODO Auto-generated method stub
		preferences.edit().putInt(Constant.IS_SAVEPASSWORD, flag).commit();
	}

	@Override
	public int getSavePwdFlag() {
		// TODO Auto-generated method stub
		return preferences.getInt(Constant.IS_SAVEPASSWORD, 0);
	}

	@Override
	public void setFlag(int flag) {
		// TODO Auto-generated method stub
		preferences.edit().putInt(Constant.FLAG, flag).commit();
	}

	@Override
	public void setTempID(String id) {
		// TODO Auto-generated method stub
		preferences.edit().putString(Constant.TEMP_ID, id).commit();
		
	}

	@Override
	public String getTempID() {
		return preferences.getString(Constant.TEMP_ID, "");
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLoginMode() {
		// TODO Auto-generated method stub
		if(preferences!=null){
		if(getUserID().equals("")||getUserID().equals("NULL")){
			return 0;
		}
		if(((!getUserID().equals(""))||(!getUserID().equals("NULL")))&&(!getPassWord().equals(""))){
			if(getFlag()==0){
				return 0;
			}else{
			return 1;
			}
			
		}
		}
		return 2;
	}

	@Override
	public void setFortifyState(int state) {
		
		if(preferences!=null)
			preferences.edit().putInt(Constant.FORTIFY_STATE, state).commit();
		
	}

	@Override
	public int getFortify() {
		// TODO Auto-generated method stub
		return preferences.getInt(Constant.FORTIFY_STATE, -1);
	}

}
