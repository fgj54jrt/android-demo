package com.cwits.cyx_drive_sdk.ui;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * 自定义对话框
 * @author lxh
 *
 */
public class cyx_CustomAlertDialog {
	Context mContext;
	android.app.AlertDialog ad;
	TextView titleView;
	TextView messageView,phoneNumber;
	TextView btn_ok,btnCancle;
	View line;
//	LinearLayout buttonLayout;
	public cyx_CustomAlertDialog(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext=context;
		ad=new android.app.AlertDialog.Builder(context).create();
		ad.show();
		Window window = ad.getWindow();
		window.setContentView(MResource.getLayoutId(context, "cyx_dialog"));
		titleView = (TextView)window.findViewById(MResource.getID(context, "cyx_dialog_title"));
		messageView = (TextView)window.findViewById(MResource.getID(context,"cyx_dialog_content"));
		phoneNumber = (TextView)window.findViewById(MResource.getID(context,"cyx_phonenumber"));
        btn_ok = (TextView)window.findViewById(MResource.getID(context, "cyx_dialog_ok"));
        btnCancle = (TextView)window.findViewById(MResource.getID(context, "cyx_dialog_cancel"));
        line = window.findViewById(MResource.getID(context, "cyx_dialog_line"));
	}
	public void setTitle(int resId)
	{
		titleView.setText(resId);
	}
	public void setTitle(String title) {
		titleView.setText(title);
	}
	public void setMessage(int resId) {
		messageView.setText(resId);
	}
	public void setMessage(String message)
	{
		messageView.setText(message);
	}
	public void setNumber(String message)
	{
		phoneNumber.setText(message);
	}
	
	//设置message是否可见
	public void setMessageVisible(boolean isVisible) {
		if(isVisible) {
			messageView.setVisibility(View.VISIBLE);
		} else {
			messageView.setVisibility(View.GONE);
		}
	}
	
	//设置message是否可见
		public void setNumberVisible(boolean isVisible) {
			if(isVisible) {
				phoneNumber.setVisibility(View.VISIBLE);
			} else {
				phoneNumber.setVisibility(View.GONE);
			}
		}
	
	//确定按钮与取消按钮之间的竖线
	public void showLine(){
		line.setVisibility(View.VISIBLE);
	}
	/**
	 * 设置按钮
	 * @param text
	 * @param listener
	 */
	public void setPositiveButton(String text,final View.OnClickListener listener)
	{
		btn_ok.setVisibility(View.VISIBLE);
		btn_ok.setText(text);
		btn_ok.setOnClickListener(listener);
	}
	public void setPositiveButton(final View.OnClickListener listener)
	{
		btn_ok.setVisibility(View.VISIBLE);
		btn_ok.setOnClickListener(listener);
	}
	/**
	 * 设置按钮
	 * @param text
	 * @param listener
	 */
	public void setNegativeButton(String text,final View.OnClickListener listener)
	{
		btnCancle.setVisibility(View.VISIBLE);
		btnCancle.setText(text);
		btnCancle.setOnClickListener(listener);
 
	}
	public void setNegativeButton(final View.OnClickListener listener)
	{
		btnCancle.setVisibility(View.VISIBLE);
		btnCancle.setOnClickListener(listener);
 
	}
	/**
	 * 关闭对话�?
	 */
	public void dismiss() {
		ad.dismiss();
	}
	public boolean isShowing(){
		return ad.isShowing();
	}
}
