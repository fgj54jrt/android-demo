package com.cwits.cyx_drive_sdk.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MKMapStatusChangeListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ConstantContext;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.ui.cyx_SearchResultMapActivity.MyMKMapStatusChangeListener;
import com.cwits.cyx_drive_sdk.userInfo.Constant;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;
import com.cwits.cyx_drive_sdk.util.BMapUtil;
import com.cwits.cyx_drive_sdk.util.Coordinate;

public class cyx_CarLocationActivity extends Activity {
	
	private Button btn_satellite, btn_location, btn_zoomin, btn_zoomout;
	private TextView tv_latest, tv_time, title, btn_refresh;
	private UserInfo userInfo;
	private String hintContent;
	private ProgressDialog mProgressDialog;
	private Handler mHandler;
	private double lon, lat, lon_pre, lat_pre, lon_p, lat_p;
	private ImageView btn_back;
	
	private MapView mMapView = null; // 地图View
	private MapController mMapController = null;
	private boolean isSatellite = false;
	private MyLocationOverlay myLocationOverlay = null;
	private LocationData locData = null;
	private MKSearch mMKSearch = null;
	private String addr = "";
	private String time_pre = "";
	private String add_pre = "";
	
	private SharedPreferences adrPreference, prePreference; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (cyx_MyApplication.mBMapManager == null) {
			cyx_MyApplication.mBMapManager = new BMapManager(getApplicationContext());
			cyx_MyApplication.mBMapManager.init(new cyx_MyApplication.MyGeneralListener());
		}
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_car_location"));
		cyx_MyApplication.getInstance().addActivity(this);
		
		init();
	}
	
	private void init() {
		//获取盒子上一个位置信息
		tv_latest = (TextView) findViewById(MResource.getID(getApplicationContext(), "latest_location"));
		tv_time = (TextView) findViewById(MResource.getID(getApplicationContext(), "latest_time"));
		btn_location = (Button) findViewById(MResource.getID(getApplicationContext(), "btn_getLocation"));
		prePreference = getSharedPreferences("CXB_LOCATION", MODE_PRIVATE);
		lon_pre = Double.parseDouble(prePreference.getString("lon_pre", "-200"));
		lat_pre = Double.parseDouble(prePreference.getString("lat_pre", "-200"));
		time_pre  = prePreference.getString("time_pre", "");
		add_pre = prePreference.getString("add_pre", "");
		btn_zoomin = (Button) findViewById(MResource.getID(getApplicationContext(),  "btn_zoomin"));
		btn_zoomout = (Button) findViewById(MResource.getID(getApplicationContext(),  "btn_zoomout"));
		btn_zoomin.setOnClickListener(clickListener);
		btn_zoomout.setOnClickListener(clickListener);
		mMapView = (MapView) findViewById(MResource.getID(getApplicationContext(),"mMapView"));
		mMapController = mMapView.getController();
		mMapController.setZoom(16);
		mMapController.enableClick(true);
		mMapController.setScrollGesturesEnabled(true);
		mMapView.regMapViewListener(cyx_MyApplication.mBMapManager, new MyMKMapViewListener());
		mMapView.regMapStatusChangeListener(new MyMKMapStatusChangeListener());
		mMapView.setEnabled(true);
		myLocationOverlay = new MyLocationOverlay(mMapView);
		//先拿上一个盒子的位置，如果有，则显示上一个位置，没有则显示用户当前的位置
		if(lon_pre!=-200 && lon_pre!=-200) {
			Log.d("--------------car location ", "get pre location success");
			GeoPoint mGeoPoint = new GeoPoint((int) (lat_p * 1e6), (int) (lon_p * 1e6));
			mMapController.animateTo(mGeoPoint);
			myLocationOverlay.setData(locData);
			mMapView.getOverlays().remove(myLocationOverlay);
			mMapView.getOverlays().add(myLocationOverlay);
			mMapView.refresh();
			btn_location.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "is_location"));
			btn_location.setEnabled(false);
			tv_time.setText(time_pre);
			tv_latest.setText(add_pre);
		} else {
			Log.d("--------------car location ", "pre location is null");
			//获取当前用户的位置，用户信息在service中已经获取并保存
			adrPreference = getSharedPreferences(Constant.ADDRESS_INFO, MODE_PRIVATE);
			lon_p = Double.parseDouble(adrPreference.getString(Constant.LON, "-200"));
			lat_p = Double.parseDouble(adrPreference.getString(Constant.LAT, "-200"));
			if(lon_p!=-200 && lat_p!=-200) {
				GeoPoint mGeoPoint = new GeoPoint((int) (lat_p * 1e6), (int) (lon_p * 1e6));
				mMapController.animateTo(mGeoPoint);
			}
		}
		
		mHandler = new Handler();
		btn_refresh = (TextView) findViewById(MResource.getID(getApplicationContext(), "TextTitle_Right"));
		btn_refresh.setText(getResources().getString(MResource.getStringId(getApplicationContext(), "refresh")));
		btn_refresh.setVisibility(View.VISIBLE);
		btn_refresh.setOnClickListener(clickListener);
		btn_satellite = (Button) findViewById(MResource.getID(getApplicationContext(), "btn_satellite"));
		btn_satellite.setOnClickListener(clickListener);
		btn_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(locData!=null){
					mMapController.animateTo(new GeoPoint((int) (locData.latitude * 1e6), (int) (locData.longitude * 1e6)));
					btn_location.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "is_location"));
					btn_location.setEnabled(false);
				}
			}
		});
		btn_zoomin = (Button) findViewById(MResource.getID(getApplicationContext(), "btn_zoomin"));
		btn_zoomout = (Button) findViewById(MResource.getID(getApplicationContext(), "btn_zoomout"));
		
		title = (TextView) findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		title.setText(MResource.getStringId(getApplicationContext(), "car_location"));
		
		btn_back = (ImageView)findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_CarLocationActivity.this.finish();
			}
		});
		
		mMKSearch = new MKSearch();
		mMKSearch.init(cyx_MyApplication.mBMapManager, new MySearchListener());  
		
		userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
		
		if(userInfo == null || TextUtils.isEmpty(userInfo.getName())) {
			Toast.makeText(this, MResource.getStringId(getApplicationContext(), "get_user_info_error"), Toast.LENGTH_SHORT).show();
		} else {
			mProgressDialog = new ProgressDialog(cyx_CarLocationActivity.this);
			mProgressDialog.setTitle(getResources().getString(MResource.getStringId(getApplicationContext(), "notice")));
			mProgressDialog.setMessage(getResources().getString(MResource.getStringId(getApplicationContext(), "wait")));
			mProgressDialog.setCanceledOnTouchOutside(false);
			getCarLocation();
		}
	}
	
