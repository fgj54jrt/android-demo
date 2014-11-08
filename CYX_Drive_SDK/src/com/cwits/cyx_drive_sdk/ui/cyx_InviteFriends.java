package com.cwits.cyx_drive_sdk.ui;

import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.userInfo.IExternalInterfaceAR;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;

import android.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class cyx_InviteFriends extends Activity implements OnClickListener {

	private ImageView wechatBtn, msgBtn, sinaBtn;// 邀请好友群按钮
	private Button cancleBtn; // 取消按钮
	private String toastText = "";
	private Handler handler;
	ProgressDialog mProgressDialog;
	private IExternalInterfaceAR mIExternalInterfaceAR;
	String UserID;
	boolean networkState = false; // 网络状态
	NetworkDetector networkDetector;
    UserInfo userInfo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题�?

		setContentView(MResource.getLayoutId(getApplicationContext(),
				"cyx_invite_friends"));
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数�?
		p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的1.0
		p.gravity = Gravity.BOTTOM;
		mIExternalInterfaceAR = CYX_Drive_SDK.getInstance().getExternalInterface();
		// getWindow().setAttributes((android.view.WindowManager.LayoutParams)
		// p); // 设置生效
        cyx_MyApplication.getInstance().addActivity(this);
		init();
	}

	@Override
	public void onClick(View v) {
		networkState = networkDetector.detect(this);
		// TODO Auto-generated method stub
		if (v.getId() == MResource.getID(getApplicationContext(), "cancle_btn")) {
			this.finish();
			return;
		} 

		if (v.getId() == MResource.getID(
				getApplicationContext(), "wechat_btn")) {
			if(mIExternalInterfaceAR!=null){
		
				String text = getResources().getString(MResource.getStringId(getApplicationContext(), "wechar_intro"));
				String absPath = "drawable-mdpi/vip_car.png";
				String wxUrl = "www.kc9555168.com";
				String title = getResources().getString(MResource.getStringId(getApplicationContext(), "invite_title"));
				int wxType = 3;
				int snsPlatform = 0;
				
				mIExternalInterfaceAR.socialShare(text,absPath, wxUrl, title, wxType, snsPlatform);				
			} 


		} else if (v.getId() == MResource.getID(
				getApplicationContext(), "msg_btn")) {
			if(mIExternalInterfaceAR!=null){
				String text = getResources().getString(MResource.getStringId(getApplicationContext(), "msg_intro"));
				String absPath = null;
				String wxUrl = null;
				String title = null;
				int wxType = 1;
				int snsPlatform = 3;
				mIExternalInterfaceAR.socialShare(text,absPath, wxUrl, title, wxType, snsPlatform);				
			}
				
		}  else if (v.getId() == MResource.getID(
				getApplicationContext(), "sina_btn")) {
			
			if(mIExternalInterfaceAR!=null){
				String text = getResources().getString(MResource.getStringId(getApplicationContext(), "sina_intro"));
				String absPath = null;
				String wxUrl = null;
				String title = null;
				int wxType = 1;
				int snsPlatform = 2;
				mIExternalInterfaceAR.socialShare(text,absPath, wxUrl, title, wxType, snsPlatform);				
			}
				
		}

	}

	private void init() {
		userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
		UserID = userInfo.getUserID();
		handler = new Handler();

		wechatBtn = (ImageView) findViewById(MResource.getID(
				getApplicationContext(), "wechat_btn"));
		msgBtn = (ImageView) findViewById(MResource.getID(
				getApplicationContext(), "msg_btn"));
		sinaBtn = (ImageView) findViewById(MResource.getID(
				getApplicationContext(), "sina_btn"));
		cancleBtn = (Button) findViewById(MResource.getID(getApplicationContext(),"cancle_btn"));
		
		wechatBtn.setOnClickListener(this);
		msgBtn.setOnClickListener(this);
		sinaBtn.setOnClickListener(this);
		cancleBtn.setOnClickListener(this);

		networkDetector = new NetworkDetector();
		
		// Log.e(TAG,"longitude"+lon);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		cyx_MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}

	private void showToast() {
		if (!TextUtils.isEmpty(toastText)) {

			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mProgressDialog!=null&&mProgressDialog.isShowing())
						mProgressDialog.dismiss();
					Toast.makeText(cyx_InviteFriends.this, toastText,
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	public class NetworkDetector {

		public boolean detect(Activity act) {

			ConnectivityManager manager = (ConnectivityManager) act
					.getApplicationContext().getSystemService(
							Context.CONNECTIVITY_SERVICE);

			if (manager == null) {
				return false;
			}

			NetworkInfo networkinfo = manager.getActiveNetworkInfo();

			if (networkinfo == null || !networkinfo.isAvailable()) {
				return false;
			}

			return true;
		}
	}

}
