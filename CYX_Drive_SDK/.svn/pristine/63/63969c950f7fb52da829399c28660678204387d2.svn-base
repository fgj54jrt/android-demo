package com.cwits.cyx_drive_sdk.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.cwits.cyx_drive_sdk.db.DBManager;
import com.cwits.cyx_drive_sdk.userInfo.Constant;

public class cyx_NaviSearchActivity extends Activity{

    private ListView searchListView;
    private EditText place_edt;
    private LinearLayout history_layout;
    private ListView search_historyListView;
    private TextView clear_history;
    private Button btn_search;
    private MKSearch mSearch;
    private String city;
    private AutoCompleteAdapter autoAdapter;
    private List<String> mPlaceList ;
    private ProgressBar search_bar;
    private ImageView img_back;
//    private Button btnVoiceSearch;
    private DBManager dBManager;
    private SearchHistoryAdapter historyAdapter;
    private ArrayList<String> historyList;
    TextView tv_main_search;
    private ProgressDialog mProgressDialog = null;
    Runnable mTimeOutRunnable; // 超时处理
    private Handler mHandler;
    String search_name;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (cyx_MyApplication.mBMapManager == null) {
			cyx_MyApplication.mBMapManager = new BMapManager(getApplicationContext());
			cyx_MyApplication.mBMapManager.init(new cyx_MyApplication.MyGeneralListener());
		}
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_search_layout"));
		cyx_MyApplication.getInstance().addActivity(this);
		init();
	}
    private void init(){
    	mSearch = new MKSearch();
		mSearch.init(cyx_MyApplication.mBMapManager, new MySearchListener());
    	mHandler = new Handler();
    	dBManager = DBManager.getInstance(cyx_NaviSearchActivity.this);
		dBManager.open();
    	city = getSharedPreferences(Constant.ADDRESS_INFO, MODE_PRIVATE).getString(Constant.ADDRESS_CITY, "深圳");
    	historyList = dBManager.getAllSearchHistory();
    	searchListView = (ListView)findViewById(MResource.getID(getApplicationContext(), "List_search_now"));
    	place_edt = (EditText)findViewById(MResource.getID(getApplicationContext(), "tv_search_AutoComplete"));
    	clear_history = (TextView)findViewById(MResource.getID(getApplicationContext(), "tv_clearSearchHitory"));
    	btn_search = (Button)findViewById(MResource.getID(getApplicationContext(), "search_btn"));
    	btn_search.setVisibility(View.VISIBLE);
    	btn_search.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_nonpoint"));
    	search_historyListView = (ListView)findViewById(MResource.getID(getApplicationContext(), "List_search_history"));
        history_layout = (LinearLayout)findViewById(MResource.getID(getApplicationContext(), "search_history_layout"));
        tv_main_search = (TextView)findViewById(MResource.getID(getApplicationContext(), "tv_main_search"));
		tv_main_search .setVisibility(View.GONE);
        if(historyList!=null&&historyList.size()>0){
        	historyAdapter = new SearchHistoryAdapter(historyList);
        	search_historyListView.setAdapter(historyAdapter);
        	search_historyListView.setVisibility(View.VISIBLE);
        	history_layout.setVisibility(View.VISIBLE);
    	}
        clear_history.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dBManager.deleteSeachHistory();
				historyAdapter.notifyDataSetChanged();
				history_layout.setVisibility(View.GONE);
				search_historyListView.setVisibility(View.GONE);
			}
		});
    	mPlaceList = new ArrayList<String>();
    	search_bar = (ProgressBar) findViewById(MResource.getID(getApplicationContext(), "search_progressBar"));
    	img_back = (ImageView)findViewById(MResource.getID(getApplicationContext(), "search_img_back"));
    	img_back.setVisibility(View.VISIBLE);
    	img_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_NaviSearchActivity.this.finish();
			}
		});
    	place_edt.setVisibility(View.VISIBLE);
    	place_edt.addTextChangedListener(new MyTextWatchListener());
    	searchListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> viewList, View arg1, int position,
					long arg3) {
				String secContent = mPlaceList.get(position).toString();
				if(!TextUtils.isEmpty(secContent)) {
					if(historyList.contains(secContent)) {
						dBManager.delete(DBManager.TABLE_NAME, secContent);
					}
					dBManager.saveSeachHistory(secContent);
				}
				mSearch.poiSearchInCity(city, mPlaceList.get(position).toString());
				search_name = mPlaceList.get(position).toString();
				showDialog();
				if(mTimeOutRunnable!=null)
					mHandler.removeCallbacks(mTimeOutRunnable);
				mHandler.postDelayed(mTimeOutRunnable, 30*1000);

			}
    		
		});
    	
