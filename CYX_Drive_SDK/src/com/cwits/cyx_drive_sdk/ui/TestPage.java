package com.cwits.cyx_drive_sdk.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cwits.cyx_drive_sdk.R;
import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;

public class TestPage extends Activity {
	private Button navigation;
	private Button register;
	private Button login;
	private Button forgetpw;
	private Button myWallet;
	private Button myCar;
	private Button settings;
    private Button tripData;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(MResource.getLayoutId(getApplicationContext(), "test"));

//		IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//		registerReceiver(netChgReceiver, intentFilter);
		CYX_Drive_SDK.getInstance().Initialize(this,1);
		cyx_MyApplication.getInstance().addActivity(this);

		navigation = (Button) findViewById(MResource.getID(
				getApplicationContext(), "navigation"));
		register = (Button) findViewById(MResource.getID(
				getApplicationContext(), "register"));
		login = (Button) findViewById(MResource.getID(
				getApplicationContext(), "login"));
		forgetpw = (Button) findViewById(MResource.getID(
				getApplicationContext(), "forgetpw"));
		myWallet = (Button) findViewById(MResource.getID(
				getApplicationContext(), "myWallet"));
		myCar = (Button) findViewById(MResource.getID(
				getApplicationContext(), "myCar"));
		settings = (Button) findViewById(MResource.getID(
				getApplicationContext(), "settings"));
        tripData = (Button) findViewById(MResource.getID(
				getApplicationContext(), "tripData"));
		navigation.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(TestPage.this, cyx_MainActivity.class);
//				startActivity(intent);
				CYX_Drive_SDK.getInstance().startToDrive(1);
			}
		});

		register.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TestPage.this, cyx_RegisterActivity.class);
				startActivity(intent);
			}
		});

		login.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TestPage.this, cyx_LoginActivity.class);
				startActivity(intent);
			}
		});

		forgetpw.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                 CYX_Drive_SDK.getInstance().startToWHB();
			}
		});

		myWallet.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent inten = new Intent(TestPage.this, cyx_CarLocationActivity.class);
				startActivity(inten);
			}
		});

		myCar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent inten = new Intent(TestPage.this, cyx_CarFortifyActivity.class);
				startActivity(inten);
			}
		});

		settings.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TestPage.this, cyx_SettingsActivity.class);
				startActivity(intent);
			}
		});
		tripData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				String jsonStr = 
//						       "{ 'rid':'1',"+
//							   "   'score':100,"+
//							     " 'got':0}";
//				CYX_Drive_SDK.getInstance().startToTripDetail(jsonStr);
				CYX_Drive_SDK.getInstance().startToHistoryTrip(1);
			}
		});
		findViewById(MResource.getID(
				getApplicationContext(), "choose_service")).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(TestPage.this, ServerChoiceActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//cyx_MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
	
}
