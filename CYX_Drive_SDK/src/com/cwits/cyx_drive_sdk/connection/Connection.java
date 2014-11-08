package com.cwits.cyx_drive_sdk.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.cwits.cyx_drive_sdk.data.PersistentStorageProvider;
import com.cwits.cyx_drive_sdk.exception.LogUtil;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;

/**
 * 与服务器连接逻辑实现
 * 
 * @author Zorson
 */
public class Connection implements ConnectionListener, IQProvider, PacketListener {
	public static final int CONN_STATE_INIT 				= 0; // 连接初始
	public static final int CONN_STATE_CONNECTING			= 1; // 正在连接服务器（TCP数据链路阶段）
	public static final int CONN_STATE_CONNECTED			= 2; // 已连接到服务器（TCP数据链路阶段）
	public static final int CONN_STATE_LOGINNING			= 3; // 正在登录服务器（业务接入阶段）
	public static final int CONN_STATE_LOGIN_OK				= 4; // 成功登录服务器（业务接入阶段）
	public static final int CONN_STATE_LOGIN_ERR			= 5; // 登录失败：如用户名或密码错，及其它原因
	public static final int CONN_STATE_KICKOUT				= 6; // 用户被系统下线，由于在别处登录
	public static final int CONN_STATE_NETWORK_ERR			= 7; // 网络或通信信号异常（稍后会自动重连）
	public static final int CONN_STATE_LOGOUTING			= 8; // 正在登出服务器
	public static final int CONN_STATE_STOPPED				= 9; // 连接已停止

	public static final int REASON_AUTH_SUCCESS = 0;			// 登录成功
	public static final int REASON_AUTH_USER_NON_EXIST = -1;	// 登录失败原因：用户不存在
	public static final int REASON_AUTH_USER_PASS_ERR = -2;		// 登录失败原因：用户名或密码错误
	public static final int REASON_AUTH_UNKONW = -3;			// 登录失败原因：登录失败（不明原因统一为此）
	public static final int REASON_AUTH_TIMEOUT = -4;			// 登录失败原因：登录超时

	public static final int START_TYPE_DEFAULT				= 0; // 默认启动类型，连接后自动登录
	public static final int START_TYPE_CONNECT_ONLY			= 1; // 仅自动连接启动类型，启动后仅自动维护TCP数据链路
	
	/* The static variables */
	private static Integer mSyncObj = new Integer(0);
	private static Connection mConnection;
	private static final String FORWARD_TAG_NAME = "forward";
	private static final String FORWARD_TAG_NAMESPACE = "com:cwits:cyxmobilegate";
	private static final String VCARD_TAG_NAME = "vCard";
	private static final String VCARD_TAG_NAMESPACE = "vcard-temp";
	private static final String VCARD_EXTRA_DATA = "X-DATA";
	private static final String VCARD_EXTRA_USER_TYPE = "USER_TYPE";
	private static final String KEY_FOR_LOGIN = "keyForLogin";
	private static final String PERSISTENT_DATA_SEND_COUNT_KEY = "persistentDataSendCount";
	private static final String PERSISTENT_DATA_SEND_TIME_KEY = "persistentDataSendTime";
	private static final String PERSISTENT_DATA_KEY = "persistentData";
	
	/* The variables used mainly for StateWorker */
	private StateWorker mStateWorker;
	private int mStartType = START_TYPE_DEFAULT;
	private int mSendingTimeout = 45*1000;						// 数据发送超时时间
	private int mLoginErrReason = REASON_AUTH_UNKONW;
	private Integer mConnectionState = new Integer(CONN_STATE_INIT);
	private ArrayList<IConnStateListener> mConnStateListeners = new ArrayList<IConnStateListener>();
	private long mLastConnectTime = 0;							// 最后一次连接服务器时间
	private long mLastLoginTime = 0;							// 最后一次登录服务器时间
	private long mLastNetworkErrTime = 0;						// 最后一次网络连接异常时间
	private int mTryTimesPersistentData = 5;					// 在删除待发送数据前尝试发送重要数据的次数（为了防止堵塞）
	private ArrayList<String> mPersistentDataCache = new ArrayList<String>();	// 用于缓存中转待发送的重要数据
	private boolean mIsSendingPersistentData = false;			// 指示是否正在发送重要数据的标记
	private String mTheIQidOfPerData = "";						// 用于判定重要数据发送应答的id值
	
	/* The variables for common use */
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private XMPPConnection mXMPPConnection = null;
	private PingManager mPingManager = null;
	private HashMap<String,SendingData> mSendingDataSet = new HashMap<String,SendingData>();
	private String mServerAddr = "";							// 服务器地址
	private int mServerPort = 5222;								// 服务器端口
	private String mUsername = "";								// 用户登录账号
	private String mPassword = "";								// 用户登录密码
	/**
	 * 获得连接对象
	 * 
	 * @return		连接对象实例
	 */
	public static Connection getInstance(){
		synchronized (mSyncObj) {
			if(null == mConnection){
				mConnection = new Connection();
				ProviderManager.addIQProvider(FORWARD_TAG_NAME, FORWARD_TAG_NAMESPACE, mConnection);
			}
		}
		return mConnection;
	}
	
	/**
	 * 设置服务器地址及端口
	 * 
	 * @param serverAddr	服务器地址
	 * @param port			服务器端口
	 */
	public void setServerAddrPort(String serverAddr, int port){
		if(null != serverAddr)
			mServerAddr = serverAddr;
		
		if(port > 0 && port < 65535)
			mServerPort = port;
	}
	
	/**
	 * 设置用户账号及密码
	 * 
	 * @param username		用户账号名
	 * @param password		用户账号密码
	 */
	public void setUsernamePasswd(String username, String password){
		if(null != username)
			mUsername = username;
		
		if(null != password)
			mPassword = password;
	}
	
	/**
	 * 设置启动类型，需在start前设置才可生效
	 * 
	 * @param startType		启动类型: START_TYPE_*
	 */
	public void setStartType(int startType){
		mStartType = startType;
	}
	
