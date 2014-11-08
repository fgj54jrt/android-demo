package com.cwits.cyx_drive_sdk.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MKMapStatusChangeListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cwits.cyx_drive_sdk.bean.MyPoiResult;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.userInfo.Constant;
import com.cwits.cyx_drive_sdk.util.Coordinate;
import com.cwits.cyx_drive_sdk.util.JourneyTool;

/**
 * 展示搜索结果的页面
 * @author lxh
 *
 */
public class cyx_SearchResultMapActivity extends Activity implements BDLocationListener{
	MapView mMapView = null; // 地图View
	MapController mMapController = null;
	LocationClient mLocClient;
	LocationData locData = null;
	private TextView  tv_main_title_right;
	LinearLayout search_layout;
	private String city = "深圳";   //当前城市
	boolean isFirstLoc = true;     // 是否首次定位
	Button btnZoomin, btnZoomout;          // 地图缩放按钮
//	private Button btn_search_voice;
	private ArrayList<MyPoiResult> myPoiResultList; 			//传递到搜索列表界面中的结果集合
	private Gallery myGallery;
	private MyGalleryAdapter myAdapter;
	private Button btn_traffic; //交通路况按钮
	private Button btn_getLocation;        // 定位按钮
	private boolean isShowTraffic = false;
	private ImageView img_back;
	private MKPoiResult resultList;
	ImageView poi_toLast,poi_toNext;
	int currentIndex;
	String searchName;
	FrameLayout searchLayout;
	FrameLayout searchMap_layout_bottom;
	private MyOverlay mOverlay = null; // 自定义overlay
	boolean isSelect = false ;
	MyLocationOverlay myLocationOverlay = null;
	boolean isRequest = false ;
	Handler mHandler;
	private boolean intentToList = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (cyx_MyApplication.mBMapManager == null) {
			cyx_MyApplication.mBMapManager = new BMapManager(getApplicationContext());
			cyx_MyApplication.mBMapManager.init(new cyx_MyApplication.MyGeneralListener());
		}
		CYX_Drive_SDK.initBaiduNavi(cyx_SearchResultMapActivity.this); 
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_search_result_map_layout"));
		cyx_MyApplication.getInstance().addActivity(this);
		init();
		
	}
	private void init(){
		mHandler = new Handler();
		myPoiResultList = new ArrayList<MyPoiResult>();
		city = getSharedPreferences(Constant.ADDRESS_INFO, MODE_PRIVATE).getString(Constant.ADDRESS_CITY, "深圳");
		btnZoomin = (Button) findViewById(MResource.getID(getApplicationContext(),  "btn_zoomin"));
		btnZoomout = (Button) findViewById(MResource.getID(getApplicationContext(),  "btn_zoomout"));
		btnZoomin.setOnClickListener(clickListener);
		btnZoomout.setOnClickListener(clickListener);
		mMapView = (MapView) findViewById(MResource.getID(getApplicationContext(),"mMapView_searchResult"));
		mMapController = mMapView.getController();
		mMapController.setZoom(16);
		mMapController.enableClick(true);
		mMapController.setScrollGesturesEnabled(true);
		mMapView.regMapViewListener(cyx_MyApplication.mBMapManager, new MyMKMapViewListener());
		mMapView.setEnabled(true);
		mMapView.regMapStatusChangeListener(new MyMKMapStatusChangeListener());
		SharedPreferences sh = getSharedPreferences("lastLocation",Activity.MODE_PRIVATE);
		String lat = sh.getString("lat", "");
		String lon = sh.getString("lon", "");
		if(!TextUtils.isEmpty(lon) && !TextUtils.isEmpty(lat)) {
			GeoPoint point = new GeoPoint((int)(Double.parseDouble(lat)*1e6), (int)(Double.parseDouble(lon)*1e6));
			mMapController.animateTo(point);
		}
		search_layout = (LinearLayout)findViewById(MResource.getID(getApplicationContext(), "search_search_layout"));
		search_layout.setOnClickListener(clickListener);
		btn_traffic = (Button)findViewById(MResource.getID(getApplicationContext(), "map_traffic"));
		btn_traffic.setOnClickListener(clickListener);
		btn_getLocation = (Button)findViewById(MResource.getID(getApplicationContext(), "btn_getLocation"));
		btn_getLocation.setOnClickListener(clickListener);
		tv_main_title_right = (TextView)findViewById(MResource.getID(getApplicationContext(), "tv_main_title_right"));
		tv_main_title_right.setOnClickListener(clickListener);
//		btn_search_voice = (Button) findViewById(MResource.getID(getApplicationContext(), "search_btn_search_voice"));
		myGallery =	(Gallery) findViewById(MResource.getID(getApplicationContext(), "my_grally"));
		img_back = (ImageView)findViewById(MResource.getID(getApplicationContext(), "search_img_back"));
		img_back.setOnClickListener(clickListener);
		img_back.setVisibility(View.VISIBLE);
		poi_toLast = (ImageView)findViewById(MResource.getID(cyx_SearchResultMapActivity.this, "img_poi_lastDetial"));
		poi_toNext = (ImageView)findViewById(MResource.getID(cyx_SearchResultMapActivity.this, "img_poi_nextDetial"));
		poi_toLast.setOnClickListener(clickListener);
		poi_toNext.setOnClickListener(clickListener);
		searchMap_layout_bottom = (FrameLayout)findViewById(MResource.getID(getApplicationContext(), "searchMap_layout_bottom"));
		mOverlay = new MyOverlay(getResources().getDrawable(MResource.getDrawableId(getApplicationContext(), "ic_click")), mMapView);
		myGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				currentIndex = position;
				if(position==0)
					poi_toLast.setVisibility(View.INVISIBLE);
				else
					poi_toLast.setVisibility(View.VISIBLE);
				if(position==resultList.getAllPoi().size()-1)
					poi_toNext.setVisibility(View.INVISIBLE);
				else
					poi_toNext.setVisibility(View.VISIBLE);
				mMapController.animateTo(resultList.getAllPoi().get(position).pt);

				nowIndex = position;
				mOverlay.updateItem(mOverlay.getItem(lastIndex));
				mOverlay.updateItem(mOverlay.getItem(position));
				mMapView.refresh();
				if(newb!=null&&!newb.isRecycled()){
			    	 newb.recycle();
			    	 newb = null;
			     }
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		initLocation();
		if (myLocationOverlay == null)
			myLocationOverlay = new MyLocationOverlay(mMapView);
		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		initPoi();
		if(resultList!=null&&resultList.getAllPoi().size()>0){
			searchMap_layout_bottom.setVisibility(View.VISIBLE);
			if(resultList.getAllPoi().size()==1){
				poi_toLast.setVisibility(View.INVISIBLE);
				poi_toNext.setVisibility(View.INVISIBLE);
			}
		}
	}
		// 初始化定位
	private void initLocation() {
			mLocClient = new LocationClient(getApplicationContext());
			locData = new LocationData();
			mLocClient.registerLocationListener(this);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(LocationClientOption.MIN_SCAN_SPAN); 
			option.setNeedDeviceDirect(true);
			option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
			mLocClient.setLocOption(option);
			mLocClient.start();
			mLocClient.requestLocation();
		}
	// 初始化定位图层
	 private void initLocationOverlay() {
		    mMapView.getOverlays().remove(myLocationOverlay);
			mMapView.getOverlays().add(myLocationOverlay);
			myLocationOverlay.setData(locData);
			mMapView.refresh();
		}
	private void initPoi(){
			searchName = getIntent().getStringExtra("search_name");
			MKPoiResult res = cyx_MyApplication.getMyPoiResult();
			if(res!=null){
					showPoiInfo(res);
			}
		}
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
			GeoPoint loc = new GeoPoint((int) (locData.latitude * 1e6), (int) (locData.longitude * 1e6));
			if(movedCenter!=loc){
				btn_getLocation.setEnabled(true);
				btn_getLocation.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_getlocation_bg"));
		 }
		}
	}	
    OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			Intent intent ;
			if(id==MResource.getID(getApplicationContext(), "btn_zoomin")){
				mMapController.zoomIn();
					
			}else if(id==MResource.getID(getApplicationContext(), "btn_zoomout")){
				mMapController.zoomOut();
			}
			else if(id==MResource.getID(getApplicationContext(), "btn_getLocation")){
				if(locData!=null){
					mMapController.animateTo(new GeoPoint((int) (locData.latitude * 1e6), (int) (locData.longitude * 1e6)));
				}
				isRequest = true;
			}else if(id==MResource.getID(getApplicationContext(), "search_search_layout")){
				intent= new Intent(cyx_SearchResultMapActivity.this, cyx_NaviSearchActivity.class);
				if(city==null||city.equals(""))
					city = "深圳";
				intent.putExtra("city", city);
				startActivity(intent);
				finish();
			}else if(id==MResource.getID(getApplicationContext(), "tv_main_title_right")){
				intentToList = true;
				intent = new Intent(cyx_SearchResultMapActivity.this, cyx_PoiDetailListActivity.class);
				intent.putExtra("search_name",searchName);
				intent.putExtra("mLatitude", locData.latitude);
				intent.putExtra("mLongitude", locData.longitude);
				if(myPoiResultList!=null&&myPoiResultList.size()>0){
					Bundle bundle = new Bundle();
					bundle.putSerializable("myPoiResult", myPoiResultList);
					intent.putExtras(bundle);
				}
				startActivity(intent);
				finish();
			}else if(id==MResource.getID(getApplicationContext(), "map_traffic")){
				if(isShowTraffic){
					mMapView.setTraffic(false);
					btn_traffic.setBackgroundResource((MResource.getDrawableId(getApplicationContext(), "lukuang")));
				    isShowTraffic = false;	
				}
				else{
					mMapView.setTraffic(true);
					btn_traffic.setBackgroundResource((MResource.getDrawableId(getApplicationContext(), "lukuang_press")));
				    isShowTraffic = true;	
				}
			}else if(id==MResource.getID(getApplicationContext(), "search_img_back")){
				intent = new Intent(cyx_SearchResultMapActivity.this, cyx_MainActivity.class);
				startActivity(intent);
				cyx_SearchResultMapActivity.this.finish();
			}else if(id==MResource.getID(cyx_SearchResultMapActivity.this, "img_poi_lastDetial")){
				if(currentIndex>0){
					myGallery.setSelection(currentIndex-1);
					mOverlay.onTap(currentIndex-1);
					if(newb!=null&&!newb.isRecycled()){
				    	 newb.recycle();
				    	 newb = null;
				     }
					}
			}else if(id==MResource.getID(cyx_SearchResultMapActivity.this, "img_poi_nextDetial")){
				if(currentIndex+1< resultList.getAllPoi().size()){
					myGallery.setSelection(currentIndex+1);
					mOverlay.onTap(currentIndex+1);
					if(newb!=null&&!newb.isRecycled()){
				    	 newb.recycle();
				    	 newb = null;
				     }
				}
			}
		}
    	
    };
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cyx_SearchResultMapActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		mMapView.destroy();
		if(mLocClient!=null) {
			mLocClient.stop();
			mLocClient = null;
		}
		/*if(mBMapManager!=null) {
			mBMapManager.destroy();
			mBMapManager = null;
		}*/
		SharedPreferences sh = getSharedPreferences("lastLocation",Activity.MODE_PRIVATE);
		sh.edit().putString("lat", locData.latitude+"").commit();
		sh.edit().putString("lon", locData.longitude+"").commit();
		if(mMapView!=null) {
			mMapView.destroy();
			mMapView = null;
		}
		if(newb!=null&&!newb.isRecycled()){
	    	 newb.recycle();
	    	 newb = null;
	     }
		resultList = null;
		myLocationOverlay = null;
		mOverlay = null;
		if(myPoiResultList != null) {
			myPoiResultList.clear();
			myPoiResultList = null;
		}
		//如果不是跳转至列表，则将内存中的数据清空
		if(!intentToList)
			cyx_MyApplication.setMyPoiResult(null);
		cyx_MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}
	@Override
	public void onReceiveLocation(BDLocation location) {
		// TODO Auto-generated method stub
		locData.latitude = location.getLatitude();
		locData.longitude = location.getLongitude();
		locData.direction = location.getDirection();
		if(isFirstLoc||isRequest){
			isFirstLoc = false;
			isRequest = false;
			btn_getLocation.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "is_location"));
			btn_getLocation.setEnabled(false);
			if (myLocationOverlay != null)
				myLocationOverlay
						.setLocationMode(com.baidu.mapapi.map.MyLocationOverlay.LocationMode.NORMAL);
			initLocationOverlay();
			mMapController.animateTo(new GeoPoint(
					(int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6)));
		}
		if(myLocationOverlay!=null){
			myLocationOverlay.setData(locData);
			myLocationOverlay.enableCompass();
		}
		try {
			if(mMapView!=null){
				if(mMapView.getOverlays()!=null &&mMapView.getOverlays().size()>0)
					mMapView.refresh();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	int lastIndex = 0;
	int nowIndex = 0;
	public class MyOverlay extends ItemizedOverlay<OverlayItem> {
        Drawable marker;
		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
			
			marker = defaultMarker;
		}
		
		@Override
		public boolean onTap(int index) {
			
			nowIndex = index;
			updateItem(getItem(lastIndex));
			updateItem(getItem(index));
			myGallery.setSelection(index);
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
			if(lastIndex==nowIndex){
				db = new BitmapDrawable(createBitmap("ic_click",nowIndex+1,"red",50));
				item.setMarker(db);
				
			}else {
				db = new BitmapDrawable(createBitmap("unclick",lastIndex+1,"green2",35));
				item.setMarker(db);
				lastIndex = nowIndex;
			}
			
			return super.updateItem(item);
		}
		
	}

	
	 private void showPoiInfo(MKPoiResult res){
		 isFirstLoc = false;
		 resultList = res ;
//    	  btn_search_voice.setVisibility(View.GONE);
	      tv_main_title_right.setVisibility(View.VISIBLE);
	      searchMap_layout_bottom .setVisibility(View.VISIBLE);
	      // 将poi结果显示到地图上
	      setPoiOverlay(res.getAllPoi());
	      for(int i = 0; i< res.getAllPoi().size();i++ ){
	            	mMapController.animateTo(res.getAllPoi().get(0).pt);
	            }
	       for (int i = 0; i < res.getAllPoi().size(); i++) {
					MyPoiResult result = new MyPoiResult();
					result.setEnd_latitude(res.getAllPoi().get(i).pt.getLatitudeE6()/1e6);
					result.setEnd_longitude(res.getAllPoi().get(i).pt.getLongitudeE6()/1e6);
					result.setPoiAddress(res.getAllPoi().get(i).address);
					result.setPoiName(res.getAllPoi().get(i).name);
					myPoiResultList.add(result);
				}
	       cyx_MyApplication.setMyPoiResult(res);
	       myAdapter = new MyGalleryAdapter(res,cyx_SearchResultMapActivity.this,locData,mMapView);
	       myGallery.setAdapter(myAdapter);
     }
	 private void setPoiOverlay(ArrayList<MKPoiInfo> allPoi) {
		 int count = allPoi.size();
		 GeoPoint point;
		 BitmapDrawable db = null;
		 if(allPoi!=null&&allPoi.size()>0){
			 for(int i=0; i<count; i++) {
				 point = new GeoPoint((int)(allPoi.get(i).pt.getLatitudeE6()), (int)(allPoi.get(i).pt.getLongitudeE6()));
				 OverlayItem item = new OverlayItem(point, "", "");
				 if(i==0) {
					 db = new BitmapDrawable(createBitmap("ic_click",1,"red",50));
				 } else {
					 db = new BitmapDrawable(createBitmap("unclick",i+1,"green2",35));
				 }
				 item.setMarker(db);
				 mOverlay.addItem(item);
				 item = null;
			     
			 }
			 mMapView.getOverlays().clear();
			 mMapView.getOverlays().add(mOverlay);
		     mMapView.refresh();
		     for(int i=0; i<count; i++) {
			     if(db!=null && !db.getBitmap().isRecycled()){
					 db.getBitmap().recycle();
					 db = null;
				 }
			     if(newb!=null&&!newb.isRecycled()){
			    	 newb.recycle();
			    	 newb = null;
			     }
		    }
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
	 
	 public class MyGalleryAdapter extends BaseAdapter{

			private MKPoiResult result;
			private LocationData location;
			private int DRIVING_POLICY = MKSearch.ECAR_AVOID_JAM;
			public MyGalleryAdapter(MKPoiResult result,Context context,LocationData myLocation,
					MapView mapView){
				this.result = result;
				this.location = myLocation;
			}
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return result.getAllPoi().size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return result.getAllPoi().get(position);
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				LayoutInflater inflater;
				final ViewHolder holder;
				if(convertView==null){
					inflater = LayoutInflater.from(cyx_SearchResultMapActivity.this);
					convertView = inflater.inflate(MResource.getLayoutId(cyx_SearchResultMapActivity.this, "cyx_searchresult_bottom"), null);
					holder = new ViewHolder();
					holder.tv_place_name = (TextView)convertView.findViewById(MResource.getID(cyx_SearchResultMapActivity.this, "place_name"));
					holder.tv_place_Distance = (TextView)convertView.findViewById(MResource.getID(cyx_SearchResultMapActivity.this, "place_Distance"));
					holder.tv_place_detial = (TextView)convertView.findViewById(MResource.getID(cyx_SearchResultMapActivity.this, "place_detial"));
					holder.btn_collectPlace = (LinearLayout)convertView.findViewById(MResource.getID(cyx_SearchResultMapActivity.this, "main_btn_collectPlace"));
					holder.btn_getRoutePlan =  (LinearLayout)convertView.findViewById(MResource.getID(cyx_SearchResultMapActivity.this, "main_btn_getRoutePlan"));
					convertView.setTag(holder);
				} else{
		            holder = (ViewHolder)convertView.getTag();
		        }
				if(result.getAllPoi()!=null&&result.getAllPoi().size()>0){
					double distance = JourneyTool.getDistance(location.latitude, location.longitude,
						(double)(result.getAllPoi().get(position).pt.getLatitudeE6()/1e6), 
						(double)(result.getAllPoi().get(position).pt.getLongitudeE6()/1e6));
					holder.tv_place_name.setText((position+1)+"."+result.getAllPoi().get(position).name);
					holder.tv_place_Distance.setText(distance+"km");
					holder.tv_place_detial.setText(result.getAllPoi().get(position).address);
				    holder.btn_getRoutePlan .setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							navi(result.getAllPoi().get(position).pt.getLatitudeE6()/1e6, 
									result.getAllPoi().get(position).pt.getLongitudeE6()/1e6, 
									result.getAllPoi().get(position).name);
						}
					});
			    }
				return convertView;
				
			}
		  class ViewHolder {
			   LinearLayout btn_getRoutePlan;    
			   LinearLayout btn_collectPlace;    
			   TextView tv_place_name;
			   TextView tv_place_Distance;		
			   TextView tv_place_detial;
		  }
		 private void navi(double end_latitude,double end_longitude,String name){
			  double [] myLocationPoint = Coordinate.baidutochina(location.longitude, location.latitude);
			  double [] endLocationPoint = Coordinate.baidutochina(end_longitude, end_latitude);
			  BaiduNaviManager.getInstance().launchNavigator( cyx_SearchResultMapActivity.this, 
						myLocationPoint[1],myLocationPoint[0],"我的位置", 
						endLocationPoint[1],endLocationPoint[0],
						name.toString(),
						DRIVING_POLICY, 		 						 //算路方式
						true, 									   		 //真实导航
						BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在线策略
						new OnStartNavigationListener() {				 //跳转监听
							@Override
							public void onJumpToNavigator(Bundle configParams) {
								Intent intent = new Intent(cyx_SearchResultMapActivity.this, cyx_BaiduNavigatorActivity.class);
								intent.putExtras(configParams);
						        startActivity(intent);
						        cyx_SearchResultMapActivity.this.finish();
							}
							@Override
							public void onJumpToDownloader() {
							}
						});
			}
			
		}
	 class MyMKMapStatusChangeListener implements MKMapStatusChangeListener {

			@Override
			public void onMapStatusChange(MKMapStatus status) {
				// TODO Auto-generated method stub
				if (status.zoom == mMapView.getMaxZoomLevel()){
					btnZoomin.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "ic_zoomin"));
					btnZoomin.setEnabled(false);
				  }
				else{
					btnZoomin.setEnabled(true);
					btnZoomin.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_zoom_in"));
				 }

				if (status.zoom == mMapView.getMinZoomLevel()){
					btnZoomout.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "ic_zoomout"));
					btnZoomout.setEnabled(false);
					}
				else{
					btnZoomout.setEnabled(true);
					btnZoomout.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_zoom_out"));
					}
				}

		}
}
