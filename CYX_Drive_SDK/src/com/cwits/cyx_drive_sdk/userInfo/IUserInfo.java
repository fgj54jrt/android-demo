package com.cwits.cyx_drive_sdk.userInfo;


public interface IUserInfo {
	/**
	 * 获取用户ID
	 * @return 用户ID
	 */
	public String getUserID();
	/**
	 * 注册时所在区域
	 * @return 区域信息
	 */
	public String getArea();
	/**
	 * 当前所在区域
	 * @return 区域信息
	 */
	public String getCurrentArea();
	/**
	 * 登录令牌
	 * @return 
	 */
	public String getToken();
	/**
	 * 语言
	 * @return 
	 */
	public String getLanguage();
	/**
	 * 时区
	 * @return 
	 */
	public int getTimeZone();
	/**
	 * 是不是VIP
	 * @return 区域信息
	 */
	public boolean isVIP();
	/**
	 * 是否为临时账号
	 * @return 
	 */
	public boolean isTemp();
	/**
	 * 获取昵称
	 * @return 区域信息
	 */
	public String getNickName();
	/**
	 * 获取手机号码;
	 * @return
	 */
	public String getName();
	/**
	 * 获取密码
	 * @return
	 */
	public String getPassWord();
	
	/**
	 * 获取是否为自动登录账号
	 * @param login 0 不自动登录 1 自动登录
	 * @return
	 */
	public int getAutoLoginFlag();
	
	/**
	 * 保存用户ＩＤ
	 * @param userID
	 */
	public void setUserID(String userID);
	/**
	 * 保存用户名
	 * @param name
	 */
	public void setName(String name);
	/**
	 * 保存昵称
	 * @param nikeName
	 */
	public void setNikeName(String nikeName);
	/**
	 * 保存是否VIP用户
	 * @param isVip
	 */
	public void setIsVip(boolean isVip);
	/**
	 * 保存token
	 * @param token
	 */
	public void setToken(String token);
	/**
	 * 保存时区
	 * @param timeZone
	 */
	public void setTimeZone(int timeZone);
	/**
	 * 保存用户区域
	 * @param area
	 */
	public void setArea(String area);
	/**
	 * 保存用户当前区域
	 * @param area
	 */
	public void setCurrentArea(String area);
	/**
	 * 保存语言
	 * @param language
	 */
	public void setLanguage(String language);
	/**
	 * 保存是否自动登录
	 * @param isAutoLogin 0 不自动登录 1 自动登录
	 */
	public void setAutoLoginFlag(int isAutoLogin);
	
	/**
	 * 保存密码
	 * @param pwd
	 */
	public void setPassword(String pwd);
	
	/**
	 * 保存是否保存密码状态
	 * @param flag
	 */
	public void setSavePwdFlag(int flag);
	
	/**
	 * 获取是否保存秘密
	 * @return
	 */
	public int getSavePwdFlag();
	/**
	 * 正式账号还是临时账号
	 * @return "0"临时账号   "1"正式账号    "2"VIP账号
	 */
	public int getFlag();
	
	/**
	 * 保存用户标志
	 * @param flag
	 */
	public void setFlag(int flag);
	
	/**
	 * 设置注册账号时申请的临时id
	 * @param id
	 */
	public void setTempID(String  id);
	
	/**
	 * 获取 注册账号时申请的临时id
	 * 
	 */
	public String getTempID();
	/**
	 * 查询登录模式  0:临时账号登录  1:正式账号登录  2:没有登录（先前操作－退出登录）
	 */
	public int getLoginMode();
	
	/**
	 * 设置设防状态
	 * @param state
	 */
	public void setFortifyState(int state);
	
	/**
	 * 获得用户设防状态
	 * @param state 0 未设防 1 已设防 -1 未绑定任何设备
	 * @return
	 */
	public int getFortify();
}
