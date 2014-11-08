package com.cwits.cyx_drive_sdk.data;

import com.cwits.cyx_drive_sdk.connection.IPersistentStorage;

/**
 * 持久化数据（主要包括待发送至系统的行驶相关数据）存取对象提供者。
 * 
 * @author Zorson
 */
public class PersistentStorageProvider {
	static IPersistentStorage mPersistentStorage = null;
	/**
	 * 获得指定账号的持久化数据存取对象
	 * ！实现时注意：因为此方法为静态方法，同时会被非常多次反复调用，所以实现要避免不断重复地创建对象。
	 * 
	 * @param usernameOrUID		用户账号名或UID
	 * @return					持久化数据存取对象（当指定的账号不存在时可以返回null）
	 */
	public static IPersistentStorage getPersistentStorage(String usernameOrUID){
		if(mPersistentStorage==null)
			mPersistentStorage = new PersistentStorage(usernameOrUID);
		return mPersistentStorage; 
	}
}
