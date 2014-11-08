package com.cwits.cyx_drive_sdk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.integrate.IConfiguration;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;

public class ServerChoiceActivity extends Activity {
	private RadioGroup radioGroup;
	private RadioButton radioBtnFormal,radioBtnTest;
	private EditText etFormalServersIp,etFormalServersPort,etTestServersIp,etTestServersPort;
	private TextView tvTitle;
	private Button btnSave,btnReset;
	private ImageView btnBack;
	private String mServerIP,mTestServerIP;
	private int mServerPort,mTestServerPort;
	private IConfiguration iGlobalConfig;
	//服务器类型，true为测试服务器，false为正式服务器
	private boolean serverTag = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		super.onCreate(savedInstanceState);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_server_choice"));
		init();
	}
	private void init(){
		tvTitle = (TextView) findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		iGlobalConfig = CYX_Drive_SDK.getInstance().getConfiguration();
		radioGroup = (RadioGroup) findViewById(MResource.getID(getApplicationContext(),"radioGroup"));
		radioBtnFormal = (RadioButton) findViewById(MResource.getID(getApplicationContext(),"radioBtnFormal"));
		radioBtnTest = (RadioButton) findViewById(MResource.getID(getApplicationContext(),"radioBtnTest"));
		btnSave = (Button) findViewById(MResource.getID(getApplicationContext(),"btnSave"));
		btnReset = (Button) findViewById(MResource.getID(getApplicationContext(),"btnReset"));
		btnBack = (ImageView) findViewById(MResource.getID(getApplicationContext(),"btn_back"));
		etFormalServersIp = (EditText) findViewById(MResource.getID(getApplicationContext(),"etFormalServersIp"));
		etFormalServersPort = (EditText) findViewById(MResource.getID(getApplicationContext(),"etFormalServersPort"));
		etTestServersIp = (EditText) findViewById(MResource.getID(getApplicationContext(),"etTestServersIp"));
		etTestServersPort = (EditText) findViewById(MResource.getID(getApplicationContext(),"etTestServersPort"));
		tvTitle.setText(getString(MResource.getStringId(getApplicationContext(),"server_choice")));
		mServerIP = iGlobalConfig.getServerIP();
		mServerPort = iGlobalConfig.getServerPort();
		mTestServerIP = iGlobalConfig.getTestServerIP();
		mTestServerPort = iGlobalConfig.getTestServerPort();
		serverTag = iGlobalConfig.isTestServer();
		if(serverTag){
			radioBtnTest.setChecked(true);
		}else{
			radioBtnFormal.setChecked(true);
		}
		etFormalServersIp.setText(mServerIP);
		etFormalServersPort.setText(mServerPort+"");
		etTestServersIp.setText(mTestServerIP);
		etTestServersPort.setText(mTestServerPort+"");
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ServerChoiceActivity.this.finish();
			}
		});
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == MResource.getID(getApplicationContext(),"radioBtnFormal")) {
					serverTag = false;
				} else if (checkedId ==MResource.getID(getApplicationContext(),"radioBtnTest")) {
					serverTag = true;
				}
			}
		});
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null!=etFormalServersIp.getText()&&null!=etFormalServersPort.getText()&&!"".equals(etFormalServersIp.getText().toString())&&!"".equals(etFormalServersPort.getText().toString())){
					iGlobalConfig.setServerIP(etFormalServersIp.getText().toString());
					try {
						iGlobalConfig.setServerPort(Integer.parseInt(etFormalServersPort.getText().toString()));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				if(null!=etTestServersIp.getText()&&null!=etTestServersPort.getText()&&!"".equals(etTestServersIp.getText().toString())&&!"".equals(etTestServersPort.getText().toString())){
					iGlobalConfig.setTestServerIP(etTestServersIp.getText().toString());
					try {
						iGlobalConfig.setTestServerPort(Integer.parseInt(etTestServersPort.getText().toString()));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				iGlobalConfig.setTestServer(serverTag);
				//切换服务器之后重新连接一遍
				CYX_Drive_SDK.getInstance().getConnection().stop();
				if(serverTag){
					CYX_Drive_SDK.getInstance().getConnection().setServerAddrPort
					(iGlobalConfig.getTestServerIP(),iGlobalConfig.getTestServerPort());
				}else{
					CYX_Drive_SDK.getInstance().getConnection().setServerAddrPort
					(iGlobalConfig.getServerIP(),iGlobalConfig.getServerPort());
				}
				CYX_Drive_SDK.getInstance().getConnection().setStartType(Connection.START_TYPE_CONNECT_ONLY);
				UserInfo user = CYX_Drive_SDK.getInstance().getUserInfo();
				user.setPassword("");
				user.setAutoLoginFlag(0);
				CYX_Drive_SDK.getInstance().getUserManager().setAutoLogin(user.getName(), 0);
				CYX_Drive_SDK.getInstance().getConnection().start();
				ServerChoiceActivity.this.finish();
			}
		});
		btnReset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etFormalServersIp.setText(iGlobalConfig.getServerIP());
				etFormalServersPort.setText(iGlobalConfig.getServerPort()+"");
				etTestServersIp.setText(mTestServerIP);
				etTestServersPort.setText(mTestServerPort+"");
				if(serverTag){
					radioBtnTest.setChecked(true);
				}else{
					radioBtnFormal.setChecked(true);
				}
			}
		});
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
}
