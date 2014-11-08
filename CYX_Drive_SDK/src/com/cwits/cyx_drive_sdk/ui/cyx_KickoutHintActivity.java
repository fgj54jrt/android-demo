package com.cwits.cyx_drive_sdk.ui;

import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class cyx_KickoutHintActivity extends Activity {
	private TextView titleView, messageView, btnOk, btnCancel, cyx_phonenumber;
	private View line;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_dialog"));
		cyx_MyApplication.getInstance().addActivity(this);
		titleView = (TextView)findViewById(MResource.getID(getApplicationContext(), "cyx_dialog_title"));
		messageView = (TextView)findViewById(MResource.getID(getApplicationContext(),"cyx_dialog_content"));
		cyx_phonenumber = (TextView)findViewById(MResource.getID(getApplicationContext(),"cyx_phonenumber"));
        btnCancel = (TextView)findViewById(MResource.getID(getApplicationContext(), "cyx_dialog_ok"));
        btnOk = (TextView)findViewById(MResource.getID(getApplicationContext(), "cyx_dialog_cancel"));
        line = findViewById(MResource.getID(getApplicationContext(), "cyx_dialog_line"));
        init();
	}
	
	
	private void init() {
		titleView.setText(getString(MResource.getStringId(getApplicationContext(), "notice")));
		titleView.setTextSize(18);
		titleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		line.setVisibility(View.VISIBLE);
		messageView.setText(getString(MResource.getStringId(getApplicationContext(), "kickout_tips")));
		cyx_phonenumber.setVisibility(View.GONE);
		btnOk.setText(getString(MResource.getStringId(
				getApplicationContext(), "relogin")));
		btnCancel.setText(getString(MResource.getStringId(
				getApplicationContext(), "exit_login")));
		btnOk.setVisibility(View.VISIBLE);
		btnOk.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		btnCancel.setVisibility(View.VISIBLE);
		btnCancel.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		
		
		btnOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CYX_Drive_SDK.getInstance().getConnection().setStartType(Connection.START_TYPE_CONNECT_ONLY);
				CYX_Drive_SDK.getInstance().getConnection().stop();
				Intent intent = new Intent(cyx_KickoutHintActivity.this, cyx_LoginActivity.class);
				intent.putExtra("autoLogin", true);
				startActivity(intent);
				cyx_KickoutHintActivity.this.finish();
			}
		});
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CYX_Drive_SDK.getInstance().getConnection().setStartType(Connection.START_TYPE_CONNECT_ONLY);
				CYX_Drive_SDK.getInstance().getConnection().stop();
				Intent intent = new Intent(cyx_KickoutHintActivity.this, cyx_LoginActivity.class);
				startActivity(intent);
				cyx_KickoutHintActivity.this.finish();
			}
		});
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		cyx_MyApplication.getInstance().removeActivity(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

}