private void showHintContent() {
	if(!TextUtils.isEmpty(hintContent)) {
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(mProgressDialog!=null&&mProgressDialog.isShowing())
					mProgressDialog.dismiss();
				Toast.makeText(cyx_CarLocationActivity.this,hintContent,Toast.LENGTH_SHORT).show(); 
				hintContent = "";
			}
		});
	}
}


@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	if(mMapView!=null) {
		mMapView.destroy();
		mMapView = null;
	}
	myLocationOverlay = null;
	cyx_MyApplication.getInstance().removeActivity(this);
}
	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			if(id==MResource.getID(getApplicationContext(), "btn_zoomin")){
				mMapController.zoomIn();
					
			}else if(id==MResource.getID(getApplicationContext(), "btn_zoomout")){
				mMapController.zoomOut();
			}
			else if(id==MResource.getID(getApplicationContext(), "btn_getLocation")){
				mMapController.animateTo(new GeoPoint((int) (lat * 1e6), (int) (lon * 1e6)));
			} else if(id == MResource.getID(getApplicationContext(), "TextTitle_Right")) {
//				mMapView.refresh();
				getCarLocation();
			} else if(id == MResource.getID(getApplicationContext(), "btn_satellite")) {
				if(!isSatellite) {
					mMapView.setSatellite(true);
					isSatellite = true;
				} else {
					mMapView.setSatellite(false);
					isSatellite = false;
				}
			}
		}
		
	};
	
	private class MyMKMapViewListener implements MKMapViewListener {

		@Override
		public void onClickMapPoi(MapPoi arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetCurrentMap(Bitmap arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMapAnimationFinish() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMapLoadFinish() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMapMoveFinish() {
			// TODO Auto-generated method stub
			GeoPoint movedCenter = mMapView.getMapCenter();
			GeoPoint loc = new GeoPoint((int) (lat * 1e6), (int) (lon * 1e6));
			if(movedCenter!=loc){
				btn_location.setEnabled(true);
				btn_location.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_getlocation_bg"));
		 }
		}
	}	

class MyMKMapStatusChangeListener implements MKMapStatusChangeListener {

	@Override
	public void onMapStatusChange(MKMapStatus status) {
		// TODO Auto-generated method stub
		if (status.zoom == mMapView.getMaxZoomLevel()){
			btn_zoomin.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "ic_zoomin"));
			btn_zoomin.setEnabled(false);
		  }
		else{
			btn_zoomin.setEnabled(true);
			btn_zoomin.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_zoom_in"));
		 }

		if (status.zoom == mMapView.getMinZoomLevel()){
			btn_zoomout.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "ic_zoomout"));
			btn_zoomout.setEnabled(false);
			}
		else{
			btn_zoomout.setEnabled(true);
			btn_zoomout.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_zoom_out"));
			}
		}

}

	private class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo result, int arg1) {
			// TODO Auto-generated method stub
			if (result == null) {  
				addr = getResources().getString(MResource.getStringId(getApplicationContext(), "unknow"));
            }  else {
            	addr = result.strAddr;
            }
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					tv_latest.setText(addr);
					prePreference.edit().putString("add_pre", addr).commit();
				}
			});
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		} 
		
	}
	
	private void getCarLocation() {
		if(mProgressDialog!=null && !mProgressDialog.isShowing())
			mProgressDialog.show();
		Connection.getInstance().sendExtData(new ExtraDataProcess().getLocation(userInfo.getUserID()), new RequestCallback() {

			@Override
			public void onSuccess(String bizJsonData) {
				// TODO Auto-generated method stub
				if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_CarLocationActivity.this.getClass().getName())) {
						try {
							JSONObject jsonObj = new JSONObject(bizJsonData);
							switch (jsonObj.getInt("result")) {
							case ConstantContext.SUCCESS:
								if(mProgressDialog!=null && mProgressDialog.isShowing())
									mProgressDialog.dismiss();
								String str = jsonObj.optString("position");
								Toast.makeText(cyx_CarLocationActivity.this, getResources().getString(MResource.getStringId(getApplicationContext(),"search_success" )), Toast.LENGTH_SHORT).show();
								if(!TextUtils.isEmpty(str)) {
									JSONObject position = new JSONObject(str);
									tv_time.setText(position.optString("time"));
									lon = position.optDouble("lon");
									lat = position.optDouble("lat");
//									tv_latest.setText(BMapUtil.getAddress(cyx_CarLocationActivity.this, lon, lat));
									double[] owgs = Coordinate.wgtobaidu(lon, lat);
									locData = new LocationData();
									locData.latitude = owgs[1];
									locData.longitude = owgs[0];
									GeoPoint mGeoPoint = new GeoPoint((int) (locData.latitude * 1e6), (int) (locData.longitude * 1e6));
									mMKSearch.reverseGeocode(mGeoPoint);
									mMapController.animateTo(mGeoPoint);
									myLocationOverlay.setData(locData);
									mMapView.getOverlays().remove(myLocationOverlay);
									mMapView.getOverlays().add(myLocationOverlay);
									mMapView.refresh();
									btn_location.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "is_location"));
									btn_location.setEnabled(false);
									prePreference.edit().putString("lon_pre", String.valueOf(lon)).commit();
									prePreference.edit().putString("lat_pre", String.valueOf(lat)).commit();
									prePreference.edit().putString("time_pre", tv_time.getText().toString()).commit();
								}
								break;
							case ConstantContext.ERROR_1:
								hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"search_fail" ));
								break;
							case ConstantContext.ERROR_2:
								hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"no_user" ));
								break;
							case ConstantContext.ERROR_3:
								hintContent = getResources().getString(MResource.getStringId(getApplicationContext(),"no_related_data" ));
								break;
							}
							showHintContent();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}

			@Override
			public void onFailed(int reason) {
				// TODO Auto-generated method stub
				if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_CarLocationActivity.this.getClass().getName())) {
					switch(reason) {
					case RequestCallback.REASON_NO_NETWORK:
						hintContent += getResources().getString(
								MResource.getStringId(getApplicationContext(), "network_switch_off"));
						break;
					case RequestCallback.REASON_NO_SIGNAL:
						hintContent += getResources().getString(
								MResource.getStringId(getApplicationContext(), "no_network_signal"));
						break;
					case RequestCallback.REASON_NOT_AUTHENTICATED:
						hintContent += getResources().getString(
								MResource.getStringId(getApplicationContext(), "no_auth"));
						break;
					case RequestCallback.REASON_TIMEOUT:
						hintContent += getResources().getString(
								MResource.getStringId(getApplicationContext(), "request_timeOut"));
						break;
					case RequestCallback.REASON_DATA_INCRECT:
						hintContent += getResources().getString(
								MResource.getStringId(getApplicationContext(), "data_increct"));
						break;
				}
				showHintContent();
				}
				}
		});
	}

}
