package com.cwits.cyx_drive_sdk.userInfo;

import android.content.Context;

/**
 * 用户信息管理类
 * @author lxh
 *
 */
public interface IUserManager {
	
	public static int TEMP_ACCOUNT = 0;
	
	
	/**
	 * 初始化用户相关
	 * @param context
	 */
	public void initUserInfo(Context context);

	/**
	 * 获取所有登录过的用户名
	 * @return
	 */
	public String[] getAllUserName();
	
    /**
     * 当前登录用户的用户名
     * @return
     */
	public String getCurrentUserName();
	
	/**
	 * 添加一个用户
	 * @param name
	 * @param loginFlag 0不自动登录 1 自动登录
	 * @param isSavePassword 密码
	 * @param userFlag 0 临时用户  1 正式用户 2 VIP 用户
	 */
	public boolean addUser(String name , int loginFlag,String password,int userFlag);
	
	/**
	 * 设置当前用户
	 * @param name
	 */
	public boolean setDefaultUser(String name);
	
	/**
	 * 设置自动登录
	 * @param name
	 * @param login 0不自动登录 1 自动登录
	 */
	public boolean setAutoLogin(String name, int login);
	
	/**
	 * 设置用户类型 0 临时用户  1 正式用户 2 VIP 用户
	 * @param name
	 * @return
	 */
	public boolean setUserFlag(String name, int flag);
	
	/**
	 * 设置是否有密码
	 * @param name
	 * @param password
	 */
	public boolean setSavePassWord(String name,String password);
	
	/**
	 * 创建用户文件夹
	 * @param username
	 */
	public void createAllFilesForDirector(String username);
}
