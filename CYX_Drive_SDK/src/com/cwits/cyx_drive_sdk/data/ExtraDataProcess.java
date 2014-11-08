package com.cwits.cyx_drive_sdk.data;

import java.util.Calendar;
import java.util.TimeZone;

import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cwits.cyx_drive_sdk.exception.LogUtil;

/**
 * 发送给平台的数据加工类
 * @author Administrator
 *
 */
public class ExtraDataProcess {
	
	/**
	 * 登陆 
	 * @param username
	 * @param password
	 */
	public JSONObject getLoginData(String type,String id,String account, String password) {
		short messageComm = 0;
		MyJSONObject object =new MyJSONObject(messageComm);
		try {
			object.put("id", account);
			object.put("type", type);
			object.put("account", account);
			object.put("password", password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	} 
	
	/**
	 * 登出
	 * @param androidId
	 * @param type 1:手机 2：OBD
	 * @return
	 */
	public JSONObject getLogoutData (String androidId) {
		short messageComm = 6;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", androidId);
			object.put("type", 1);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	} 
	

	
	/**
	 * 临时账号创建
	 * area  用户所在区域
	 * androidId 手机特征码
	 * @return 
	 */
	public JSONObject getTempAccountData(String androidId,String area) {
		short messageComm = 12302;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("area", area);
			object.put("sign", androidId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	/**
	 * 手机验证
	 * @param id 用户id
	 * @param phonenumber 手机号码
	 * @return 
	 */
	public JSONObject getPhoneVerifyData(int type ,String id,String phonenumber) {
		short messageComm = 12300;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", id);
			object.put("type", type);
			object.put("phone", phonenumber);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	/**
	 * 手机绑定
	 * @param id 用户id
	 * @param phoneNumber 手机号码
	 * @param verificationCode 验证码
	 * @return 
	 */
	public JSONObject getPhoneBindingData(String id,String phoneNumber,String verificationCode) {
		short messageComm = 12290;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", id);
			object.put("phone", phoneNumber);
			object.put("captcha", verificationCode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	/**
	 * 创建密码
	 * @param id 用户id
	 * @param password 密码
	 * @return 
	 */
	public JSONObject getCreatPassword(String id,String password) {
		short messageComm = 12310;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", id);
			object.put("password", password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	/**
	 * 找回密码
	 * @param id 用户id
	 * @param password 密码
	 * @return 
	 */
	public JSONObject getFindPwdData(String id,String password) {
		short messageComm = 12296;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", id);
			object.put("password", password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	/**
	 * 查询附近的交通信息众包
	 * @param lon：经度,精度为5位小数
	 * @param lat：纬度,精度为5位小数
	 * @param level：比例尺级别，附近=0，市级=1
	 */
	public JSONObject getTCSData(double lon, double lat, String level) {
		short messageComm = 8198;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("lon", lon);
			object.put("lat", lat);
			object.put("level",level);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	
	/**
	 *  众包信息评价
	 * @param crowdId
	 * @param agrOpp
	 */
	public JSONObject getAppraiseTCSData(String crowdId, String agrOpp) {
		short messageComm = 8194;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("crowdId", crowdId);
			object.put("agrOpp", agrOpp);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	/**
	 * 众包信息评论
	 * @param crowdId
	 * @param userId
	 * @param commContent
	 */
	public JSONObject getCommTCSData(String crowdId, String userId, String commContent) {
		short messageComm = 8196;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("crowdId", crowdId);
			object.put("userId", userId);
			object.put("commContent", commContent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	
	/**
	 * 众包信息详情
	 * @param crowdId
	 * @param pageSize
	 * @param pageNow
	 */
	public JSONObject getTCSDetailData(String crowdId, int pageSize, int pageNow) {
		short messageComm = 8200;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("crowdId", crowdId);
			object.put("pageSize", pageSize);
			object.put("pageNow", pageNow);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	/**
	 * 淇敼瀵嗙爜
	 * @param newPassword
	 * @param conn
	 * @return true
	 */
	public boolean changePassword (String newPassword) {
		
//		try {
//			//conn.getAccountManager().changePassword(newPassword);
//			return true;
//		} catch (XMPPException e) {
//			e.printStackTrace();
//			return false;
//		}
		return false;
	}
	
	/**
	 * 娉ㄩ攢鐢ㄦ埛
	 * @param conn
	 * @return
	 */
	public boolean deleteAccount () {
//		try {
//			//conn.getAccountManager().deleteAccount();
//			return true;
//		} catch (XMPPException e) {
//			e.printStackTrace();
//			return false;
//		}
		return false;
	}
	
	/**
	 * 涓庢寚瀹氱敤鎴峰垱寤轰竴涓亰澶╄繛鎺ワ紝骞剁洃鍚帴鏀朵俊鎭�
	 * @param userJID
	 * @param iReceiveMessage
	 */
	
	public void createChat (String userJID, final IReceiveMessage iReceiveMessage) {
//		chat = conn.getChatManager().createChat(userJID, new MessageListener() {
//			
//			@Override
//			public JSONObject processMessage(Chat arg0, Message arg1) {
//				iReceiveMessage.receiveTextMessage(arg1);
//			}
//		});
	}
	
	/**
	 * 鍙戦�佷竴鏉℃秷鎭�
	 * @param msg
	 */
	public void sendMessage (String msg) {
//		try {
//
//			Log.d("ouyangxin","error="+msg);
//			//chat.sendMessage(msg);
//		} catch (XMPPException e) {
//			Log.d("ouyangxin","error");
//			e.printStackTrace();
//		}
	}
	/**
	 * 缁欐寚瀹氱敤鎴峰彂閫佷竴鏉℃秷鎭�
	 * @param userJID
	 * @param msg
	 */
	public void sendMessage (String userJID, String msg) {
		Message message = new Message();
		message.setBody(msg);
		message.setTo(userJID);
		message.setType(org.jivesoftware.smack.packet.Message.Type.chat);
//		try {
//			//chat.sendMessage(message);
//		} catch (XMPPException e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * 鎺ユ敹淇℃伅鎺ュ彛
	 * @author Iceshow_xm
	 *
	 */
	public interface IReceiveMessage {
		JSONObject receiveTextMessage (Message message);
	}
	
	
	/**
	 * 自定义JSONObect
	 * @author Administrator
	 *
	 */
	private class MyJSONObject extends JSONObject {
		public MyJSONObject(short messageComm) {
			try {
				this.put("messageVersion", 1);
				this.put("messageComm", messageComm);
				this.put("messageNumber", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).getTimeInMillis()));
				this.put("pacFlag", 60);
				this.put("pac", 60);
				this.put("resWord", 60);
				this.put("conver", 60);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	/**
	 * 交通信息上报
	 * @param userId 用户id
	 * @param type 上报事件类型
	 * @param content 文字内容
	 * @param fileStr 多媒体文件
	 * @param fileType 多媒体文件类型
	 * @param lon 经度
	 * @param lat 纬度
	 * @return 
	 */
	public JSONObject getTrafficReportData(String userId, String type,
			String content, String fileStr, String fileType, double lon,
			double lat) {
		short messageComm = 8192;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("userId", userId);
			object.put("type", type);
			object.put("content", content);
			object.put("fileStr", fileStr);
			object.put("fileType", fileType);
			object.put("lon", lon);
			object.put("lat", lat);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;

	}
	/**
	 * 交通上报历史
	 * @param userId 用户id
	 * @param pageNow 上报事件类型
	 * @param pageSize
	 * @return 
	 */
	public JSONObject getTRHistoryData(String userId, int pageNow,
			int pageSize) {
		short messageComm = 8202;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("userId", userId);
			object.put("pageNow", pageNow);
			object.put("pageSize", pageSize);			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * toAR行程段通知
	 * @param id 用户id
	 * @param data 数据详细
	 *            
	 * @return
	 */
	public JSONObject getNoticeJourneyPartData(String id,JSONObject data) {
		short messageComm = 16384;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", id);
			object.put("data", data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}

	
	/**
	 * toAR行程通知
	 * @param id 用户id
	 * @param data 数据详情
	 * @return
	 */
	public JSONObject getNoticeJourneyAllData(String id,JSONObject data)  {
			LogUtil.printLog(2,"chainwayits","总行程通知-----AR");
		short messageComm = 16385;			// 返回命令字16391
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", id);
			object.put("data", data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * 查询行程列表
	 * @param ter_id 用户ID
	 * @param time   查询时间
	 * @param flag   标志 向下查询或向上查询，0上滑（往后查询） 1 下滑（往前查询）
	 */
	public JSONObject getQueryStrokeData(String ter_id,String time,int flag,int area){
		short messageComm = 4106;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", ter_id);
			object.put("ter_id", ter_id);
			object.put("time", time);
			object.put("flag", flag);
			object.put("size", 10);
			object.put("area", area);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	/**
	 * 查询行程轨迹点详情
	 * @param ter_id 用户ID
	 * @param stroke 行程编号
	 * @param begin	  开始时间	  
	 * @param end	  结束时间
	 * @param page   查询第几页数据
	 */
	public JSONObject getQueryStrokeDetailData(String ter_id,String stroke,
			String begin,String end, int page){
		short messageComm = 4100;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", ter_id);
			object.put("ter_id", ter_id);
			object.put("stroke", stroke);
			object.put("begin", begin);
			object.put("end", end);
			object.put("page", page);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * 上报实时位置
	 * @param ter_id 用户id
	 * @param stroke 行程数据
	 * @param position 位置点
	 */ 
	public JSONObject getUploadPositionData(String ter_id, JSONObject stroke,
			JSONObject position){
		short messageComm = 4096;		// 返回命令字4097
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", ter_id);
			object.put("ter_id", ter_id);
			object.put("stroke", stroke);
			object.put("position", position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	/**
	 * 上报驾驶事件
	 * @param ter_id 用户id
	 * @param stroke 行程编号
	 * @param position 位置点
	 * @param type 事件类型
	 * @param detail 时间数据详情
	 */
	public JSONObject getUploadEventData(String ter_id, String stroke,
			JSONObject position, int type, JSONObject detail){
		short messageComm = 4110;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", ter_id);
			object.put("ter_id", ter_id);			
			object.put("stroke",stroke);
			object.put("position", position);
			object.put("type", type);
			object.put("detail", detail);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	/**
	 * 上报补偿点
	 * @param ter_id 用户id
	 * @param stroke 行程编号
	 * @param positions 补偿点点集
	 */
	public JSONObject getUploadTurnsData(String ter_id, String stroke,
			JSONArray positions){
		short messageComm = 4114;	// 返回命令字 4115
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", ter_id);
			object.put("ter_id", ter_id);			
			object.put("stroke", stroke);
			object.put("positions", positions);			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	/**
	 * 上报行程结束标记
	 * @param ter_id 用户id
	 * @param stroke 行程编号
	 * @param end_flag 结束标记，0未结束，1已结束
	 */
	public JSONObject getUploadJourneyEndFlagData(String ter_id, String stroke,
			String end_flag){
		short messageComm = 4116;     // 返回命令字 4117
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", ter_id);
			object.put("ter_id", ter_id);			
			object.put("stroke", stroke);
			object.put("end_flag", end_flag);			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * 车辆设防
	 * @param ter_id
	 * @param fortifyState  0 取消设防 1 设防
	 * @return
	 */
	public JSONObject getCarFortifyData(String ter_id,String fortifyState){
		short messageComm = 18;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", ter_id);
			object.put("userId", ter_id);			
			object.put("fortifyState", fortifyState);			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	/**
	 * 车辆定位
	 * @param ter_id
	 * @return
	 */
	public JSONObject getLocation(String ter_id) {
		short messageComm = 4098;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("ter_id", ter_id);				
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	/**
	 * 绑定盒子
	 * @param id
	 * @param pn
	 * @param sn
	 * @return
	 */
	public JSONObject bindingCXB(String id, String pn, String sn) {
		short messageComm = 12318;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", id);		
			object.put("pn", pn);		
			object.put("sn", sn);		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	/**
	 * 车辆解绑
	 * @param id
	 * @param phone
	 * @param captcha
	 * @return
	 */
	public JSONObject unbindingCXB(String id, String phone, String captcha) {
		short messageComm = 12320;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", id);		
			object.put("phone", phone);		
			object.put("captcha", captcha);		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	/**
	 * 我的汽车（包含设防状态）
	 * @param id 用户id
	 * @return
	 */
	public JSONObject getMyCarData(String id){
		short messageComm = 16;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", id);
			object.put("phoneType", 1);
			object.put("token","");
			object.put("appVersion", "v1.1");
			object.put("cerName", "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	/**
	 * 收到报警消息的应答
	 * @param userId 用户ID
	 * @param result 结果码 1 成功 0失败
	 * @return
	 */
	public JSONObject getAlarmResponse(String userId,String result){
		short messageComm = 16393;
		MyJSONObject object = new MyJSONObject(messageComm);
		try {
			object.put("id", userId);
			object.put("result", result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
}


