package com.cwits.cyx_drive_sdk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class cyx_BindingFeedbackActivity extends Activity {
	private TextView title;
	private ImageView btn_back;
	private CheckBox trouble_cb, lost_cb, data_inac_cb, oper_inco_cb;
	private Button ensure_btn;
	private EditText other_et;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		cyx_MyApplication.getInstance().addActivity(this);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_binding_feedback"));
		init();
	}

	private void init() {
		title = (TextView) findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		title.setText(getResources().getString(MResource.getStringId(getApplicationContext(), "user_feedback")));
		btn_back = (ImageView) findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_BindingFeedbackActivity.this.finish();
			}
		});
		trouble_cb = (CheckBox) findViewById(MResource.getID(getApplicationContext(), "trouble_cb"));
		trouble_cb.setChecked(false);
		lost_cb = (CheckBox) findViewById(MResource.getID(getApplicationContext(), "lost_cb"));
		lost_cb.setChecked(false);
		data_inac_cb = (CheckBox) findViewById(MResource.getID(getApplicationContext(), "data_inac_cb"));
		data_inac_cb.setChecked(false);
		oper_inco_cb = (CheckBox) findViewById(MResource.getID(getApplicationContext(), "oper_inco_cb"));
		oper_inco_cb.setChecked(false);
		other_et = (EditText) findViewById(MResource.getID(getApplicationContext(), "other_et"));
		ensure_btn = (Button) findViewById(MResource.getID(getApplicationContext(), "ensure_btn"));
		ensure_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//此处调用ar接口
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
