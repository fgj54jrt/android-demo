package com.cwits.cyx_drive_sdk.ui;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;

public class cyx_TrafficReportHistory extends Activity{
	private ListView list;
	private ViewHolder holder;
	private MyListAdapter adapter;
	private int page_now = 1;// 当前�?
	private int page_size = 3;// 每页条数
	private int itemNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_traffic_report_history"));
		list = (ListView) findViewById(MResource.getID(getApplicationContext(),
				"report_history_list"));
		adapter = new MyListAdapter(this);
		list.setAdapter(adapter);
		cyx_MyApplication.getInstance().addActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		cyx_MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}

	public final class ViewHolder {
		public TextView text1, text2, text3, text4;
	}

	private class MyListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyListAdapter(Context context) {

			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemNumber;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// if (convertView == null) {

			holder = new ViewHolder();

			convertView = mInflater.inflate(MResource.getLayoutId(getApplicationContext(), "cyx_traffic_history_item"),
					null);
			holder.text1 = (TextView) convertView.findViewById(MResource.getID(
					getApplicationContext(), "report_time"));
			holder.text2 = (TextView) convertView.findViewById(MResource.getID(
					getApplicationContext(), "accident_type"));
			holder.text3 = (TextView) convertView.findViewById(MResource.getID(
					getApplicationContext(), "report_lon"));
			convertView.setTag(holder);
			holder.text4 = (TextView) convertView.findViewById(MResource.getID(
					getApplicationContext(), "report_lat"));
			convertView.setTag(holder);

			// }else {
			//
			// holder = (ViewHolder)convertView.getTag();
			// }
			//

			// holder.text1.setText("数据"+position);
			// holder.text2.setText("数据"+position);
			// holder.text3.setText("数据"+position);

			return convertView;
		}
	}


	
   JSONObject jsonObj;
   private void request(){
	   String id = CYX_Drive_SDK.getInstance().getUserInfo().getUserID();
	   
	   CYX_Drive_SDK.getInstance().getConnection().sendExtData(
			   new ExtraDataProcess().getTRHistoryData(id, page_now, page_size),
			   new RequestCallback(){
				@Override
				public void onSuccess(String bizJsonData) {
				
				}
				@Override
				public void onFailed(int reason) {
					if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_TrafficReportHistory.this.getClass().getName())) {
					Toast.makeText(cyx_TrafficReportHistory.this, "查询失败", Toast.LENGTH_SHORT).show();
					}
				}
			   });
   }
}
