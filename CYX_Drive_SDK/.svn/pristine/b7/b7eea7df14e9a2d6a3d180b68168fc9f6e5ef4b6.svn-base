package com.cwits.cyx_drive_sdk.ui;

import java.util.Calendar;

import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.userInfo.IExternalInterfaceAR;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

public class cyx_AboutUsActivity extends Activity {
	private static final String TAG = "AboutUsActivity";
	private TextView appVersion;
	private TextView protocol;
	private TextView copyrigthYear;
	private TextView checkUpdata;
	private int mYear, mMonth, mDay;
	private MDatePickerDialog mDatePickerDialog;
    private ImageView image_back;
    private TextView tv_title,agreement;
    private IExternalInterfaceAR ExternalInterfaceAR;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ExternalInterfaceAR = CYX_Drive_SDK.getInstance().getExternalInterface();
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_about_us_layout"));
		cyx_MyApplication.getInstance().addActivity(this);
		appVersion = (TextView) findViewById(MResource.getID(getApplicationContext(), "version"));
		appVersion.setText(getResources().getString(MResource.getStringId(getApplicationContext(), "app_name")) + " "+getIntent().getExtras().getString("version"));
		checkUpdata = (TextView) findViewById(MResource.getID(getApplicationContext(), "check_update"));
		//检查更新
		checkUpdata.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (ExternalInterfaceAR != null)
							ExternalInterfaceAR.CheckUpdate();
					}
				});
		image_back = (ImageView)findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		tv_title = (TextView)findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		tv_title.setText(MResource.getStringId(getApplicationContext(), "about"));
		protocol=(TextView) findViewById(MResource.getID(getApplicationContext(), "version"));
		
		protocol.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		image_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_AboutUsActivity.this.finish();
			}
		});
		agreement = (TextView)findViewById(MResource.getID(getApplicationContext(), "agreement"));
		agreement.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(cyx_AboutUsActivity.this,
						cyx_webview_about.class);
				startActivity(intent);
				
			}
		});
		copyrigthYear = (TextView) findViewById(MResource.getID(getApplicationContext(), "copuright_yeah"));
		copyrigthYear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Calendar calendar = Calendar.getInstance();
				int cYear, cMonth, cDay;
				if(cyx_MyApplication.year != 0) {
					cYear = cyx_MyApplication.year;
					cMonth = cyx_MyApplication.month;
					cDay = cyx_MyApplication.day;
				} else {
					cYear = calendar.get(Calendar.YEAR);
					cMonth = calendar.get(Calendar.MONTH);
					cDay = calendar.get(Calendar.DAY_OF_MONTH);
				}
				mYear = cYear;
				mMonth = cMonth;
				mDay = cDay;
				mDatePickerDialog = new MDatePickerDialog(cyx_AboutUsActivity.this,
						new OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								// TODO Auto-generated method stub

							}
						}, cYear, cMonth, cDay);

				mDatePickerDialog.setCancelable(true);
				mDatePickerDialog.setCanceledOnTouchOutside(true);
				mDatePickerDialog.setTitle(getResources().getString(
						MResource.getStringId(getApplicationContext(), "datePickerDialog_title")));
				mDatePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,
						getResources().getString(MResource.getStringId(getApplicationContext(), "ensure")),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Log.d("Picker", "ensure!");
								cyx_MyApplication.year = mYear;
								cyx_MyApplication.month = mMonth;
								cyx_MyApplication.day =  mDay;
								if (mYear == 2006 && mMonth == 6 && mDay == 1) {
									cyx_MyApplication.isLogMode = true;	//标志位已�?��日志模式
									Log.d(TAG, "已开启日志功能");
									AlertDialog.Builder builder = new AlertDialog.Builder(cyx_AboutUsActivity.this);
									final AlertDialog alertDialog = builder.create();
									alertDialog.setTitle("提示");
									alertDialog.setMessage("已启动日志功能！");
									alertDialog.show();
									Handler handler = new Handler();
									handler.postDelayed((new Runnable() {
										
										@Override
										public void run() {
											// TODO Auto-generated method stub
											alertDialog.dismiss();
										}
									}), 2000);
								}
							}
						});
				mDatePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
						getResources().getString(MResource.getStringId(getApplicationContext(), "cancel")),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Log.d("Picker", "Cancel!");
								mDatePickerDialog.dismiss();
							}
						});
				mDatePickerDialog.show();
			}
		});
	}

	private class MDatePickerDialog extends DatePickerDialog {

		public MDatePickerDialog(Context context, OnDateSetListener callBack,
				int year, int monthOfYear, int dayOfMonth) {

			super(context, callBack, year, monthOfYear, dayOfMonth);
		}

		@Override
		public void onDateChanged(DatePicker view, int year, int month, int day) {
			super.onDateChanged(view, year, month, day);
			mYear = year;
			mMonth = month;
			mDay = day;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		cyx_MyApplication.getInstance().removeActivity(this);
	}
	
	

}
