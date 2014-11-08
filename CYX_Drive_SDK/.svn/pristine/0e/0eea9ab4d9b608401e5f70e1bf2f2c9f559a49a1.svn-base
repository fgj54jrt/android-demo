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

public class cyx_BindingCXBActivity extends Activity {
	private Button input_btn, scan_btn;
	private TextView title;
	private ImageView img_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_binding_cxb_layout"));
		cyx_MyApplication.getInstance().addActivity(this);
		init();
	}
	
	private void init() {
		input_btn = (Button) findViewById(MResource.getID(getApplicationContext(), "input_btn"));
		input_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(cyx_BindingCXBActivity.this, cyx_InputBindingActivity.class);
				startActivity(intent);
			}
		});
		scan_btn = (Button) findViewById(MResource.getID(getApplicationContext(), "scan_btn"));
		scan_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(cyx_BindingCXBActivity.this, cyx_CaptureBindingActivity.class);
				startActivity(intent);
			}
		});
		title = (TextView)findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		title.setText(MResource.getStringId(getApplicationContext(), "binding_cxb"));
		img_back = (ImageView)findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		img_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_BindingCXBActivity.this.finish();
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
