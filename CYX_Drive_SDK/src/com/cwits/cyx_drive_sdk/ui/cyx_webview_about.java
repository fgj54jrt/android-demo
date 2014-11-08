package com.cwits.cyx_drive_sdk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class cyx_webview_about extends Activity{
	private WebView agreementView;
	private ImageView btn_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getLayoutId(getApplicationContext(),
				"cyx_webview"));
		cyx_MyApplication.getInstance().addActivity(this);
		btn_back = (ImageView) findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_webview_about.this.finish();
			}
		});
		agreementView=(WebView) findViewById(MResource.getID(
				getApplicationContext(), "word_web_view"));
		agreementView.loadUrl("http://kcnzq.com/down/agreement.htm");
		agreementView.getSettings().setUseWideViewPort(true);
		agreementView.getSettings().setLoadWithOverviewMode(true);
	}

}
