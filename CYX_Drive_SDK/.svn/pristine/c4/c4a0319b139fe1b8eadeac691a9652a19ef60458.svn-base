package com.cwits.cyx_drive_sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 非VIP车辆防护页面
 * @author lxh
 *
 */
public class cyx_HintCarFortifyActivity extends Activity{

	private Button btn_vip;
	private TextView title;
    private ImageView btn_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		cyx_MyApplication.getInstance().addActivity(this);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_car_vip_hint"));
		title = (TextView)findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		title.setText(MResource.getStringId(getApplicationContext(), "car_fortify"));
		btn_vip = (Button)findViewById(MResource.getID(getApplicationContext(), "btn_vip"));
		btn_back = (ImageView)findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		btn_vip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(cyx_HintCarFortifyActivity.this, cyx_BindingCXBActivity.class);
				startActivity(intent);
			}
		});
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_HintCarFortifyActivity.this.finish();
			}
		});
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		this.finish();
		super.onBackPressed();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		cyx_MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
	
}