	/**
	 * 启动连接模块
	 * 
	 * @return
	 */
	public boolean start(){
		// If it already started, then return true
		boolean isRunning = false;
		synchronized (mSyncObj) {
			isRunning = null != mStateWorker && mStateWorker.mRunning && mStateWorker.isAlive();
		}
		if(isRunning)
			return true;
		
		// Should trigger start event when the state is on CONN_STATE_INIT && CONN_STATE_STOPPED
		if(CONN_STATE_INIT != getConnectionState() && CONN_STATE_STOPPED != getConnectionState())
			return false;
		
		debugLog("Start the connection.");
		initialize();
		
		setConnectionState(CONN_STATE_INIT);
		
		// Create a new worker, and start it
		mStateWorker = new StateWorker();
		mStateWorker.mRunning = true;
		mStateWorker.start();

		return true;
	}
	
	/**
	 * 停止连接模块
	 * 
	 * @return
	 */
	public boolean stop(){
		return eventStop();
	}
	
	/**
	 * 用户账号登录，如账号密码正确且成功登录，则连接对象会自动保持用户的账号随时自动登录。
	 * 登录的结果通过回调通知，同时连接监听者接口的事件也会触发。
	 * 
	 * @param username		用户账号
	 * @param password		密码
	 * @param callback		结果回调
	 */
	public void login(String username, String password,RequestCallback callback){
		// If parameter incorrect
		if (null == username || (null != username && username.length() <= 0)
				|| null == password
				|| (null != password && password.length() <= 0)) {
			if (null != callback) {
				RequestCallbackRunnable r = new RequestCallbackRunnable(
						callback, false,
						RequestCallback.REASON_DATA_INCRECT, null);
				mHandler.post(r);
			}
			return;
		}

		// Save the persistent data cache
		doSavePersistentCache();

		// Save the parameter
		mUsername = username;
		mPassword = password;
		// Stop first
		stop();
		start();
		
		// Create a sending data, this is not a real sending request
		SendingData sendingData = new SendingData();
		sendingData.jsonExtData = null;
		sendingData.requestCallback = callback;
		sendingData.lastSendingTime = new Long(0);
		sendingData.theIQ = null;
		sendingData.key = KEY_FOR_LOGIN;
		
		synchronized (mSendingDataSet) {
			mSendingDataSet.put(sendingData.key, sendingData);
		}
		
		// Start a new connection to server
		eventConnecting();
	}
	
	/**
	 * 用户账号退出登录。
	 * 
	 * @param callback		结果回调
	 */
	public void logout(RequestCallback callback){
		
	}
	