//    	btnVoiceSearch = (Button)findViewById(MResource.getID(getApplicationContext(), "search_btn_search_voice"));
        search_historyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				if(historyList!=null&&historyList.size()>0){
					mSearch.poiSearchInCity(city, historyList.get(position).toString());
					search_name = historyList.get(position).toString();
					System.out.println("----------search city:" + city);
					showDialog();
					if(historyList.contains(search_name)) {
						dBManager.delete(DBManager.TABLE_NAME, search_name);
						dBManager.saveSeachHistory(search_name);
					}
					if(mTimeOutRunnable!=null)
						mHandler.removeCallbacks(mTimeOutRunnable);
					mHandler.postDelayed(mTimeOutRunnable, 30*1000);
				}
			}
		});
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				InputMethodManager inputManager =
		                 (InputMethodManager)place_edt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		             inputManager.showSoftInput(place_edt, 0);
			}}, 800);
        
        mTimeOutRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mProgressDialog.dismiss();
				Toast.makeText(
						cyx_NaviSearchActivity.this, getResources().getString(
								MResource.getStringId(getApplicationContext(), "request_timeOut")), Toast.LENGTH_SHORT).show();
			}
		};
    }
    class MySearchListener implements MKSearchListener{

		@Override
		public void onGetAddrResult(MKAddrInfo info, int error) {
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult result, int arg1) {
			
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult result, int arg1) {
			
		}

		@Override
		public void onGetPoiDetailSearchResult(int poiResult, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {
			System.out.println("---------error: " + error);
			if(mProgressDialog!=null&&mProgressDialog.isShowing()){
				mProgressDialog.cancel();
			 }
	        if (error != 0 || res == null) {
	            Toast.makeText(cyx_NaviSearchActivity.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
	            search_bar.setVisibility(View.GONE);
	            if(historyList!=null&&historyList.size()>0){
		            historyList.remove(search_name);
		            historyList.add(0, search_name);
		            if(historyAdapter != null)
		            	historyAdapter.notifyDataSetChanged();
	            }
	            return;
	        }
	        if(mTimeOutRunnable!=null)
	        	mHandler.removeCallbacks(mTimeOutRunnable);
	        cyx_MyApplication.setMyPoiResult(res);
	    	Intent intent = new Intent();
			intent.setClass(cyx_NaviSearchActivity.this,cyx_SearchResultMapActivity.class);
			intent.putExtra("search_name", search_name);
			startActivity(intent);
			cyx_NaviSearchActivity.this.finish();
		}
     
		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult info, int arg1) {
			if ( info == null || info.getAllSuggestions() == null){
        		return ;
        	}
        	mPlaceList.clear();
        	for ( MKSuggestionInfo s : info.getAllSuggestions()){
        		if ( s.key != null)
        		    mPlaceList.add(s.key);
        	}
        	
        	autoAdapter = new AutoCompleteAdapter(mPlaceList);
        	autoAdapter.notifyDataSetChanged();
        	searchListView.setAdapter(autoAdapter);
        	searchListView.setVisibility(View.VISIBLE);
        	search_bar.setVisibility(View.GONE);
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
    class MyTextWatchListener implements TextWatcher{

		@Override
		public void afterTextChanged(Editable s) {
		}
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if(!TextUtils.isEmpty(city)&&s.length()>=1){
				mSearch.suggestionSearch(s.toString(), city);
				System.out.println("-----------suggestionSearch city:"+city);
				search_bar.setVisibility(View.VISIBLE);
				btn_search.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_search_bg"));
				btn_search.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String secContent = place_edt.getText().toString();
						if(!TextUtils.isEmpty(secContent)) {
							if(historyList.contains(secContent)) {
								dBManager.delete(DBManager.TABLE_NAME, secContent);
							}
							dBManager.saveSeachHistory(secContent);
						}
						mSearch.poiSearchInCity(city, place_edt.getText().toString());
						search_name = place_edt.getText().toString();
						showDialog();
						if(mTimeOutRunnable!=null)
							mHandler.removeCallbacks(mTimeOutRunnable);
						mHandler.postDelayed(mTimeOutRunnable, 30*1000);
						
					}
				});
			}else if(s.length()<=0){
				search_bar.setVisibility(View.GONE);
				btn_search.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_nonpoint"));
				btn_search.setOnClickListener(null);
			}
		}
    	
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cyx_NaviSearchActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mTimeOutRunnable!=null)
			mHandler.removeCallbacks(mTimeOutRunnable);
		dBManager.close();
		if(mSearch!=null){
			mSearch.destory();
			mSearch = null;
			}
		if(mProgressDialog!=null&&mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
			mProgressDialog = null;
			}
		cyx_MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
     //自动补全列表Adapter
	class AutoCompleteAdapter extends BaseAdapter{

		List<String> placeList;
		public AutoCompleteAdapter(List<String> placeList){
			this.placeList = placeList;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return placeList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return placeList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			LayoutInflater inflater;
			if(convertView==null){
				inflater = LayoutInflater.from(cyx_NaviSearchActivity.this);
				convertView = inflater.inflate(MResource.getLayoutId(getApplicationContext(), "cyx_search_list_item"), null);
				holder = new ViewHolder();
				holder.tv_place = (TextView)convertView.findViewById(MResource.getID(getApplicationContext(), "tv_search_textView"));
				convertView.setTag(holder);
			} else{
	            holder = (ViewHolder)convertView.getTag();
	        }
			holder.tv_place.setText(placeList.get(position));
			return convertView;
		}
		class ViewHolder{
			TextView tv_place;
		}
	}
    class SearchHistoryAdapter extends BaseAdapter{

		List<String> placeList;
		public SearchHistoryAdapter(List<String> placeList){
			this.placeList = placeList;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return placeList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return placeList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			LayoutInflater inflater;
			if(convertView==null){
				inflater = LayoutInflater.from(cyx_NaviSearchActivity.this);
				convertView = inflater.inflate(MResource.getLayoutId(getApplicationContext(), "cyx_search_list_item"), null);
				holder = new ViewHolder();
				holder.tv_place = (TextView)convertView.findViewById(MResource.getID(getApplicationContext(), "tv_search_textView"));
				holder.imageView = (ImageView)convertView.findViewById(MResource.getID(getApplicationContext(), "search_img_left"));
				convertView.setTag(holder);
			} else{
	            holder = (ViewHolder)convertView.getTag();
	        }
			holder.tv_place.setText(placeList.get(position));
			holder.imageView.setImageResource(
					MResource.getDrawableId(getApplicationContext(), "ic_time"));
			return convertView;
		}
		class ViewHolder{
			TextView tv_place;
			ImageView imageView;
		}
	}
    private void showDialog(){
    	if(mProgressDialog!=null&&mProgressDialog.isShowing()){
			mProgressDialog.cancel();
			mProgressDialog = null ;
		}
		mProgressDialog = new ProgressDialog(cyx_NaviSearchActivity.this);
		mProgressDialog.setTitle(getString(MResource.getStringId(getApplicationContext(), "notice")));
		mProgressDialog.setMessage(getString(MResource.getStringId(getApplicationContext(), "search_ing")));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();
    }
}
