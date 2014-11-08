package com.cwits.cyx_drive_sdk.connection;

/**
 * 用于实现部分需要可靠发送的数据的持久化存储的接口
 * 
 * @author Zorson
 */
public interface IPersistentStorage {

	/**
	 * 获得当前存储的数据数量。
	 * 
	 * @return		当前存储的数据数量
	 */
	public int getCount();
	
	/**
	 * 读取有序存储中处于头部（第一个）的数据。
	 * 数据为 JSON String 格式，此接口的实现并不用关心数据的格式，只需要保证能按序存取数据即可。
	 * 
	 * @return		读取到的数据
	 */
	public String getHeader();
	
	/**
	 * 将数据存入有序存储的尾部（最后一个）。
	 * 数据为 JSON String 格式，此接口的实现并不用关心数据的格式，只需要保证能按序存取数据即可。
	 * 
	 * @param jsonStrData	JSON String 格式的数据
	 * @return				如果成功返回true，否则false（比如写数据时拋异常了可以返回false）
	 */
	public boolean addTail(String jsonStrData);
	
	/**
	 * 删除有序存储中处于头部（第一个）的数据。
	 * 
	 * @return		如果成功返回true，否则false（如果删除操作时已经无数据了应该返回false）
	 */
	public boolean deleteHeader();
	
	/**
	 * 修改有序存储中处于头部（第一个）的数据
	 * 
	 * @param jsonStrData	待修改的数据
	 * @return		如果成功返回true，否则false（如果删除操作时已经无数据了应该返回false）
	 */
	public boolean modifyHeader(String jsonStrData);
	
	/**
	 * 读取所有数据
	 * 
	 * @return		所有数据的数组
	 */
	public String[] getAll();
	
	/**
	 * 清除所有数据
	 * 
	 * @return		如果成功返回true，否则false（如果删除操作时已经无数据了应该返回false）
	 */
	public boolean clear();
}