	/**
	 * 读取用户是否成功登录手机网关服务器状态
	 * 
	 * @return				true：如果已经成为登录，false：如果未成功登录
	 */
	public boolean isAuthenticated(){
		if(null != mXMPPConnection && mXMPPConnection.isAuthenticated()){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 扩展数据发送接口。主要用于扩展IQ节。其参数无需IQ节本身，仅需要扩展的JSON数据对象。
	 * 
	 * @param jsonObj		扩展的JSON数据对象
	 * @param callback		结果回调
	 * 
	 * @return				如果发送请求被接受则返回发送请求的任务ID，否则返回null
	 */
	public String sendExtData(JSONObject jsonObj, RequestCallback callback) {
		// Construct IQ object, then get the packet ID for key
		cyx_IQquery iq = null;
		String key = null;
		if(null != jsonObj){
			iq = new cyx_IQquery();
			iq.setType(IQ.Type.GET);
			iq.setJsonobj(jsonObj.toString());

			key = iq.getPacketID();
		}
		
		// If jsonObj is null then trigger a failed callback
		if (null == jsonObj || null == key) {
			if (null != callback) {
				RequestCallbackRunnable r = new RequestCallbackRunnable(
						callback, false, RequestCallback.REASON_DATA_INCRECT,
						null);
				mHandler.post(r);
			}

			return null;
		}
		
		// If do not login to server then trigger a failed callback
		if(CONN_STATE_LOGIN_OK != getConnectionState()
				&& CONN_STATE_LOGINNING != getConnectionState()
				&& CONN_STATE_CONNECTED != getConnectionState()
				&& CONN_STATE_CONNECTING != getConnectionState()
				&& CONN_STATE_NETWORK_ERR != getConnectionState()){
			if (null != callback) {
				RequestCallbackRunnable r = new RequestCallbackRunnable(
						callback, false, RequestCallback.REASON_NOT_AUTHENTICATED,
						null);
				mHandler.post(r);
			}

			return null;
		}
		
		// If the state is CONN_STATE_NETWORK_ERR, then let it quickly reconnect
		if(CONN_STATE_NETWORK_ERR == getConnectionState()){
			mLastNetworkErrTime = 0;
		}
		
		// Create a sending data to sending queue
		SendingData sendingData = new SendingData();
		sendingData.jsonExtData = jsonObj;
		sendingData.requestCallback = callback;
		sendingData.lastSendingTime = new Long(0);
		sendingData.theIQ = iq;
		sendingData.key = key;
		
		synchronized (mSendingDataSet) {
			mSendingDataSet.put(key, sendingData);
		}
		
		// Notify the worker to send
		synchronized (mStateWorker.mWakeupEvent) {
			try{
				mStateWorker.mWakeupEvent.notifyAll();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		return key;
	}
	
	private void checkAndSendExtData(){
		long currTime = new Date().getTime();
		
		synchronized (mSendingDataSet) {
			// If there are data then process the sending data set
			if(mSendingDataSet.size() > 0){
				ArrayList<String> keys = new ArrayList<String>();
				
				for(SendingData data : mSendingDataSet.values()){
					if(0 == data.lastSendingTime && null != data.theIQ){
						// Sending the data
						try {
//							debugLog(">>> Sending:" + data.theIQ.toXML());
							LogUtil.printLog(LogUtil.DEBUG, "cyx_drive_sdk ", "sending : " + data.theIQ.toXML().toString());
							mXMPPConnection.sendPacket(data.theIQ);
							data.lastSendingTime = new Date().getTime();
						} catch (NotConnectedException e) {
							e.printStackTrace();
						}
					}else if(currTime - data.lastSendingTime > mSendingTimeout){
						// Timeout for request
						if (null != data.requestCallback) {
							RequestCallbackRunnable r = new RequestCallbackRunnable(
									data.requestCallback, false,
									RequestCallback.REASON_TIMEOUT, null);
							mHandler.post(r);
						}
						
						// Save the key of timeout data 
						keys.add(data.key);
					}
				}
				
				// Delete timeout data
				for(String key : keys){
					mSendingDataSet.remove(key);
				}
			}
		}
	}
	
	/**
	 * 取消发送业务扩展数据的请求任务
	 * 
	 * @param requestTaskID	发送请求的任务ID
	 */
	public void cancelSendExtData(String requestTaskID){
		synchronized (mSendingDataSet) {
			if(null != requestTaskID)
				mSendingDataSet.remove(requestTaskID);
		}
	}
	
	/**
	 * 取消所有发送业务扩展数据的请求任务
	 */
	private void cancelAllSendExtData(){
		synchronized (mSendingDataSet) {
			mSendingDataSet.clear();
		}
	}
	
	/**
	 * 发送重要数据（目前主要是驾驶相关数据：定时位置点、补偿位置点、驾驶行为事件、行程数据等）。
	 * 此方法会将数据排队存入持久化存储器里，以便保证数据不丢失，同时在适当的时候再将数据发到系统。
	 * ！注意：此处机制会尽可能保证数据发送到系统，如不是非重要的数据（尤其是有界面操作产生的数据）不要
	 * 使用此方法发送，应该使用sendExtData方法发送并及时给反馈。因为发送是按顺序发送同时一个数据发成功
	 * 才发下一数据的，为了防止个别异常数据永远发不成功而堵塞发送队列，此处机制还是加了个发送尝试次数的
	 * 限制（由变量：mTryTimesPersistentData控制）。
	 * 
	 * @param jsonObj		待发送的数据（应为扩展的业务JSON数据格式）
	 */
	public void sendPersistentData(JSONObject jsonObj){
		boolean isOnlyTestPersistentData = true;	// 当为false时数据每次存入持久数据中再取出来发送，否则正常逻辑
		
		if(null == jsonObj)
			return;
		
		
		// Create the wrapper data
		JSONObject theData = new JSONObject();
		try {
			theData.put(PERSISTENT_DATA_KEY, jsonObj);
			theData.put(PERSISTENT_DATA_SEND_COUNT_KEY, 0);
			theData.put(PERSISTENT_DATA_SEND_TIME_KEY, new Date().getTime());
			
			// Storage the sending data
			IPersistentStorage perSto = PersistentStorageProvider.getPersistentStorage(mUsername);
			if((null != perSto && perSto.getCount() > 0) || isOnlyTestPersistentData){
				// If there are data in the persistent storage, then put new data to the tail
				perSto.addTail(theData.toString());
			}else{
				// If there is no data in the persistent storage, then add it to the cache
				mPersistentDataCache.add(theData.toString());
			}
			
			// Notify the worker
			synchronized (mStateWorker.mWakeupEvent) {
				try{
					mStateWorker.mWakeupEvent.notifyAll();
				}catch(Exception e){
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void checkAndSendPersistentData() {
		boolean isShouldDeleteHeader = false;
		boolean isShouldUpdateHeader = false;
		String theDataString = null;
		JSONObject theData = null;
		long currTime = new Date().getTime();

		synchronized (mPersistentDataCache) {
			IPersistentStorage perSto = PersistentStorageProvider
					.getPersistentStorage(mUsername);

			// Get the data string
			theDataString = doPreProcessSendPerSto(theDataString, perSto);

			// Deal with the data string and send the data
			if ( null != theDataString ) {
				try {
					theData = new JSONObject(theDataString);
					int sentCount = theData
							.getInt(PERSISTENT_DATA_SEND_COUNT_KEY);
					long sentTime = theData
							.getLong(PERSISTENT_DATA_SEND_TIME_KEY);

					if (sentCount >= mTryTimesPersistentData) {
						// If too many times try to send the data, then delete
						// it
						isShouldDeleteHeader = true;
					} else {
						if (!mIsSendingPersistentData) {
							// Then send the data
							cyx_IQquery iq = null;
							JSONObject jsonObj = theData
									.getJSONObject(PERSISTENT_DATA_KEY);

							if (null != jsonObj) {
								iq = new cyx_IQquery();
								iq.setType(IQ.Type.GET);
								iq.setJsonobj(jsonObj.toString());

								try {
//									debugLog(">>> Sending_Important:" + iq.toXML());
									LogUtil.printLog(LogUtil.DEBUG, "cyx_drive_sdk ", "Sending_Important: " + iq.toXML().toString());
									mXMPPConnection.sendPacket(iq);
                                    mTheIQidOfPerData = iq.getPacketID();
									sentCount++;
									theData.put(PERSISTENT_DATA_SEND_COUNT_KEY,
											sentCount);
									theData.put(PERSISTENT_DATA_SEND_TIME_KEY,
											currTime);

									isShouldUpdateHeader = true;
									mIsSendingPersistentData = true;
									mTheIQidOfPerData = iq.getPacketID();
								} catch (NotConnectedException e) {
									e.printStackTrace();
									mIsSendingPersistentData = false;
								}
							} else {
								isShouldDeleteHeader = false;
							}
						} else {
							// If send timeout
							if(currTime - sentTime > mSendingTimeout){
								mIsSendingPersistentData = false;
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					isShouldDeleteHeader = true;
				}
			}

			// Do post processing
			doPostProcessSendPerSto(isShouldDeleteHeader, isShouldUpdateHeader,
					theData, perSto);
		}

	}

	/**
	 * @param theDataString
	 * @param perSto
	 * @return
	 */
	private String doPreProcessSendPerSto(String theDataString,
			IPersistentStorage perSto) {
		try {
			if (mPersistentDataCache.size() > 0) {
				theDataString = mPersistentDataCache.get(0);
			} else if (null != perSto && perSto.getCount() > 0) {
				theDataString = PersistentStorageProvider.getPersistentStorage(
						mUsername).getHeader();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return theDataString;
	}

	/**
	 * @param isShouldDeleteHeader
	 * @param isShouldUpdateHeader
	 * @param theData
	 * @param perSto
	 */
	private void doPostProcessSendPerSto(boolean isShouldDeleteHeader,
			boolean isShouldUpdateHeader, JSONObject theData,
			IPersistentStorage perSto) {
		// Update the header
		doUpdateHeaderPersistentData(isShouldDeleteHeader,
				isShouldUpdateHeader, theData, perSto);
		
		// If there is any persistent data, then notify the worker
		if((mPersistentDataCache.size() > 0) || (null != perSto && perSto.getCount() > 0)){
			synchronized (mStateWorker.mWakeupEvent) {
				try{
					mStateWorker.mWakeupEvent.notifyAll();
				}catch(Exception e){}
			}
		}
	}

	/**
	 * @param isShouldDeleteHeader
	 * @param isShouldUpdateHeader
	 * @param theData
	 * @param perSto
	 */
	private void doUpdateHeaderPersistentData(boolean isShouldDeleteHeader,
			boolean isShouldUpdateHeader, JSONObject theData,
			IPersistentStorage perSto) {
		try{
			if(mPersistentDataCache.size() > 0){
				if(isShouldUpdateHeader && null != theData){
					mPersistentDataCache.set(0, theData.toString());
				}
				if(isShouldDeleteHeader){
					mPersistentDataCache.remove(0);
				}
			}else if(null != perSto && perSto.getCount() > 0){
				if(isShouldUpdateHeader && null != theData){
					perSto.modifyHeader(theData.toString());
				}
				if(isShouldDeleteHeader){
					perSto.deleteHeader();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 处理发送的重要数据的应答
	 * 
	 * @param thePacketID
	 */
	private void doReceivedPersistentData(String thePacketID){
		
		if(null != thePacketID && 0 == mTheIQidOfPerData.compareToIgnoreCase(thePacketID)){
			// Remove the header of saved persistent data
			synchronized (mPersistentDataCache) {
				IPersistentStorage perSto = PersistentStorageProvider.getPersistentStorage(mUsername);
				if(mPersistentDataCache.size() > 0){
					mPersistentDataCache.remove(0);
				}else if(null != perSto && perSto.getCount() > 0){
					perSto.deleteHeader();
				}
				
				// If there is any persistent data, then notify the worker
				if((mPersistentDataCache.size() > 0) || (null != perSto && perSto.getCount() > 0)){
					synchronized (mStateWorker.mWakeupEvent) {
						try{
							mStateWorker.mWakeupEvent.notifyAll();
						}catch(Exception e){}
					}
				}
			}
			
			mTheIQidOfPerData = "";
			mIsSendingPersistentData = false;
		}
		
	}
	
	/**
	 * 保存缓存中的重要数据到持久化存储中
	 */
	private void doSavePersistentCache(){
		synchronized (mPersistentDataCache) {
			// If there is any data in the cache, then save it to the persistent storage
			if(mPersistentDataCache.size() > 0){
				IPersistentStorage perSto = PersistentStorageProvider.getPersistentStorage(mUsername);
				
				//  Save data to the persistent storage
				for (int i = 0; i < mPersistentDataCache.size(); ++i) {
					if (null != perSto) {
						perSto.addTail(mPersistentDataCache.get(i));
					}
				}
				
				mPersistentDataCache.clear();
			}
		}
	}
	
	/**
	 * 获得登录错误原因
	 * 
	 * @return		登录错误原因：REASON_AUTH_* (当原因不定义值时其内容为系统返回的结果码）
	 */
	public int getLoginErrReason(){
		return mLoginErrReason;
	}
	
	/**
	 * 增加连接状态监听者
	 * 
	 * @param listener		连接状态监听者
	 */
	public void addConnStateListener(IConnStateListener listener){
		if(mConnStateListeners!=null)
			mConnStateListeners.add(listener);
	}
	
	/**
	 * 移除连接状态监听者
	 * 
	 * @param listener		连接状态监听者
	 */
	public void removeConnStateListener(IConnStateListener listener){
		if(mConnStateListeners!=null)
			mConnStateListeners.remove(listener);
	}
	
	/**
	 * 获得连接当前连接状态
	 * 
	 * @return		当前连接状态：CONN_STATE_*
	 */
	public int getConnectionState(){
		return mConnectionState;
	}
	
	/** 
	 * 获得数据发送超时时间（单位为毫秒）
	 * 
	 * @return		数据发送超时时间（单位为毫秒）
	 */
	public int getSendingTimeout(){
		return mSendingTimeout;
	}
	
	/**
	 * Use to others for notifying of network OK to this connection object
	 */
	public void onNetworkOK(){
		if(null != mXMPPConnection && CONN_STATE_CONNECTING != getConnectionState()){
			eventNetworkErr();
			mLastNetworkErrTime = 0;
			
			synchronized (mStateWorker.mWakeupEvent) {
				try{
					mStateWorker.mWakeupEvent.notifyAll();
				}catch(Exception e){
				}
			}
		}
	}
	
	/**
	 * 业务扩展数据请求结果回调Runnable（从此可将回调事件代码执行放到主线程中）
	 */
	private class RequestCallbackRunnable implements Runnable{
		private RequestCallback requestCallback;
		private boolean isDataResponsed;
		private int nonResponseReason;
		private JSONObject jsonExtResponseData;
		
		public RequestCallbackRunnable(RequestCallback requestCallback,
				boolean isDataResponsed, int nonResponseReason,
				JSONObject jsonExtResponseData) {
			this.requestCallback = requestCallback;
			this.isDataResponsed = isDataResponsed;
			this.nonResponseReason = nonResponseReason;
			this.jsonExtResponseData = jsonExtResponseData;
		}
		
		@Override
		public void run(){
			if (null != requestCallback) {
				try {
					if (isDataResponsed) {
						requestCallback.onSuccess(jsonExtResponseData
								.toString());
					} else {
						requestCallback.onFailed(nonResponseReason);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 连接状态改变通知runnable（从此可将回调事件代码执行放到主线程中）
	 */
	private class StateChangeRunnable implements Runnable{
		private int oldState;
		private int newState;
		private IConnStateListener listener;
		
		public StateChangeRunnable(IConnStateListener listener, int oldState, int newState){
			this.listener = listener;
			this.oldState = oldState;
			this.newState = newState;
		}
		
		@Override
		public void run() {
			try{
				listener.onConnStateChanged(oldState, newState);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public String getConncectStateString(int state){
		switch(state){
		case CONN_STATE_INIT: 			return "CONN_STATE_INIT"; 		// 连接初始
		case CONN_STATE_CONNECTING:		return "CONN_STATE_CONNECTING"; // 正在连接服务器（TCP数据链路阶段）
		case CONN_STATE_CONNECTED:		return "CONN_STATE_CONNECTED"; 	// 已连接到服务器（TCP数据链路阶段）
		case CONN_STATE_LOGINNING:		return "CONN_STATE_LOGINNING"; 	// 正在登录服务器（业务接入阶段）
		case CONN_STATE_LOGIN_OK:		return "CONN_STATE_LOGIN_OK"; 	// 成功登录服务器（业务接入阶段）
		case CONN_STATE_LOGIN_ERR:		return "CONN_STATE_LOGIN_ERR"; 	// 登录失败：如用户名或密码错，及其它原因
		case CONN_STATE_KICKOUT:		return "CONN_STATE_KICKOUT"; 	// 用户被系统下线，由于在别处登录
		case CONN_STATE_NETWORK_ERR:	return "CONN_STATE_NETWORK_ERR"; // 网络或通信信号异常（稍后会自动重连）
		case CONN_STATE_LOGOUTING:		return "CONN_STATE_LOGOUTING"; 	// 正在登出服务器
		case CONN_STATE_STOPPED:		return "CONN_STATE_STOPPED"; 	// 连接已停止
		}
		return "UNKONW";
	}
	
	public String getAuthReasonString(int reason){
		switch(reason){
		case REASON_AUTH_SUCCESS: 			return "REASON_AUTH_SUCCESS"; 			// 登录成功
		case REASON_AUTH_USER_NON_EXIST:	return "REASON_AUTH_USER_NON_EXIST"; 	// 登录失败原因：用户不存在
		case REASON_AUTH_USER_PASS_ERR:		return "REASON_AUTH_USER_PASS_ERR"; 	// 登录失败原因：用户名或密码错误
		case REASON_AUTH_UNKONW:			return "REASON_AUTH_UNKONW"; 			// 登录失败原因：登录失败（不明原因统一为此）
		case REASON_AUTH_TIMEOUT:			return "REASON_AUTH_TIMEOUT"; 			// 登录失败原因：登录超时
		}
		return "UNKONW";
	}

	/**
	 * 初始化参数
	 * 
	 * @return
	 */
	private boolean initialize(){
		long currTime = new Date().getTime();
		mLoginErrReason = REASON_AUTH_UNKONW;
		mLastConnectTime = currTime;
		mLastLoginTime = currTime;
		mLastNetworkErrTime = currTime;
		mIsSendingPersistentData = false;
		mTheIQidOfPerData = "";
		
		cancelAllSendExtData();

		return false;
	}

	/**
	 * 设置连接状态，当状态改变时，通知所有监听者。
	 * 
	 * @param state	连接状态：CONN_STATE_*
	 * @return
	 */
	private boolean setConnectionState(int state){
		synchronized (mConnectionState) {
			int oldState = getConnectionState();
			
			// Return immediately while the state are equal to current state
			if(oldState == state)
				return true;
			
			// Change the state
			mConnectionState = state;
			
			debugLog(String.format("ConnectionStateChanged old:%s, new:%s", 
					getConncectStateString(oldState), getConncectStateString(state)));
			
			// Notify the connection state listeners
			for(IConnStateListener listener : mConnStateListeners){
				mHandler.post(new StateChangeRunnable(listener, oldState, state));
			}
		}
		
		return true;
	}
	
	private void debugLog(String log){
		Log.d("CYX_Connection", log);
	}
	
	/**
	 * 触发开始事件
	 */
	private boolean eventConnecting(){
		
		// Transfer to connecting state
		setConnectionState(CONN_STATE_CONNECTING);
		
		// Disconnect first if there is already a connect object
		if(null != mXMPPConnection){
			debugLog("eventConnecting: A connection exist, disconnect it first");
			mXMPPConnection.removePacketListener(this);
			mXMPPConnection.removeConnectionListener(this);
			try {
				mXMPPConnection.disconnect();
				mXMPPConnection = null;
			} catch (NotConnectedException e) {
				e.printStackTrace();
			}
		}

		debugLog("ConnectionConfiguration serverAddr:"+ mServerAddr + " serverPort:" + mServerPort);
		ConnectionConfiguration connConfig = new ConnectionConfiguration(mServerAddr, mServerPort);
		connConfig.setReconnectionAllowed(false);
		connConfig.setSendPresence(true);
		connConfig.setRosterLoadedAtLogin(false);
		connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		ProviderManager.addIQProvider(VCARD_TAG_NAME, VCARD_TAG_NAMESPACE, new mVCardProvider());
		mXMPPConnection = new XMPPTCPConnection(connConfig);
		mXMPPConnection.addConnectionListener(this);
		mXMPPConnection.addPacketListener(this, new PacketFilter() {
			@Override
			public boolean accept(Packet packet) {
				//debugLog("PacketListener packet:" + packet.toXML());
				if (IQ.class.isInstance(packet)) {
					CharSequence cs = ((IQ) packet).getChildElementXML();
					String forwardXML = (null != cs ? cs.toString() : "") ;
					
					// Check to see is there the forward element
					if (null != forwardXML
							&& forwardXML.matches(String.format(
									"(?im).*<%s\\s*xmlns=\"??%s\"??>.*</%s>.*",
									FORWARD_TAG_NAME, FORWARD_TAG_NAMESPACE,
									FORWARD_TAG_NAME))) {
						return true;
					}
				}
				return false;
			}
		});
		
		mLastConnectTime = 0;

		synchronized (mStateWorker.mWakeupEvent) {
			try{
				mStateWorker.mWakeupEvent.notifyAll();
			}catch(Exception e){
			}
		}
		
		return true;
	}
	
	/**
	 * 触发连接成功事件
	 */
	private boolean eventConnected(){
		// Should trigger this event when state is on CONN_STATE_CONNECTING
		if(CONN_STATE_CONNECTING != getConnectionState())
			return false;
		
		// Transfer to connected state
		setConnectionState(CONN_STATE_CONNECTED);
		
		// Choose the start type to execute
		if(START_TYPE_DEFAULT == mStartType){
			eventLogin();
		}
		
		return true;
	}
	
	/**
	 * 触发用户登录事件
	 */
	private boolean eventLogin(){
		// Should trigger this event when state is on CONN_STATE_CONNECTING
		if(CONN_STATE_CONNECTED != getConnectionState())
			return false;
		
		mLastLoginTime = new Date().getTime();
		
		// Transfer to loginning state
		setConnectionState(CONN_STATE_LOGINNING);

		try {
			mXMPPConnection.login(mUsername, mPassword,"mobile");
		} catch (SaslException e) {
			//e.printStackTrace();
			eventLoginErr(REASON_AUTH_USER_PASS_ERR);
			//如果自动登录失败，则自动断开重连
			reConn();
			return true;
		} catch (XMPPException e) {
			//e.printStackTrace();
			boolean isUserPassErr = Pattern.matches("(?im).*SASLError.*:.*not-authorized.*", e.getMessage());
			if(isUserPassErr){
			}else{
				eventLoginErr(REASON_AUTH_UNKONW);
			}
			reConn();
			return true;
		} catch (SmackException e) {
			//e.printStackTrace();
			eventLoginErr(REASON_AUTH_TIMEOUT);
			reConn();
			return true;
		} catch (IOException e) {
			//e.printStackTrace();
			eventLoginErr(REASON_AUTH_UNKONW);
			reConn();
			return true;
		} 
		return true;
	}
	
	/**
	 * 主动断开重连
	 */
	private void reConn() {
		stop();
		setStartType(Connection.START_TYPE_CONNECT_ONLY);
		start();
	}
	
	/**
	 * 触发用户登录成功事件
	 */
	private boolean eventLoginOK(){
		// Should trigger this event when state is on CONN_STATE_LOGINNING
		if(CONN_STATE_LOGINNING != getConnectionState())
			return false;
		
		// Notify the requester
		synchronized (mSendingDataSet) {
			if (mSendingDataSet.containsKey(KEY_FOR_LOGIN)) {
				SendingData sendingData = mSendingDataSet.get(KEY_FOR_LOGIN);

				RequestCallbackRunnable r = new RequestCallbackRunnable(
						sendingData.requestCallback, true,
						RequestCallback.REASON_OK, new JSONObject());
				mHandler.post(r);
				mSendingDataSet.remove(KEY_FOR_LOGIN);
			}
		}
		
		// Transfer to login OK state
		setConnectionState(CONN_STATE_LOGIN_OK);

		return true;
	}
	
	/**
	 * 触发用户登录失败事件
	 * 
	 * @param reason		登录失败原因：REASON_AUTH_* (或者是系统返回的错误码)
	 * @return
	 */
	private boolean eventLoginErr(int reason){
		// Should trigger this event when state is on CONN_STATE_LOGINNING
		if(CONN_STATE_LOGINNING != getConnectionState())
			return false;
		
		mLoginErrReason = reason;
		debugLog("eventLoginErr, reason:" + getAuthReasonString(reason));
		
		// Notify the requester
		synchronized (mSendingDataSet) {
			if (mSendingDataSet.containsKey(KEY_FOR_LOGIN)) {
				SendingData sendingData = mSendingDataSet.get(KEY_FOR_LOGIN);

					RequestCallbackRunnable r = new RequestCallbackRunnable(
							sendingData.requestCallback, false,
							RequestCallback.REASON_ERROR, new JSONObject());
					mHandler.post(r);
					mSendingDataSet.remove(KEY_FOR_LOGIN);
			}
		}
		
		// Transfer to login error state
		setConnectionState(CONN_STATE_LOGIN_ERR);

		return true;
	}
	
	/**
	 * 系统主动请用户退出登录，原因是在别处登录了相同账号。
	 */
	private boolean eventKickout(){
		// Should trigger this event when state is the below values
		if (CONN_STATE_CONNECTING != getConnectionState()
				&& CONN_STATE_CONNECTED != getConnectionState()
				&& CONN_STATE_LOGINNING != getConnectionState()
				&& CONN_STATE_LOGIN_OK != getConnectionState())
			return false;
				
		// Transfer to login OK state
		setConnectionState(CONN_STATE_KICKOUT);

		return true;
	}

	/**
	 * 触发退出登录事件
	 */
	@SuppressWarnings("unused")
	private boolean eventLogout(){
		// Should trigger this event when state is on CONN_STATE_LOGIN_OK && CONN_STATE_CONNECTED
		if(CONN_STATE_LOGIN_OK != getConnectionState() && CONN_STATE_CONNECTED != getConnectionState())
			return false;
		
		// Transfer to login OK state
		setConnectionState(CONN_STATE_LOGOUTING);

		return true;
	}
	
	/**
	 * 触发停止连接管理事件
	 */
	private boolean eventStop(){
		// Stop the worker thread 
		synchronized (mSyncObj) {
			if(null != mStateWorker){
				mStateWorker.mRunning = false;
				
				synchronized (mStateWorker.mWakeupEvent) {
					try{
						mStateWorker.mWakeupEvent.notifyAll();
					}catch(Exception e){
					}
				}
			}
		}

		// Disconnect first if there is already a connect object
		if(null != mXMPPConnection){
			debugLog("eventStop: A connection exist, disconnect it first");
			mXMPPConnection.removePacketListener(this);
			mXMPPConnection.removeConnectionListener(this);
			try {
				mXMPPConnection.disconnect();
				mXMPPConnection = null;
			} catch (NotConnectedException e) {
				e.printStackTrace();
			}
		}
		// Save the persistent data cache
		doSavePersistentCache();

		// Transfer to login OK state
		setConnectionState(CONN_STATE_STOPPED);

		return true;
	}
	
	/**
	 * 触发网络连接异常事件
	 */
	private boolean eventNetworkErr(){
		// Should trigger this event when state is the below values
		if (CONN_STATE_CONNECTING != getConnectionState()
				&& CONN_STATE_CONNECTED != getConnectionState()
				&& CONN_STATE_LOGINNING != getConnectionState()
				&& CONN_STATE_LOGIN_OK != getConnectionState()
				&& CONN_STATE_LOGIN_ERR != getConnectionState())
			return false;
		
		// Save the persistent data cache
		doSavePersistentCache();

		mLastNetworkErrTime = new Date().getTime();
		
		// Notify all waiter for network error
		synchronized (mSendingDataSet) {
			if (mSendingDataSet.size() > 0) {
				ArrayList<String> keys = new ArrayList<String>();

				for (SendingData data : mSendingDataSet.values()) {
					if (null != data.requestCallback) {
						RequestCallbackRunnable r = new RequestCallbackRunnable(
								data.requestCallback, false,
								RequestCallback.REASON_NO_SIGNAL, null);
						mHandler.post(r);
					}

					// Save the key of timeout data
					keys.add(data.key);
				}

				// Delete data
				for (String key : keys) {
					mSendingDataSet.remove(key);
				}
			}
		}
		
		// Transfer to login OK state
		setConnectionState(CONN_STATE_NETWORK_ERR);

		return true;
	}

	/**
	 * 利用状态机的机制实现的逻辑管理工作者
	 */
	private class StateWorker extends Thread {
		public boolean mRunning = false;
		public Integer mWakeupEvent = new Integer(0);

		public void run() {
			while (mRunning) {
				long currTime = new Date().getTime();
				
				switch (mConnectionState) {
				case CONN_STATE_INIT:
					eventConnecting();
					break;
				case CONN_STATE_CONNECTING:
					// If connect timeout then try to connect again
					if(currTime - mLastConnectTime > 50*1000){
						mLastConnectTime = new Date().getTime();

						try {
							mXMPPConnection.connect();
						} catch (SmackException e) {
							e.printStackTrace();
							eventLoginErr(REASON_AUTH_USER_PASS_ERR);
						} catch (IOException e) {
							e.printStackTrace();
							eventNetworkErr();
						} catch (XMPPException e) {
							e.printStackTrace();
							eventNetworkErr();
						}
					}
					break;
				case CONN_STATE_CONNECTED:
					checkAndSendExtData();
					break;
				case CONN_STATE_LOGINNING:
					// If login timeout then try to connect again
					if(currTime - mLastLoginTime > 50*1000){
						eventNetworkErr();
					}
					break;
				case CONN_STATE_LOGIN_OK:
					checkAndSendExtData();
					checkAndSendPersistentData();
					break;
				case CONN_STATE_LOGIN_ERR:
					checkAndSendExtData();
					// If login error and waiting to long then re-login again
					if(currTime - mLastLoginTime > 5*60*1000){
						eventConnecting();
					}
					break;
				case CONN_STATE_NETWORK_ERR:
					// If network error and waiting to long then re-connect again
					if(currTime - mLastNetworkErrTime > 3*60*1000){
						eventConnecting();
					}
					break;
				case CONN_STATE_LOGOUTING:
					break;
				case CONN_STATE_STOPPED:
					break;
				}

				// Sleep some time
				synchronized (mWakeupEvent) {
					try {
						mWakeupEvent.wait(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private class SendingData{
		@SuppressWarnings("unused")
		public JSONObject jsonExtData;
		public RequestCallback requestCallback;
		public Long lastSendingTime;
		public cyx_IQquery theIQ;
		public String key;
	}

	@Override
	public void processPacket(Packet packet) throws NotConnectedException {
//		debugLog("<<< Received Packet:" + packet.toXML());
		LogUtil.printLog(LogUtil.DEBUG, "cyx_drive_sdk ", "received : " +packet.toXML().toString());
		String id = packet.getPacketID();
		Matcher matcher = null;
		String jsonData = null;
		
		// Find the JSON data using regular expression
		try {
			matcher = Pattern
					.compile(
							String.format(
									"(?im).*<\\s*%s\\s*xmlns=\"??%s\"??\\s*>\\s*(\\{.*\\})\\s*<\\s*/\\s*%s\\s*>.*",
									FORWARD_TAG_NAME, FORWARD_TAG_NAMESPACE,
									FORWARD_TAG_NAME)).matcher(
							null != packet.toXML() ? packet.toXML() : "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Get the JSON data
		if(null != matcher && matcher.matches()){
			try{
				if(matcher.groupCount() > 0)
					jsonData = matcher.group(1);
			}catch(Exception e){}
		}
		
		// Notify the request callback
		if (null != jsonData) {
			
			synchronized (mSendingDataSet) {
				if (mSendingDataSet.containsKey(id)) {
					SendingData sendingData = mSendingDataSet.get(id);

					try {
						RequestCallbackRunnable r = new RequestCallbackRunnable(
								sendingData.requestCallback, true,
								RequestCallback.REASON_OK, new JSONObject(
										jsonData));
						mHandler.post(r);
						mSendingDataSet.remove(id);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					debugLog("Received data that couldn't found the sending cache!");
				}
			}
		}
		
		// check the response of persistent data
		doReceivedPersistentData(id);
	}
	
	private class ForwardIQ extends IQ {
		private String jsonData = "";

		public void setJsonData(String jsonData) {
			this.jsonData = jsonData;
		}

		@Override
		public CharSequence getChildElementXML() {
			String result = String.format("<%s xmlns=%s>%s</%s>",
					FORWARD_TAG_NAME, FORWARD_TAG_NAMESPACE, jsonData, FORWARD_TAG_NAME);
			return result;
		}
	}

	@Override
	public IQ parseIQ(XmlPullParser xmlData) throws Exception {
		String jsonData = "";
		boolean isTagForwardFound = false;
		
		int eventType = xmlData.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (null != xmlData.getName()
						&& xmlData.getName().equalsIgnoreCase(FORWARD_TAG_NAME)) {
					isTagForwardFound = true;
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (null != xmlData.getName()
						&& xmlData.getName().equalsIgnoreCase(FORWARD_TAG_NAME)) {
					isTagForwardFound = false;
					break;
				}
			} else if (eventType == XmlPullParser.TEXT) {
				if (isTagForwardFound) {
					jsonData = (null != xmlData.getText() ? xmlData.getText()
							: "");
				}
			}

			try {
				eventType = xmlData.next();
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
        
        ForwardIQ iq = new ForwardIQ();
        iq.setJsonData(jsonData);
//		debugLog("[iqprovider] The responsed ForwordIQ:" + iq.getChildElementXML());
		return iq;
	}

	@Override
	public void authenticated(XMPPConnection conn) {
		debugLog("[listener]authenticated");
		mPingManager = PingManager.getInstanceFor(conn);
		mPingManager.setPingInterval(3*60);
		debugLog(String.format("mPingManager ping interval:%d", mPingManager.getPingInterval()));
		eventLoginOK();
		
		if(CYX_Drive_SDK.getInstance().getConnection().getConnectionState()==Connection.CONN_STATE_LOGIN_OK&& 
				!CYX_Drive_SDK.getInstance().getUserInfo().getName().equals("temp")) {
		VCard vCard = new VCard();
		try {
			vCard.load(mXMPPConnection);
		} catch (NoResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}

	@Override
	public void connected(XMPPConnection conn) {
		debugLog("[listener]connected");
		eventConnected();
	}

	@Override
	public void connectionClosed() {
		debugLog("[listener]connectionClosed");
		eventNetworkErr();
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		debugLog("[listener]connectionClosedOnError:" + e.getMessage());
		
		boolean isResourceConflict = Pattern.matches("(?im).*stream.*:.*error.*(conflict).*", e.getMessage());
		
		if(isResourceConflict){
			eventKickout();
		}else{
			eventNetworkErr();
		}
	}

	@Override
	public void reconnectingIn(int reconnTime) {
		debugLog("[listener]reconnectingIn:" + reconnTime);
	}

	@Override
	public void reconnectionFailed(Exception e) {
		debugLog("[listener]reconnectionFailed:" + e.toString());
		eventNetworkErr();
	}

	@Override
	public void reconnectionSuccessful() {
		debugLog("[listener]reconnectionSuccessful");
		eventConnected();
	}

	public XMPPConnection getConn(){
			return mXMPPConnection;
	}
	
	private static class mVCardProvider extends VCardProvider {
		
		@Override
		public IQ parseIQ(XmlPullParser parser) throws Exception {
			// TODO Auto-generated method stub
			String content = "";
			 StringBuilder sb = new StringBuilder();
			    int depth = 1;
			    while (depth != 0) {
			        switch (parser.next()) {
			        case XmlPullParser.END_TAG:
			            depth--;
			            if (depth > 0) {
			                sb.append("</" + parser.getName() + ">");
			            }
			            break;
			        case XmlPullParser.START_TAG:
			            depth++;
			            StringBuilder attrs = new StringBuilder();
			            for (int i = 0; i < parser.getAttributeCount(); i++) {
			                attrs.append(parser.getAttributeName(i) + "=\""
			                        + parser.getAttributeValue(i) + "\" ");
			            }
			            sb.append("<" + parser.getName() + " " + attrs.toString() + ">");
			            break;
			        default:
			            sb.append(parser.getText());
			            break;
			        }
			    }
	         content = sb.toString();
	         if(!TextUtils.isEmpty(content)) {
	        	 String[] userTypes = getElementsByTag(VCARD_EXTRA_USER_TYPE, content);		//获取usertype
	        	 if(userTypes == null || userTypes.length==0) {
	        		 Log.d("mVCardProvider", "flag data is null");
	        	 } else {
		        	 String userType = userTypes[0].trim();
//		        	 if(!"0".equalsIgnoreCase(userType) || !"1".equalsIgnoreCase(userType) || !"2".equalsIgnoreCase(userType)) {
//		        		 System.out.println("----------------------- userType 11111 " + userType);
//		        		 Log.d("mVCardProvider", "flag data is null");
//		        	 } else {
			        	 int flag = Integer.parseInt(userType.trim());
			        	 System.out.println("------------ flag " + flag);
			        	 if(flag ==0 || flag == 1 || flag == 2) {
				        	 CYX_Drive_SDK.getInstance().getUserInfo().setFlag(flag);
				        	 CYX_Drive_SDK.getInstance().getUserManager().setUserFlag(CYX_Drive_SDK.getInstance().getUserInfo().getName(), flag);
			        	 }
//		        	 }
	        	 }
	         } else {
	        	 Log.d("mVCardProvider", "flag data is null");
	         }
//			return super.parseIQ(mParser);
	         return new VCard();
		}
		
	}
	
	 /**
	   * 在文档中搜索指定的元素,返回符合条件的元素数组.
	   * @param tagName, content
	   * @return String[]
	   */
	public static String[] getElementsByTag(String tagName, String content)  
	  {  
		Pattern pattern=Pattern.compile("<" + tagName + " >(.*?)</" + tagName + ">");  
	      Matcher matcher=pattern.matcher(content);  
	      ArrayList<String> al = new ArrayList<String>();
		  while(matcher.find())
		      al.add(matcher.group(1));
		  String[] arr = al.toArray(new String[al.size()]);
		  al.clear();
	      return arr;  
	  }  
}
