package com.cwits.cyx_drive_sdk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class cyx_RegisterUserProtocolActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_protocol"));
		cyx_MyApplication.getInstance().addActivity(this);
		TextView tvTitle = (TextView)findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
	    tvTitle.setText(MResource.getStringId(getApplicationContext(), "user_protoctol"));
	    ImageView imgBack = (ImageView) findViewById(MResource.getID(getApplicationContext(), "btn_back"));
	    imgBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			  cyx_RegisterUserProtocolActivity.this.finish();	
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		cyx_MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}

	
	
}
