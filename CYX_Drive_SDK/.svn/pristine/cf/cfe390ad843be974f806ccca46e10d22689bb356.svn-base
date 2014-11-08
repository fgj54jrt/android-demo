package com.cwits.cyx_drive_sdk.userInfo;



public interface IExternalInterfaceAR {		
	/**
	 * 用户登录
	 * @param errcode 错误码
	 * @param logintype 登录类型 0：自动登录 1：手动登录
	 * @param errmsg  错误信息
	 */
	public void OnUserLogin(int errcode, int logintype,String errmsg);
	/**
	 * 用户登出
	 * @param errcode 错误码
	 * @param errmsg  错误信息
	 */
	public void OnUserLogout(int errcode, String errmsg);
	/**
	 * 结束驾驶
	 * @param message 要发送的消息内容
	 * @param errmsg  错误信息
	 * @param flag  1:结束驾驶, 0：列表	，表示是哪里调用 ,2:表示要跳到登陆界面
	 */
	public  void OnFinishDriving(String message, String errmsg, int flag);
	/**
	 * 退出应用程序
	 */
	public void OnExitApplication();
	/**
	 * 获取行程奖励标记
	 * @param ids 行程id
	 */
	public void  receiverTripMark(ITripMark obj,String[] ids);
	/**
	 * Log
	 * @param 
	 */
	public void  TraceLn(String msg);
	
	/**
	 * Log
	 * @param 
	 */
	public void  TraceErr(String msg);
	/**
	 * 返回键回调
	 * @param flag 0:从行程记录返回   1：从登陆界面返回 
	 */
	public void  clickOnBack(int flag);
	/**
	 * 检查更新
	 * @param 
	 */
	public void  CheckUpdate();
	
	/**
	 * 用户反馈
	 */
	public void openFeedBack();


	/**邀请好友和分享接口 
	 *@param text 文字
	 *@param image 图片路径（绝对路径）
	 *@param wxUrl 微信图文带链接类型的 链接地址
	 *@param title 标题栏文字
	 *@param wxType 1:纯文本类型 2:图片类型 3:图文带链接类型
	 *@param snsPlatform 分享或邀请的平台 0: 微信好友  1:微信朋友圈 2:新浪 3:短信		  
    */
	public void socialShare(final String text,final String image,final String wxUrl,final String title,final int wxType, final int snsPlatform);

}
