package com.cwits.cyx_drive_sdk.connection;

/**
 * 业务扩展数据请求回调接口。
 * 
 * @author Zorson
 */
public class RequestCallback {
	public static final int REASON_OK = 0;					// 非失败原因
	public static final int REASON_NO_NETWORK = 1;			// 数据请求失败原因：网络开关未开
	public static final int REASON_NO_SIGNAL = 2;			// 数据请求失败原因：无通信网络信号
	public static final int REASON_NOT_AUTHENTICATED = 3;	// 数据请求失败原因：尚未成功登录服务器
	public static final int REASON_TIMEOUT = 4;				// 数据请求失败原因：数据请求超时
	public static final int REASON_DATA_INCRECT = 5;		// 数据请求失败原因：请求数据异常
	public static final int REASON_ERROR = 6;				// 数据请求失败原因：一般性错误，请按具体请求获取具体原因（当前会出此原因的命令：login）

	/**
	 * 业务数据请求成功回调。
	 * 
	 * @param bizJsonData	Json格式的业务返回数据
	 */
	public void onSuccess(String bizJsonData){
	}
	
	/**
	 * 业务数据请求失败回调。
	 * 
	 * @param reason		数据请求失败原因：REASON_*
	 */
	public void onFailed(int reason){
	}
}
