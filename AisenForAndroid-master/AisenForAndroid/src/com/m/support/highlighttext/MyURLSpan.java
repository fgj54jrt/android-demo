package com.m.support.highlighttext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import com.m.R;
import com.m.common.utils.Logger;
import com.m.ui.activity.BaseActivity;
import com.m.ui.utils.MToast;

public class MyURLSpan extends ClickableSpan implements ParcelableSpan {

	private final String mURL;

	public MyURLSpan(String url) {
		mURL = url;
	}

	public MyURLSpan(Parcel src) {
		mURL = src.readString();
	}

	public int getSpanTypeId() {
		return 11;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mURL);
	}

	public String getURL() {
		return mURL;
	}

	@Override
	public void onClick(View widget) {
		Logger.v(MyURLSpan.class.getSimpleName(), String.format("the link(%s) was clicked ", getURL()));

		Uri uri = Uri.parse(getURL());
		Context context = widget.getContext();
		if (uri.getScheme().startsWith("http")) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			intent.setData(uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} else {
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
			context.startActivity(intent);
		}
	}

	public void onLongClick(View widget) {
		Uri data = Uri.parse(getURL());
		if (data != null) {
			String d = data.toString();
			String newValue = "";
			if (d.startsWith("com.m.ui")) {
				int index = d.lastIndexOf("/");
				newValue = d.substring(index + 1);
			} else if (d.startsWith("http")) {
				newValue = d;
			}
			if (!TextUtils.isEmpty(newValue)) {
				ClipboardManager cm = (ClipboardManager) widget.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
				cm.setPrimaryClip(ClipData.newPlainText("ui", newValue));
				MToast.showMessage(String.format(widget.getContext().getString(R.string.have_copied), newValue));
			}
		}
	}

	@Override
	public void updateDrawState(TextPaint tp) {
		int[] attrs = new int[] { R.attr.link_color };
		TypedArray ta = BaseActivity.getRunningActivity().obtainStyledAttributes(attrs);
		tp.setColor(ta.getColor(0, Color.BLUE));

//        tp.setUnderlineText(true);
	}
}
