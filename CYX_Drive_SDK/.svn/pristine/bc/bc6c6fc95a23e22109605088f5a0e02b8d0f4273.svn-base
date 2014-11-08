package com.cwits.cyx_drive_sdk.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ConstantContext;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;

public class cyx_InputBindingActivity extends Activity {
	private EditText pn_et, sn_et;
	private Button binding_btn;
	private TextView title;
	private ImageView img_back;
	private ProgressDialog mProgressDialog; 
	private String hintContent = "";
	private UserInfo userInfo;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_input_binding_layout"));
		
		init();
	}
	
	private void init() {
		handler = new Handler();
		userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle(getResources().getString(MResource.getStringId(getApplicationContext(), "notice")));
		mProgressDialog.setMessage(getResources().getString(MResource.getStringId(getApplicationContext(), "wait")));
		mProgressDialog.setCanceledOnTouchOutside(false);
		pn_et = (EditText) findViewById(MResource.getID(getApplicationContext(), "pn_et"));
		sn_et = (EditText) findViewById(MResource.getID(getApplicationContext(), "sn_et"));
		binding_btn = (Button) findViewById(MResource.getID(getApplicationContext(), "binding_btn"));
		binding_btn.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_nonpoint"));
		binding_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mProgressDialog!=null && !mProgressDialog.isShowing())
					mProgressDialog.show();
				Connection.getInstance().sendExtData(new ExtraDataProcess().bindingCXB(userInfo.getUserID(), pn_et.getText().toString(), sn_et.getText().toString()), new RequestCallback() {

					@Override
					public void onSuccess(String bizJsonData) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObj = new JSONObject(bizJsonData);
							int resultCode = jsonObj.getInt("result");
							switch (resultCode) {
							case ConstantContext.SUCCESS:
								if(mProgressDialog!=null&&mProgressDialog.isShowing())
									mProgressDialog.dismiss();
								Toast.makeText(cyx_InputBindingActivity.this,getResources().getString(MResource.getStringId(getApplicationContext(),"binding_success" )),Toast.LENGTH_SHORT).show();
								CYX_Drive_SDK.getInstance().getUserManager().setUserFlag(userInfo.getName(), 2);	//更新用户信息
								cyx_InputBindingActivity.this.finish();
								userInfo.setFlag(2);
								break;
							//userid为空
							case ConstantContext.ERROR_1:
								hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "user_id_null"));
								break;
								//pn为空
							case ConstantContext.ERROR_2:
								hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "pn_null_error"));		
								break;
								//sn为空
							case ConstantContext.ERROR_3:
								hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "sn_null_error"));
								break;
								//用户不合法
							case ConstantContext.ERROR_4:
								hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "user_error"));
								break;
								//用户为非正式用户
							case ConstantContext.ERROR_5:
								hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "informality_user_error"));
								break;
								//用户已绑定其他设备
							case ConstantContext.ERROR_6:
								hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "already_binding_device"));
								break;
								//设备已被绑定
							case ConstantContext.ERROR_7:
								hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "already_binding"));
								break;
								// cn或sn不正确
							case ConstantContext.ERROR_8:
								hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "pn_sn_wrong_error"));
								break;
								//程序错误
							case ConstantContext.ERROR_9:
								hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "procedure_error"));
								break;
							}
							showHintContent();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					@Override
					public void onFailed(int reason) {
						switch(reason) {
						case RequestCallback.REASON_NO_NETWORK:
							hintContent += getResources().getString(
									MResource.getStringId(getApplicationContext(), "network_switch_off"));
							break;
						case RequestCallback.REASON_NO_SIGNAL:
							hintContent += getResources().getString(
									MResource.getStringId(getApplicationContext(), "network_error"));
							break;
						case RequestCallback.REASON_NOT_AUTHENTICATED:
							hintContent += getResources().getString(
									MResource.getStringId(getApplicationContext(), "no_auth"));
							break;
						case RequestCallback.REASON_TIMEOUT:
							hintContent += getResources().getString(
									MResource.getStringId(getApplicationContext(), "request_timeOut"));
							break;
						case RequestCallback.REASON_DATA_INCRECT:
							hintContent += getResources().getString(
									MResource.getStringId(getApplicationContext(), "data_increct"));
							break;
					}
					showHintContent();
					}
				});
				
			}
		});
		pn_et.addTextChangedListener(new MaxLengthWatcher());
		sn_et.addTextChangedListener(new MaxLengthWatcher());
		title = (TextView)findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		title.setText(MResource.getStringId(getApplicationContext(), "input_cxb_no"));
		img_back = (ImageView)findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		img_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_InputBindingActivity.this.finish();
			}
		});
	}

	private class MaxLengthWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (pn_et.getText().length() >= 15 && sn_et.getText().length() == 20) {
				binding_btn.setClickable(true);
				binding_btn.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "register_btn_bg"));
			} else {
				binding_btn.setClickable(false);
				binding_btn.setBackgroundResource(MResource.getDrawableId(
						getApplicationContext(), "btn_nonpoint"));
			}
		}

	}
	
	private void showHintContent() {
		if(!TextUtils.isEmpty(hintContent)) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mProgressDialog!=null&&mProgressDialog.isShowing())
						mProgressDialog.dismiss();
					Toast.makeText(cyx_InputBindingActivity.this,hintContent,Toast.LENGTH_SHORT).show(); 
					hintContent = "";
				}
			});
		}
	}
}
