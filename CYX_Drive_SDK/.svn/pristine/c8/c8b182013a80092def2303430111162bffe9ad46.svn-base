package com.cwits.cyx_drive_sdk.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.cwits.cyx_drive_sdk.connection.IPersistentStorage;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;

public class PersistentStorage implements IPersistentStorage{
	String[] allStr = null;
	SharedPreferences mPreferences;
	String JSONSTR = "JSONSTR";
	public String DATA_URL = "/data/data/";
	public String SHARED_MAIN_XML = "";
	List<String> alldatas = new ArrayList<String>();

	public PersistentStorage(String name) {
		String fileName = name + "_" + "upload_data";
		mPreferences = CYX_Drive_SDK.getSavedContext().getSharedPreferences(
				fileName, Context.MODE_PRIVATE);
		SHARED_MAIN_XML = fileName + ".xml";
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		getAll();
		if (allStr != null && allStr.length > 0) {
			return allStr.length;
		} else
			return 0;
	}

	@Override
	public String getHeader() {
		// TODO Auto-generated method stub
		getAll();
		if (allStr != null && allStr.length > 0) {
			return allStr[0];
		} else
			return "";
	}

	@Override
	public boolean addTail(String jsonStrData) {
		// TODO Auto-generated method stub
		Log.i("lxh", "-------------添加 ");
		alldatas.add(jsonStrData);
		Log.i("lxh","---------------------allDatas.size: "+ alldatas.size());
		String allString = "";
		JSONArray jsonArray = new JSONArray(alldatas);

		if (jsonArray != null) {
			allString = jsonArray.toString();
			if (!allString.equals(new JSONArray().toString())) {
				mPreferences.edit().putString(JSONSTR, allString).commit();
			} else {
				mPreferences.edit().putString(JSONSTR, "").commit();
			}
		}
		return true;
	}

	@Override
	public boolean deleteHeader() {
		Log.i("lxh", "--------------删除 ");
		try {
				if(alldatas!=null&&alldatas.size()>0){
					alldatas.remove(0);
					JSONArray jsonArray2 = new JSONArray();
					for (int i = 0; i < alldatas.size(); i++) {
						JSONObject js = new JSONObject(alldatas.get(i));
						jsonArray2.put(js);
						js = null;
					}
					if (!jsonArray2.toString().equals(new JSONArray().toString())) {
						 mPreferences.edit().putString(JSONSTR, jsonArray2.toString()).commit();
						 }else{
							 mPreferences.edit().putString(JSONSTR, "").commit();
						 }
				}else{
					return false;
				}
		Log.i("lxh","---------------------allDatas.size: "+ alldatas.size());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return true;
	}

	@Override
	public boolean modifyHeader(String jsonStrData) {
		Log.i("lxh", "--------------更新 ");
		String allString = "";
			try {
				      if(alldatas!=null&&alldatas.size()>0){
				    	  	alldatas.set(0, jsonStrData);
							JSONArray jsonArray2 = new JSONArray();
							for (int i = 0; i < alldatas.size(); i++) {
								JSONObject js = new JSONObject(alldatas.get(i));
								jsonArray2.put(js);
								js = null;
							}
							if (jsonArray2 != null) {
								allString = jsonArray2.toString();
								jsonArray2 = null;
								if (!allString.equals(new JSONArray().toString())) {
									mPreferences.edit().putString(JSONSTR, allString).commit();
								}else{
									mPreferences.edit().putString(JSONSTR, "").commit();
								}
							}
					}else{
						return false;
					}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public String[] getAll() {
		// TODO Auto-generated method stub
		String str = mPreferences.getString(JSONSTR, "");
		List<String> allStrList = new ArrayList<String>();
		try {
			if (str != null && !str.equals("")
					&& !str.equals(new JSONArray().toString())) {
				JSONArray jsonArray = new JSONArray(str);
				if (jsonArray != null) {
					for (int i = 0; i < jsonArray.length(); i++) {
						allStrList.add(jsonArray.get(i).toString());
					}
					if (allStrList.size() > 0)
						allStr = allStrList.toArray(new String[allStrList
								.size()]);
				}
			}else{
				allStr = null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allStr;
	}

	@Override
	public boolean clear() {
		mPreferences.edit().clear().commit();
		File file = new File(DATA_URL
				+ CYX_Drive_SDK.getSavedContext().getPackageName().toString()
				+ "/shared_prefs", SHARED_MAIN_XML);
		if (file.exists()) {
			file.delete();
		}
		return false;
	}

}
