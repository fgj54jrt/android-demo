package com.cwits.cyx_drive_sdk.integrate;

import android.content.Context;

/**
 * 程序相关配置
 * @author lxh
 *
 */
public interface IConfiguration {
	/**
	 * 初始化配置
	 * @param context
	 */
	void InitConfig(Context context);

	/**
	 * 设置服务器IP地址
	 * 
	 * @param serverIP	服务器IP地址
	 * @return			成功设置并保存服务器IP则返回true，否则返回false
	 */
	boolean setServerIP(String serverIP);
	
	String getServerIP();
	/**
	 * 获得服务器端口
	 * 
	 * @return		服务器端口
	 */
	int getServerPort();
	
	/**
	 * 设置服务器端口
	 * 
	 * @param serverPort	服务器端口
	 * @return			成功设置并保存服务器端口则返回true，否则返回false
	 */
	boolean setServerPort(int serverPort);
	/**
	 * 获得测试服务器IP地址
	 * 
	 * @return		服务器IP地址
	 */
	String getTestServerIP();
	
	/**
	 * 设置测试服务器IP地址
	 * 
	 * @param serverIP	服务器IP地址
	 * @return			成功设置并保存服务器IP则返回true，否则返回false
	 */
	boolean setTestServerIP(String serverIP);
	
	/**
	 * 获得测试服务器端口
	 * 
	 * @return		服务器端口
	 */
	int getTestServerPort();
	
	/**
	 * 设置测试服务器端口
	 * 
	 * @param serverPort	服务器端口
	 * @return			成功设置并保存服务器端口则返回true，否则返回false
	 */
	boolean setTestServerPort(int serverPort);
	
	
	/**
	 * 获取服务器
	 * @return true为测试服务器，false为正式服务器，默认为false
	 */
	boolean isTestServer();
	
	/**
	 * 设置服务器
	 * @param serverTag 服务器类型，true为测试服务器，false为正式服务器
	 */
	boolean setTestServer(boolean serverTag);
	
	/**
	 * 读取应用程序版本码
	 * 
	 * @return		应用程序版本码
	 */
	int getAppVersionCode();
	
	/**
	 * 读取应用程序版本号字符串
	 * 
	 * @return		应用程序版本号字符串
	 */
	String getAppVersionName();
	
	/**
	 * 程序数据存储相对路径
	 * @return
	 */
	public String getConfigDir();
}
