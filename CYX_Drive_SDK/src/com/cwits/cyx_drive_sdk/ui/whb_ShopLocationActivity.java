package com.cwits.cyx_drive_sdk.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MKMapStatusChangeListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
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
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.ui.cyx_MainActivity.MyLocationListener;
import com.cwits.cyx_drive_sdk.ui.cyx_SearchResultMapActivity.MyMKMapStatusChangeListener;
import com.cwits.cyx_drive_sdk.ui.cyx_SearchResultMapActivity.MyOverlay;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;
import com.cwits.cyx_drive_sdk.util.BMapUtil;
import com.cwits.cyx_drive_sdk.util.Coordinate;

public class whb_ShopLocationActivity extends Activity {

	private Button btn_satellite, btn_location, btn_zoomin, btn_zoomout;
	private TextView title, btn_refresh;
	private UserInfo userInfo;
	private String hintContent;
	private ProgressDialog mProgressDialog;
	private Handler mHandler;
	private double lon, lat;
	private ImageView btn_back;

	private MapView mMapView = null; // 地图View
	private MapController mMapController = null;
	private boolean isSatellite = false;
	private MyLocationOverlay myLocationOverlay = null;
	private LocationData locData = null;
	private boolean isFirstLoc=true;
	private MKSearch mMKSearch = null;
	private String addr = "";
	private double whb_lat,whb_lon;
	String whb_content;
	public MyLocationListener myListener = new MyLocationListener();
	LocationClient mLocClient;
	
