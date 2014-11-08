package com.cwits.cyx_drive_sdk.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.cwits.cyx_drive_sdk.bean.MyPoiResult;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.util.Coordinate;
import com.cwits.cyx_drive_sdk.util.JourneyTool;

public class cyx_PoiDetailListActivity extends Activity{
    private TextView tv_title;
    private TextView tv_Map;
    private ImageView img_back;
    private ListView detial_list;
    private double mLongitude,mLatitude;
    ArrayList<MyPoiResult> resultList;
    MyAdapter adapter ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		CYX_Drive_SDK.initBaiduNavi(cyx_PoiDetailListActivity.this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		cyx_MyApplication.getInstance().addActivity(this);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_poilist_layout"));
		init();
	}
    private void init(){
    	mLongitude = getIntent().getDoubleExtra("mLongitude", 0);
    	mLatitude = getIntent().getDoubleExtra("mLatitude",0);
    	tv_title = (TextView) findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
    	tv_title.setText(getIntent().getStringExtra("search_name"));
    	tv_Map = (TextView) findViewById(MResource.getID(getApplicationContext(), "TextTitle_Right"));
    	tv_Map.setVisibility(View.VISIBLE);
    	img_back = (ImageView) findViewById(MResource.getID(getApplicationContext(), "btn_back"));
    	img_back.setVisibility(View.INVISIBLE);
    	detial_list = (ListView) findViewById(MResource.getID(getApplicationContext(), "poi_result_listView"));
    	tv_Map.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initentToMain();
			}
		});
    	resultList = (ArrayList<MyPoiResult>)getIntent().getSerializableExtra("myPoiResult");
    	if(resultList!=null&&resultList.size()>0){
    		adapter = new MyAdapter(resultList);
    		detial_list.setAdapter(adapter);
    	}
    }
	
    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
    	initentToMain();
		super.onBackPressed();
	}
	private void initentToMain(){
    	Intent intent = new Intent(cyx_PoiDetailListActivity.this, cyx_SearchResultMapActivity.class);
    	intent.putExtra("search_name", getIntent().getStringExtra("search_name"));
    	startActivity(intent);
    	this.finish();
    }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(resultList!=null) {
			resultList.clear();
			resultList = null;
		}
		cyx_MyApplication.getInstance().removeActivity(this);
		
		super.onDestroy();
	}
    class MyAdapter extends BaseAdapter {
        ArrayList<MyPoiResult> myResult;
        public MyAdapter(ArrayList<MyPoiResult> resultList){
        	this.myResult = resultList;
        }
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return myResult.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return myResult.get(position);
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
			ViewHolder holder;
			if(convertView==null){
				inflater = LayoutInflater.from(cyx_PoiDetailListActivity.this);
				convertView = inflater.inflate(MResource.getLayoutId(getApplicationContext(), "poi_list_item"), null);
				holder = new ViewHolder();
				holder.tv_placeName = (TextView)convertView.findViewById(MResource.getID(getApplicationContext(), "poi_list_PoiName"));
				holder.tv_distance = (TextView)convertView.findViewById(MResource.getID(getApplicationContext(), "poi_place_Distance"));
				holder.tv_place_detial = (TextView)convertView.findViewById(MResource.getID(getApplicationContext(), "poi_list_PoiAddress"));
				holder.btn_navi = (LinearLayout)convertView.findViewById(MResource.getID(getApplicationContext(), "poiList_btn_navi"));
				convertView.setTag(holder);
			} else{
	            holder = (ViewHolder)convertView.getTag();
	        }
			holder.tv_placeName.setText((position+1)+"."+myResult.get(position).getPoiName());
			holder.tv_place_detial.setText(myResult.get(position).getPoiAddress());
			final double end_latitude = myResult.get(position).getEnd_latitude();
			final double end_longitude = myResult.get(position).getEnd_longitude();
			double distance = JourneyTool.getDistance(mLatitude, mLongitude, end_latitude, end_longitude);
			holder.tv_distance.setText(distance+"km");
			holder.btn_navi.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					navi(end_latitude, end_longitude,
							myResult.get(position).getPoiName());
				}
			});
			return convertView;
		}
    	class ViewHolder{
    		TextView tv_placeName;
    		TextView tv_distance;
    		TextView tv_place_detial;
    		LinearLayout btn_navi;
    	}
    }
	private void navi(double end_latitude,double end_longitude,String name){
		double [] myLocationPoint = Coordinate.baidutochina(mLongitude, mLatitude);
		double [] endLocationPoint = Coordinate.baidutochina(end_longitude, end_latitude);
		BaiduNaviManager.getInstance().launchNavigator(cyx_PoiDetailListActivity.this, 
				myLocationPoint[1],myLocationPoint[0],"我的位置", 
				endLocationPoint[1],endLocationPoint[0],
				name.toString(),
				NE_RoutePlan_Mode.ROUTE_PLAN_MOD_AVOID_TAFFICJAM, 		 						 //算路方式
				true, 									   		 //真实导航
				BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策�?
				new OnStartNavigationListener() {				 //跳转监听
					
					@Override
					public void onJumpToNavigator(Bundle configParams) {
						Intent intent = new Intent(cyx_PoiDetailListActivity.this, cyx_BaiduNavigatorActivity.class);
						intent.putExtras(configParams);
				        startActivity(intent);
				        cyx_PoiDetailListActivity.this.finish();
					}
					@Override
					public void onJumpToDownloader() {
					}
				});
	}
}
