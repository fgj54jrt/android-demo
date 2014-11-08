package com.cwits.cyx_drive_sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class cyx_CarVipHintActivity extends Activity {
	private Button btn_vip;
	private TextView title;
    private ImageView btn_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_car_vip_hint"));
		cyx_MyApplication.getInstance().addActivity(this);
		title = (TextView)findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		title.setText(MResource.getStringId(getApplicationContext(), "car_location")); // 标题
		btn_vip = (Button)findViewById(MResource.getID(getApplicationContext(), "btn_vip"));
		btn_back = (ImageView)findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		btn_vip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(cyx_CarVipHintActivity.this, cyx_BindingCXBActivity.class);
				startActivity(intent);
			}
		});
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_CarVipHintActivity.this.finish();
			}
		});
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		cyx_MyApplication.getInstance().removeActivity(this);
	}
	
	

}
