package com.cwits.cyx_drive_sdk.findPassword;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ConstantContext;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.ui.MResource;
import com.cwits.cyx_drive_sdk.ui.cyx_CustomAlertDialog;
import com.cwits.cyx_drive_sdk.ui.cyx_MyApplication;
import com.cwits.cyx_drive_sdk.ui.cyx_RegisterUserProtocolActivity;

public class cyx_FindPasswordActivity extends Activity{

	private Button btn_next;
	private EditText edt_phone_Number;
	private CheckBox mcheckBox;
	private TextView tv_UserProtocol;
	private TextView title,agree,protocol_tv;
	private ImageView img_back;
	private TextView input_phoneNum_notice;
	ProgressDialog mProgressDialog; 
	Handler mHandler;
	cyx_CustomAlertDialog mDialog;
	cyx_CustomAlertDialog mDialog1;
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
		init();
	}
	private void init(){
		mHandler = new Handler();
		btn_next = (Button) findViewById(MResource.getID(getApplicationContext(), "next_btn"));
		btn_next.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_nonpoint"));
		edt_phone_Number = (EditText) findViewById(MResource.getID(getApplicationContext(), "register_phoneNo"));
		edt_phone_Number.addTextChangedListener(new MaxLengthWatcher());
		
		mcheckBox = (CheckBox) findViewById(MResource.getID(getApplicationContext(), "register_checkbox"));
	    mcheckBox.setVisibility(View.GONE);
		tv_UserProtocol = (TextView) findViewById(MResource.getID(getApplicationContext(), "protocol_tv"));
		title = (TextView) findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		agree = (TextView) findViewById(MResource.getID(getApplicationContext(), "agree"));
		agree.setVisibility(View.GONE);
		protocol_tv = (TextView) findViewById(MResource.getID(getApplicationContext(), "protocol_tv"));
		protocol_tv.setVisibility(View.GONE);
		img_back = (ImageView) findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		title.setText(MResource.getStringId(getApplicationContext(), "cyx_find_password"));
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
		mProgressDialog = new ProgressDialog(cyx_FindPasswordActivity.this);
		mProgressDialog.setTitle(getResources().getString(MResource.getStringId(getApplicationContext(), "notice")));
		mProgressDialog.setMessage(getResources().getString(MResource.getStringId(getApplicationContext(), "wait")));
		//下一步
		btn_next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					if(!edt_phone_Number.getText().toString().trim().equals("")){
						if(edt_phone_Number.length()!=11){
							Toast.makeText(cyx_FindPasswordActivity.this,getResources().getString(MResource.getStringId(getApplicationContext(), "input_error_phone")), Toast.LENGTH_SHORT).show();
						}else if(!isMobileNO(edt_phone_Number.getText().toString().trim())){
							Toast.makeText(cyx_FindPasswordActivity.this,getResources().getString(MResource.getStringId(getApplicationContext(), "format_error")), Toast.LENGTH_SHORT).show();
						}else{
							createDialog();
						}
					}else{
						return;
					}
				
				
		}
		});
		tv_UserProtocol.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(cyx_FindPasswordActivity.this, cyx_RegisterUserProtocolActivity.class);
				startActivity(intent);
			}
		});
		img_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_FindPasswordActivity.this.finish();
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
    	mDialog = new cyx_CustomAlertDialog(cyx_FindPasswordActivity.this);
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
						// TODO Auto-generated method stub
						send();
						mDialog.dismiss();
						mProgressDialog.show();
					}
				});
    }
    JSONObject jsonObj ;
    private void send(){
    	CYX_Drive_SDK.getInstance().getConnection().sendExtData(new ExtraDataProcess().
    			getPhoneVerifyData(ConstantContext.VERIFY_FIND_PASSWD, CYX_Drive_SDK.getInstance().getUserInfo().getUserID(), edt_phone_Number.getText().toString()),
    			new RequestCallback(){
    		 @Override
    		public void onSuccess(String bizJsonData) {
    			 if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_FindPasswordActivity.this.getClass().getName())) {
    				 if(mProgressDialog!=null && mProgressDialog.isShowing())
    					 mProgressDialog.dismiss();
    			 try {
					jsonObj = new JSONObject(bizJsonData);
					switch (jsonObj.getInt("result")) {
					case ConstantContext.SUCCESS:
						if(listenerRunable!=null){
							mHandler.removeCallbacks(listenerRunable);
							listenerRunable = null;
							}
						listenerRunable = new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									  CYX_Drive_SDK.getInstance().getUserInfo().setTempID(jsonObj.getString("phoneId"));
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Toast.makeText(cyx_FindPasswordActivity.this,
										getResources().getString(MResource.getStringId(getApplicationContext(),"sent_checkCode" )),Toast.LENGTH_SHORT).show(); 
								Intent intent = new Intent(cyx_FindPasswordActivity.this, cyx_FindPasswordCheckCodeActivity.class);
								intent.putExtra("phoneNum", edt_phone_Number.getText().toString());	
								startActivity(intent);
								cyx_FindPasswordActivity.this.finish();
							}
						};
						mHandler.post(listenerRunable);
						
				    break;
					case ConstantContext.ERROR_1:	//id为空
						Toast.makeText(cyx_FindPasswordActivity.this, getResources().getString(MResource.getStringId(getApplicationContext(),"user_id_null" )),Toast.LENGTH_SHORT).show(); 
						break;
					case ConstantContext.ERROR_2:	//手机号为空
						Toast.makeText(cyx_FindPasswordActivity.this, getResources().getString(MResource.getStringId(getApplicationContext(),"phoneNum_isNUll" )),Toast.LENGTH_SHORT).show(); 
						break;
					case ConstantContext.ERROR_3:	//两次获取验证码时间小于5秒
						Toast.makeText(cyx_FindPasswordActivity.this, getResources().getString(MResource.getStringId(getApplicationContext(),"apply_too_often" )),Toast.LENGTH_SHORT).show(); 
						break;
					case ConstantContext.ERROR_4:	//短信发送失败
						Toast.makeText(cyx_FindPasswordActivity.this, getResources().getString(MResource.getStringId(getApplicationContext(),"send_sms_error" )),Toast.LENGTH_SHORT).show(); 
						break;
					case ConstantContext.ERROR_5:	//程序错误
						Toast.makeText(cyx_FindPasswordActivity.this, getResources().getString(MResource.getStringId(getApplicationContext(),"procedure_error" )),Toast.LENGTH_SHORT).show(); 
						break;
					case ConstantContext.ERROR_6:	//手机号格式不正确
						Toast.makeText(cyx_FindPasswordActivity.this, getResources().getString(MResource.getStringId(getApplicationContext(),"format_error" )),Toast.LENGTH_SHORT).show(); 
						break;
					 case ConstantContext.ERROR_7:	//用户未注册
						Toast.makeText(cyx_FindPasswordActivity.this, getResources().getString(MResource.getStringId(getApplicationContext(),"find_password_message" )),Toast.LENGTH_SHORT).show(); 
						 break;
					 case ConstantContext.ERROR_8:	//用户已注册
							Toast.makeText(cyx_FindPasswordActivity.this, getResources().getString(MResource.getStringId(getApplicationContext(),"user_already_resister" )),Toast.LENGTH_SHORT).show(); 
							break;
					 case ConstantContext.ERROR_9:	//类型错误
							Toast.makeText(cyx_FindPasswordActivity.this, getResources().getString(MResource.getStringId(getApplicationContext(),"type_error" )),Toast.LENGTH_SHORT).show(); 
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
    			if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_FindPasswordActivity.this.getClass().getName())) {
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
					if(mProgressDialog!=null&&mProgressDialog.isShowing())
						mProgressDialog.dismiss();
					Toast.makeText(cyx_FindPasswordActivity.this,hintContent,Toast.LENGTH_SHORT).show(); 
					hintContent = "";
				}
			});
		}
	}
}
