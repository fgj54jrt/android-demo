package com.cwits.cyx_drive_sdk.util;


import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;

import com.baidu.location.BDLocation;

public class BMapUtil {
    	
	/**
	 * ��view �õ�ͼƬ
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache(true);
	}
	/**
	 * 从 dp转成为 px(像素)
	 */
	public static  int dp2px(Context context, int dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
	
	//通过经纬度得到地址信息
	public static String getAddress(Context context, double lon, double lat) {
		Geocoder geocoder=new Geocoder(context);
		List<Address> places = null;
		try {
			places = geocoder.getFromLocation(lat, lon, 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//其中getAddressLine(0)表示国家，getAddressLine(1)表示精确到某个区，getAddressLine(2)表示精确到具体的街
		//广东省深圳市南山区北环大道   北环科苑立交   清华信息港科研楼
		if(places!=null && places.size()>0) {
			return ((Address) places.get(0)).getAddressLine(2);
		} 
		return "";
	}
	public static String getCity(Context context, BDLocation location){
		double[] wg = Coordinate.baidutowg(location.getLongitude(), location.getLatitude());
		Geocoder geocoder=new Geocoder(context);
		List<Address> places = null;
		try {
			places = geocoder.getFromLocation(wg[1], wg[0], 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//其中getAddressLine(0)表示国家，getAddressLine(1)表示精确到某个区，getAddressLine(2)表示精确到具体的街
		//广东省深圳市南山区北环大道   北环科苑立交   清华信息港科研楼
		if(places!=null && places.size()>0) {
			return ((Address) places.get(0)).getAddressLine(1);
		} 
		return "";
	}
}