	private MyOverlay mOverlay = null; // 自定义overlay显示商家的位置
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (cyx_MyApplication.mBMapManager == null) {
			cyx_MyApplication.mBMapManager = new BMapManager(
					getApplicationContext());
			cyx_MyApplication.mBMapManager
					.init(new cyx_MyApplication.MyGeneralListener());
		}
		setContentView(MResource.getLayoutId(getApplicationContext(),
				"whb_shop_location"));
		cyx_MyApplication.getInstance().addActivity(this);
		whb_lat = getIntent().getDoubleExtra("lat", 1);
		whb_lon = getIntent().getDoubleExtra("lon", 1);
		whb_content = getIntent().getStringExtra("content");
		Log.e("lxh","whb_lat:"+whb_lat+"   whb_lon:"+whb_lon+"   whb_content:"+whb_content);
		init();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		initLocation();
		super.onResume();
	}

	private void initLocation() {
		mLocClient = new LocationClient(getApplicationContext());
		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(LocationClientOption.MIN_SCAN_SPAN); // 最小间隔为1秒
		option.setNeedDeviceDirect(true);
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		mLocClient.setLocOption(option);
		mLocClient.start();
		mLocClient.requestLocation();
	}
	
	private void init() {
		btn_zoomin = (Button) findViewById(MResource.getID(
				getApplicationContext(), "btn_zoomin"));
		btn_zoomout = (Button) findViewById(MResource.getID(
				getApplicationContext(), "btn_zoomout"));
		btn_zoomin.setOnClickListener(clickListener);
		btn_zoomout.setOnClickListener(clickListener);
		mMapView = (MapView) findViewById(MResource.getID(
				getApplicationContext(), "mMapView"));
		mMapController = mMapView.getController();
		mMapController.setZoom(16);
		mMapController.enableClick(true);
		mMapController.setScrollGesturesEnabled(true);
		mMapView.regMapViewListener(cyx_MyApplication.mBMapManager,
				new MyMKMapViewListener());
		mMapView.regMapStatusChangeListener(new MyMKMapStatusChangeListener());
		mMapView.setEnabled(true);
		if (myLocationOverlay == null)
			myLocationOverlay = new MyLocationOverlay(mMapView);
		mMapView.getOverlays().add(myLocationOverlay);
		
		mHandler = new Handler();
		btn_refresh = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "TextTitle_Right"));
		btn_refresh.setText(getResources().getString(
				MResource.getStringId(getApplicationContext(), "refresh")));
		btn_refresh.setVisibility(View.VISIBLE);
		btn_refresh.setOnClickListener(clickListener);
		btn_satellite = (Button) findViewById(MResource.getID(
				getApplicationContext(), "btn_satellite"));
		btn_satellite.setOnClickListener(clickListener);
		btn_location = (Button) findViewById(MResource.getID(
				getApplicationContext(), "btn_getLocation"));
		btn_location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (locData != null) {
					mMapController.animateTo(new GeoPoint(
							(int) (locData.latitude * 1e6),
							(int) (locData.longitude * 1e6)));
					btn_location.setBackgroundResource(MResource.getDrawableId(
							getApplicationContext(), "is_location"));
					btn_location.setEnabled(false);
				}
			}
		});
		btn_zoomin = (Button) findViewById(MResource.getID(
				getApplicationContext(), "btn_zoomin"));
		btn_zoomout = (Button) findViewById(MResource.getID(
				getApplicationContext(), "btn_zoomout"));

		title = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "TextTitle"));
		title.setText(MResource.getStringId(getApplicationContext(),
				"whb_shop_location"));

		btn_back = (ImageView) findViewById(MResource.getID(
				getApplicationContext(), "btn_back"));
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				whb_ShopLocationActivity.this.finish();
			}
		});
		mOverlay = new MyOverlay(getResources().getDrawable(MResource.getDrawableId(getApplicationContext(), "ic_click")), mMapView);
		mMKSearch = new MKSearch();
		mMKSearch.init(cyx_MyApplication.mBMapManager, new MySearchListener());

		userInfo = CYX_Drive_SDK.getInstance().getUserInfo();

		getLocation();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mMapView != null) {
			mMapView.destroy();
			mMapView = null;
		}
		if (mLocClient != null) {
			mLocClient.stop();
			mLocClient = null;
		}
		myLocationOverlay = null;
		cyx_MyApplication.getInstance().removeActivity(this);
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			if (id == MResource.getID(getApplicationContext(), "btn_zoomin")) {
				mMapController.zoomIn();

			} else if (id == MResource.getID(getApplicationContext(),
					"btn_zoomout")) {
				mMapController.zoomOut();
			} else if (id == MResource.getID(getApplicationContext(),
					"btn_getLocation")) {
				mMapController.animateTo(new GeoPoint((int) (lat * 1e6),
						(int) (lon * 1e6)));
			} else if (id == MResource.getID(getApplicationContext(),
					"TextTitle_Right")) {
				// mMapView.refresh();
				//getLocation();
			} else if (id == MResource.getID(getApplicationContext(),
					"btn_satellite")) {
				if (!isSatellite) {
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
			if (movedCenter != loc) {
				btn_location.setEnabled(true);
				btn_location.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "btn_getlocation_bg"));
			}
		}
	}

	class MyMKMapStatusChangeListener implements MKMapStatusChangeListener {

		@Override
		public void onMapStatusChange(MKMapStatus status) {
			// TODO Auto-generated method stub
			if (status.zoom == mMapView.getMaxZoomLevel()) {
				btn_zoomin.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "ic_zoomin"));
				btn_zoomin.setEnabled(false);
			} else {
				btn_zoomin.setEnabled(true);
				btn_zoomin.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "btn_zoom_in"));
			}

			if (status.zoom == mMapView.getMinZoomLevel()) {
				btn_zoomout.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "ic_zoomout"));
				btn_zoomout.setEnabled(false);
			} else {
				btn_zoomout.setEnabled(true);
				btn_zoomout.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "btn_zoom_out"));
			}
		}

	}

	private class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo result, int arg1) {
			// TODO Auto-generated method stub
			if (result == null) {
				addr = getResources()
						.getString(
								MResource.getStringId(getApplicationContext(),
										"unknow"));
			} else {
				addr = result.strAddr;
			}

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

	private void getLocation() {
		Log.e("lxh","whb_lat1:"+whb_lat+"   whb_lon1:"+whb_lon+"   whb_content1:"+whb_content);
		double[] owgs = Coordinate.wgtobaidu(whb_lon, whb_lat);
		locData = new LocationData();
		locData.latitude = owgs[1];
		locData.longitude = owgs[0];
		GeoPoint mGeoPoint = new GeoPoint((int) (locData.latitude * 1e6), (int) (locData.longitude * 1e6));

		 OverlayItem item = new OverlayItem(mGeoPoint, "", "");
		 BitmapDrawable db = new BitmapDrawable(createBitmap("ic_click",1,"red",50));
		 item.setMarker(db);
		 mOverlay.addItem(item);
		 mMapController.animateTo(new GeoPoint(
					(int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6))); 
		mMapView.getOverlays().add(mOverlay);
		mMapView.refresh();
	}
     //一秒一个百度定位
	class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation arg0) {
			// TODO Auto-generated method stub
			locData = new LocationData();
			locData.latitude = arg0.getLatitude();
			locData.longitude = arg0.getLongitude();
//			if (isFirstLoc) {
//				isFirstLoc = false;
//				mMapController.animateTo(new GeoPoint(
//						(int) (locData.latitude * 1e6),
//						(int) (locData.longitude * 1e6)));
//			}
			myLocationOverlay.setData(locData);
			mMapView.refresh();

		}
	}
	
	public class MyOverlay extends ItemizedOverlay<OverlayItem> {
        Drawable marker;
		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
			
			marker = defaultMarker;
		}
		
		@Override
		public boolean onTap(int index) {		
			updateItem(getItem(index));
//			mMapView.refresh();
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			return false;
		}
		
		@Override
		public boolean updateItem(OverlayItem item) {
			BitmapDrawable db ;
			
				db =new BitmapDrawable(createBitmap("ic_click",1,"red",50));
			item.setMarker(db);
			return super.updateItem(item);
		}
		
	}
	
	 Bitmap newb = null;
	 /**
	  * 创建搜索结果图标
	  * @param imgName 底图图片资源
	  * @param index   数字
	  * @param textColor 字体颜色
	  * @param textSize  字体大小
	  * @return
	  */
	 private Bitmap createBitmap(String imgName,int index,
			 String textColor,int textSize) {
		 BitmapDrawable src = (BitmapDrawable) getResources().getDrawable(MResource.
				 getDrawableId(getApplicationContext(),imgName));//"unclick" ,
	     int w = src.getBitmap().getWidth();
	     int h = src.getBitmap().getHeight();
	     //create the new blank bitmap
	     newb = Bitmap.createBitmap( w, h, Config.ARGB_8888 );//创建�?��新的和SRC长度宽度�?��的位�?
	     Canvas cv = new Canvas( newb );
	     //draw src into
	     Paint paint = new Paint();  
	     paint.setColor(getResources().getColor(MResource.getColorId(getApplicationContext(), textColor)));
	     paint.setTextSize(textSize);
	     paint.setTextAlign(Align.CENTER);
	     FontMetrics fontMetrics = paint.getFontMetrics(); 
	     // 计算文字高度 
	     float fontHeight = fontMetrics.bottom - fontMetrics.top; 
	     // 计算文字baseline 
	     float textBaseY = h - (h - fontHeight) / 2 - fontMetrics.bottom; 
	     cv.drawBitmap( src.getBitmap(), 0, 0, null );//�?0�?坐标�?��画入src
	     cv.drawText(String.valueOf(index), newb.getWidth()/2+2 , textBaseY-5, paint);
	     cv.save( Canvas.ALL_SAVE_FLAG );//保存
	     cv.restore();//存储
	     src = null;
	     return newb;
	}

}
