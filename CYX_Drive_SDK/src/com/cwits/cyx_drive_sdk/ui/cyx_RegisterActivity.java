package com.cwits.cyx_drive_sdk.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ConstantContext;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.userInfo.Constant;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;

public class cyx_RegisterActivity extends Activity{

	private Button btn_next;
	private EditText edt_phone_Number;
	private CheckBox mcheckBox;
	private TextView tv_UserProtocol;
	private TextView title;
	private ImageView img_back;
	private TextView input_phoneNum_notice;
	private  SharedPreferences adrpre ;
	ProgressDialog mProgressDialog; 
	Handler mHandler;
	cyx_CustomAlertDialog mDialog;
	String subPhone1, subPhone2;
	private Runnable listenerRunable;
	private String hintContent = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_regist"));
		cyx_MyApplication.getInstance().addActivity(this);
		adrpre = this.getSharedPreferences(Constant.ADDRESS_INFO, Context.MODE_PRIVATE);
		init();
	}
	private void init(){
		mHandler = new Handler();
		btn_next = (Button) findViewById(MResource.getID(getApplicationContext(), "next_btn"));
		btn_next.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_nonpoint"));
		edt_phone_Number = (EditText) findViewById(MResource.getID(getApplicationContext(), "register_phoneNo"));
		edt_phone_Number.addTextChangedListener(new MaxLengthWatcher());
		
		mcheckBox = (CheckBox) findViewById(MResource.getID(getApplicationContext(), "register_checkbox"));
		mcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
            @Override 
            public void onCheckedChanged(CompoundButton buttonView, 
                    boolean isChecked) { 
                // TODO Auto-generated method stub 
            	Editable editable = edt_phone_Number.getText();  
   		        int len = editable.length(); 
   		     
                if(len==11&&isChecked){
       				 btn_next.setClickable(true);
       				 btn_next.setBackgroundResource(MResource.
       						 getDrawableId(getApplicationContext(), "register_btn_bg"));
       			 } else {
       				 btn_next.setClickable(false);
       				 btn_next.setBackgroundResource(MResource.
       						 getDrawableId(getApplicationContext(), "btn_nonpoint"));
       			 }
               
            } 
        }); 
		tv_UserProtocol = (TextView) findViewById(MResource.getID(getApplicationContext(), "protocol_tv"));
		title = (TextView) findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		img_back = (ImageView) findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		title.setText(MResource.getStringId(getApplicationContext(), "cyx_register"));
		input_phoneNum_notice = (TextView)findViewById(MResource.getID(getApplicationContext(), "input_phoneNum_notice"));
		edt_phone_Number.setOnFocusChangeListener( new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					input_phoneNum_notice.setVisibility(View.VISIBLE);
				}else{
					input_phoneNum_notice.setVisibility(View.GONE);
				}
			}
		});
		mProgressDialog = new ProgressDialog(cyx_RegisterActivity.this);
		mProgressDialog.setTitle(getResources().getString(MResource.getStringId(getApplicationContext(), "notice")));
		mProgressDialog.setMessage(getResources().getString(MResource.getStringId(getApplicationContext(), "wait")));
		mProgressDialog.setCanceledOnTouchOutside(false);
		//下一步
		btn_next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mcheckBox.isChecked()){
					if(!edt_phone_Number.getText().toString().trim().equals("")){
						if(edt_phone_Number.length()!=11){
							Toast.makeText(cyx_RegisterActivity.this,getResources().getString(MResource.getStringId(getApplicationContext(), "input_error_phone")), Toast.LENGTH_SHORT).show();
						}else if(!isMobileNO(edt_phone_Number.getText().toString().trim())){
							Toast.makeText(cyx_RegisterActivity.this,getResources().getString(MResource.getStringId(getApplicationContext(), "format_error")), Toast.LENGTH_SHORT).show();
						}else{
							createDialog();
						}
					}else{
						return;
					}
				}else {
					return;
				}
				
		}
		});
		tv_UserProtocol.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(cyx_RegisterActivity.this, cyx_webview_about.class);
				startActivity(intent);
			}
		});
		img_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_RegisterActivity.this.finish();
			}
		});
		
	}
	//自定义对话框
    private void createDialog(){
    	subPhone1 = edt_phone_Number.getText().toString().substring(0, 3);
    	subPhone2 = edt_phone_Number.getText().toString().substring(7,11);
    	if(mDialog!=null&&mDialog.isShowing()){
    		mDialog.dismiss();
    		mDialog=null;
    	}
    	mDialog = new cyx_CustomAlertDialog(cyx_RegisterActivity.this);
		mDialog.setTitle(MResource.getStringId(getApplicationContext(), "ensure_phone_no"));
		mDialog.setMessage(getResources().getString(MResource.getStringId(getApplicationContext(), "send_sms_to")));
		mDialog.setNumber(subPhone1+"****" + subPhone2);
		mDialog.showLine();
		mDialog.setNegativeButton(getResources().getString
				(MResource.getStringId(getApplicationContext(), "cancel")), new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDialog.dismiss();
						mDialog = null ;
					}
				});
		mDialog.setPositiveButton( new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mDialog.dismiss();
						mProgressDialog.show();
						getTemp();
					}
				});
    }
    
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		if(mDialog!=null){
			if(mDialog.isShowing())
				mDialog.dismiss();
			mDialog = null;
		}
		if(mProgressDialog!=null&&mProgressDialog.isShowing())
			mProgressDialog.cancel();
		mProgressDialog = null;
		if(listenerRunable!=null)
			mHandler.removeCallbacks(listenerRunable);
		cyx_MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
	public class MaxLengthWatcher implements TextWatcher {

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
			 Editable editable = edt_phone_Number.getText();  
		     int len = editable.length(); 
			 if((len==11)&&mcheckBox.isChecked()){
				 btn_next.setClickable(true);
				 btn_next.setBackgroundResource(MResource.
						 getDrawableId(getApplicationContext(), "register_btn_bg"));
			 } else {
				 btn_next.setClickable(false);
				 btn_next.setBackgroundResource(MResource.
						 getDrawableId(getApplicationContext(), "btn_nonpoint"));
			 }
		}
		
	}
	
	//发送验证码
	public void register(){
		UserInfo us = CYX_Drive_SDK.getInstance().getUserInfo();
		CYX_Drive_SDK.getInstance().getConnection().sendExtData(new ExtraDataProcess().getPhoneVerifyData(ConstantContext.VERIFY_REGISTER, us.getTempID(), 
				edt_phone_Number.getText().toString()), new RequestCallback(){
			@Override
			public void onSuccess(String bizJsonData) {
				if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_RegisterActivity.this.getClass().getName())) {
				if(mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				JSONObject jsonObj;
				try {
					jsonObj = new JSONObject(bizJsonData);
					switch(jsonObj.getInt("result")) {
					case ConstantContext.SUCCESS:
						mProgressDialog.dismiss();
						if(listenerRunable!=null){
							mHandler.removeCallbacks(listenerRunable);
							listenerRunable = null;
							}
						listenerRunable = new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(cyx_RegisterActivity.this,
										getResources().getString(MResource.getStringId(getApplicationContext(),"sent_checkCode" )),Toast.LENGTH_SHORT).show(); 
								Intent intent = new Intent(cyx_RegisterActivity.this, cyx_RegisterCheckCodeActivity.class);
								intent.putExtra("phoneNum", edt_phone_Number.getText().toString());	
								startActivity(intent);
								cyx_RegisterActivity.this.finish();
							}
						};
						mHandler.post(listenerRunable);
						
				    break;
					 case ConstantContext.ERROR_1:
					    	if(listenerRunable!=null){
								mHandler.removeCallbacks(listenerRunable);
								listenerRunable = null;
								}
					    	listenerRunable = new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									Toast.makeText(cyx_RegisterActivity.this,
							    			getResources().getString(MResource.getStringId(getApplicationContext(), 
							    					"user_id_null"))
							    			, Toast.LENGTH_SHORT).show();	
								}
							};
					    	mHandler.post(listenerRunable);
					    break;
				    case ConstantContext.ERROR_2:
				    	if(listenerRunable!=null){
							mHandler.removeCallbacks(listenerRunable);
							listenerRunable = null;
							}
				    	listenerRunable = new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(cyx_RegisterActivity.this,
						    			getResources().getString(MResource.getStringId(getApplicationContext(), 
						    					"null_phonenumber"))
						    			, Toast.LENGTH_SHORT).show();	
							}
						};
				    	mHandler.post(listenerRunable);
				    break;
				    case ConstantContext.ERROR_3:
				    	if(listenerRunable!=null){
							mHandler.removeCallbacks(listenerRunable);
							listenerRunable = null;
							}
				    	listenerRunable = new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(cyx_RegisterActivity.this,
						    			getResources().getString(MResource.getStringId(getApplicationContext(), 
						    					"apply_too_often"))
						    			, Toast.LENGTH_SHORT).show();	
							}
						};
				    	mHandler.post(listenerRunable);
				    break;
				    case ConstantContext.ERROR_4:
				    	if(listenerRunable!=null){
							mHandler.removeCallbacks(listenerRunable);
							listenerRunable = null;
							}
				    	listenerRunable = new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(cyx_RegisterActivity.this,
						    			getResources().getString(MResource.getStringId(getApplicationContext(), 
						    					"send_sms_error"))
						    			, Toast.LENGTH_SHORT).show();	
							}
						};
				    	mHandler.post(listenerRunable);
				    break;
				    case ConstantContext.ERROR_5:
				    	if(listenerRunable!=null){
							mHandler.removeCallbacks(listenerRunable);
							listenerRunable = null;
							}
				    	listenerRunable = new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(cyx_RegisterActivity.this,
						    			getResources().getString(MResource.getStringId(getApplicationContext(), 
						    					"procedure_error"))
						    			, Toast.LENGTH_SHORT).show();	
							}
						};
				    	mHandler.post(listenerRunable);
				    break;
				    case ConstantContext.ERROR_6:
				    	if(listenerRunable!=null){
							mHandler.removeCallbacks(listenerRunable);
							listenerRunable = null;
							}
				    	listenerRunable = new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(cyx_RegisterActivity.this,
						    			getResources().getString(MResource.getStringId(getApplicationContext(), 
						    					"format_error"))
						    			, Toast.LENGTH_SHORT).show();	
							}
						};
				    	mHandler.post(listenerRunable);
				    break;
				
				    case ConstantContext.ERROR_8:
				    	if(listenerRunable!=null){
							mHandler.removeCallbacks(listenerRunable);
							listenerRunable = null;
							}
				    	listenerRunable = new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(cyx_RegisterActivity.this,
						    			getResources().getString(MResource.getStringId(getApplicationContext(), 
						    					"phone_already_register"))
						    			, Toast.LENGTH_SHORT).show();	
							}
						};
				    	mHandler.post(listenerRunable);
				    break;
				    case ConstantContext.ERROR_9:
				    	if(listenerRunable!=null){
							mHandler.removeCallbacks(listenerRunable);
							listenerRunable = null;
							}
				    	listenerRunable = new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(cyx_RegisterActivity.this,
						    			getResources().getString(MResource.getStringId(getApplicationContext(), 
						    					"type_error"))
						    			, Toast.LENGTH_SHORT).show();	
							}
						};
				    	mHandler.post(listenerRunable);
				    break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				
			}
			@Override
			public void onFailed(int reason) {
				if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_RegisterActivity.this.getClass().getName())) {
				switch (reason) {
					case RequestCallback.REASON_NO_NETWORK:
						hintContent += getResources().getString(
								MResource.getStringId(getApplicationContext(), "network_switch_off"));
						break;
					case RequestCallback.REASON_NO_SIGNAL:
						hintContent += getResources().getString(
								MResource.getStringId(getApplicationContext(), "no_network_signal"));
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
			}
		});
	}
	//注册临时账号
	private void getTemp(){
		String androidId = Secure.getString(CYX_Drive_SDK.getSavedContext().getContentResolver(),Secure.ANDROID_ID);
		String area = adrpre.getString(Constant.ADDRESS_PROVINCE, "30");
		CYX_Drive_SDK.getInstance().getConnection().sendExtData(new ExtraDataProcess().getTempAccountData(androidId, area), 
				new RequestCallback(){
			@Override
			public void onSuccess(String bizJsonData) {
				JSONObject jsonObj;
				try {
					jsonObj = new JSONObject(bizJsonData);
					//临时账号注册成功后发送验证码
					switch (jsonObj.getInt("result")) {
					case 0:
					case 1:
						UserInfo userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
						userInfo.setTempID(jsonObj.getString("id"));	
						register();
					break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onFailed(int reason) {
						
			}
		});
	}
	/**
	 * 判断是否是手机号码
	 * @param mobiles
	 * @return
	 */
	private boolean isMobileNO(String mobiles){  
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");  
		Matcher m = p.matcher(mobiles);  
		return m.matches();  
	}
	private void showHintContent() {
		if(!TextUtils.isEmpty(hintContent)) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mProgressDialog!=null&&mProgressDialog.isShowing())
						mProgressDialog.dismiss();
					Toast.makeText(cyx_RegisterActivity.this,hintContent,Toast.LENGTH_SHORT).show(); 
					hintContent = "";
				}
			});
		}
	}
}
