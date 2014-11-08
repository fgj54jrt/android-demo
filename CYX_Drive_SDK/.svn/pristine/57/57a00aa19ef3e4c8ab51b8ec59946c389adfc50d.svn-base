package com.cwits.cyx_drive_sdk.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.cwits.cyx_drive_sdk.userInfo.IExternalInterfaceAR;

/**
 * 填写验证码页�?
 * @author lxh
 *
 */
public class cyx_UnbindingCheckCodeActivity extends Activity {

	private Button btn_next,requestPhone;    //下一步， 重新发�?
	private EditText edt_checCode;           //验证�?
	private ImageView img_delete, img_back;  // 删除，返�?
	private TextView title;                // 标题
	private TextView tv_phone_num;       //手机号码
	private TimeCount time;//60s倒计�?
	private SmsContent content;
	ProgressDialog mProgressDialog;
	Handler mHandler;
	String phoneNum,subPhone1, subPhone2;
	private String hintContent = "";
	private IExternalInterfaceAR ExternalInterfaceAR;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_checkcode"));
		cyx_MyApplication.getInstance().addActivity(this);
		content = new SmsContent(new Handler());
        //注册短信变化监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, content);
		init();
	}
	private void init(){
		mHandler = new Handler();
		ExternalInterfaceAR=CYX_Drive_SDK.getInstance().getExternalInterface();
		btn_next = (Button) findViewById(MResource.getID(getApplicationContext(), "checkcode_next_btn"));
		btn_next.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_nonpoint"));
		btn_next.setClickable(false);
		requestPhone = (Button) findViewById(MResource.getID(getApplicationContext(), "request_phoneNo"));
		requestPhone.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_nonpoint"));
		requestPhone.setEnabled(false);
		edt_checCode = (EditText) findViewById(MResource.getID(getApplicationContext(), "edt_checkCode"));
		edt_checCode.addTextChangedListener(new MaxLengthWatcher());
		img_delete = (ImageView) findViewById(MResource.getID(getApplicationContext(), "delete_checkCode"));
		img_back = (ImageView) findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		title =  (TextView) findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		tv_phone_num = (TextView) findViewById(MResource.getID(getApplicationContext(), "phone_no_short"));
		time = new TimeCount(60000, 1000);
		time.start();
		phoneNum = getIntent().getStringExtra("phoneNum");
		subPhone1 = phoneNum.substring(0, 3);
		subPhone2 = phoneNum.substring(7, 11);
		tv_phone_num.setText(subPhone1 +"****" + subPhone2);
		title.setText(MResource.getStringId(getApplicationContext(), "input_checkcode"));
		mProgressDialog = new ProgressDialog(cyx_UnbindingCheckCodeActivity.this);
		mProgressDialog.setTitle(getResources().getString(MResource.getStringId(getApplicationContext(), "notice")));
		mProgressDialog.setMessage(getResources().getString(MResource.getStringId(getApplicationContext(), "wait")));
		mProgressDialog.setCanceledOnTouchOutside(false);
		//点击下一步进行解绑
		btn_next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!edt_checCode.getText().toString().trim().equals("")){
					String UserID=CYX_Drive_SDK.getInstance().getUserInfo().getUserID();
					Connection.getInstance().sendExtData(new ExtraDataProcess().unbindingCXB(UserID, phoneNum, edt_checCode.getText().toString()), 
							new RequestCallback() {

								@Override
								public void onSuccess(String bizJsonData) {
									// TODO Auto-generated method stub
									if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_UnbindingCheckCodeActivity.this.getClass().getName())) {
									try {
										JSONObject jsonObj = new JSONObject(bizJsonData);
										switch (jsonObj.getInt("result")) {
										//解绑成功
										case 0:
											Toast.makeText(cyx_UnbindingCheckCodeActivity.this,
															getResources().getString(MResource.getStringId(getApplicationContext(),"unbinding" )), Toast.LENGTH_SHORT).show();
											CYX_Drive_SDK.getInstance().getUserManager().setUserFlag(CYX_Drive_SDK.getInstance().getUserInfo().getName(), 1);
											CYX_Drive_SDK.getInstance().getUserInfo().setFlag(1);
//											Intent intent = new Intent(cyx_UnbindingCheckCodeActivity.this, cyx_BindingFeedbackActivity.class);
//											startActivity(intent);
											if(ExternalInterfaceAR != null) {
												ExternalInterfaceAR.openFeedBack();
											}
											cyx_UnbindingCheckCodeActivity.this.finish();
											break;
											//UserId 为空
										case 1:
											hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "user_id_null"));
											break;
											//手机号码为空
										case 2:
											hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "phoneNum_isNUll"));
											break;
										//验证码为�?
										case 3:
											hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "captcha_isNull"));
											break;
											//验证码错误
										case 4:
											hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "checkCode_error"));
											break;
										//程序错误
										case 5:
											hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "procedure_error"));
											break;
									}
										showHintContent();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									}
								}

								@Override
								public void onFailed(int reason) {
									// TODO Auto-generated method stub
									if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_UnbindingCheckCodeActivity.this.getClass().getName())) {
									switch(reason) {
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
					mProgressDialog.show();
				}
			}
		});
		
		requestPhone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				time.start();
				requestPhone.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "btn_nonpoint"));
				requestPhone.setEnabled(false);
				mProgressDialog.show();
				Connection.getInstance().sendExtData(new ExtraDataProcess().getPhoneVerifyData(ConstantContext.VERIFY_UNBIND_CXB, CYX_Drive_SDK.getInstance().getUserInfo().getUserID(), phoneNum), 
						new RequestCallback(){

							@Override
							public void onSuccess(String bizJsonData) {
								// TODO Auto-generated method stub
								if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_UnbindingCheckCodeActivity.this.getClass().getName())) {
								try {
									JSONObject jsonObj = new JSONObject(bizJsonData);
									switch (jsonObj.getInt("result")) {
									case ConstantContext.SUCCESS:
										hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "sent_checkCode"));
										break;
									case ConstantContext.ERROR_3:	//两次获取验证码时间小于5秒
										hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "apply_too_often"));
										break;
									case ConstantContext.ERROR_2:	//手机号为空
										hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "phoneNum_isNUll"));
										break;
									case ConstantContext.ERROR_1:	//id为空
										hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "logout_id_type_null"));
										break;
									case ConstantContext.ERROR_4:	//短信发送失败
										hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "send_sms_error"));
										break;
									case ConstantContext.ERROR_5:	//程序错误
										hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "procedure_error"));
										break;
									case ConstantContext.ERROR_6:	//手机号格式不正确
										hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "format_error"));
										break;
									 case ConstantContext.ERROR_7:	//用户未注册
										 hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "find_password_message"));
										 break;
									 case ConstantContext.ERROR_8:	//用户已注册
										 hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "user_already_resister"));
											break;
									 case ConstantContext.ERROR_9:	//类型错误
										 hintContent += getResources().getString(MResource.getStringId(getApplicationContext(), "type_error"));
											break;
									}
									showHintContent();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							}

							@Override
							public void onFailed(int reason) {
								// TODO Auto-generated method stub
								if(cyx_MyApplication.getInstance().getActivityStack().lastElement().getClass().getName().equals(cyx_UnbindingCheckCodeActivity.this.getClass().getName())) {
								switch(reason) {
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
		});
		
		img_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_UnbindingCheckCodeActivity.this.finish();
			}
		});
		
		img_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_checCode.setText("");
			}
		});
	}
	
	public class MaxLengthWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			 Editable editable = edt_checCode.getText();  
		     int len = editable.length(); 
			 if((len==6)){
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
     * 监听短信数据库
     */
    class SmsContent extends ContentObserver {

        private Cursor cursor = null;

        public SmsContent(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {

            super.onChange(selfChange);
            //读取收件箱中指定号码的短信
            
            cursor = managedQuery(Uri.parse("content://sms/inbox"), new String[]{"_id", "address", "read", "body"},
                    " address=? and read=?", new String[]{"1069026000230", "0"}, "_id desc");//按id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
            Log.d("ouyangxin", "读取收件箱中指定号码的短信"+cursor.getColumnIndex("body")+
            		"   getcount=="+cursor.getCount()+"  cursor======"+cursor);
            if (cursor != null && cursor.getCount() > 0) {
                ContentValues values = new ContentValues();
                values.put("read", "1");        //修改短信为已读模式
                cursor.moveToNext();
                int smsbodyColumn = cursor.getColumnIndex("body");
                String smsBody = cursor.getString(smsbodyColumn);
                Log.d("ouyangxin", "短信不为空");
                edt_checCode.setText(getDynamicPassword(smsBody));

            }
            Log.d("ouyangxin", "短信为空");
            //在用managedQuery的时候，不能主动调用close()方法， 否则在Android 4.0+的系统上， 会发生崩溃
            if(Build.VERSION.SDK_INT < 14) {
                cursor.close();
            }
        }
    }
    /**
     * 从字符串中截取连续6位数字
     * 用于从短信中获取动态密码
     * @param str 短信内容
     * @return 截取得到的6位动态密码
     */
    public static String getDynamicPassword(String str) {
        Pattern  continuousNumberPattern = Pattern.compile("[0-9\\.]+");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while(m.find()){
            if(m.group().length() == 6) {
                System.out.print(m.group());
                dynamicPassword = m.group();
            }
        }
       Log.d("ouyangxin", "dynamicPassword"+dynamicPassword);
        return dynamicPassword;
    }
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		if(mProgressDialog!=null&&mProgressDialog.isShowing())
			mProgressDialog.cancel();
		cyx_MyApplication.getInstance().removeActivity(this);
		getContentResolver().unregisterContentObserver(content);  
		super.onDestroy();
	}
	//60秒�?计时
	public class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {

			requestPhone.setBackgroundResource(
					MResource.getDrawableId(getApplicationContext(), "btn_resendcode_bg"));
			requestPhone.setText(MResource.getStringId(getApplicationContext(), "check_code_resend"));
			requestPhone.setEnabled(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			requestPhone.setText(millisUntilFinished / 1000 + getResources().getString(
					MResource.getStringId(getApplicationContext(), "s_sendAgain")));
		}

	}
	
	private void showHintContent() {
		if(!TextUtils.isEmpty(hintContent)) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mProgressDialog!=null&&mProgressDialog.isShowing())
						mProgressDialog.dismiss();
					Toast.makeText(cyx_UnbindingCheckCodeActivity.this,hintContent,Toast.LENGTH_SHORT).show(); 
					hintContent = "";
				}
			});
		}
	}

}
