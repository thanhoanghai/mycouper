package com.nct.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.nct.constants.Constants;

public class Debug {

	public static void logError(String tag, String msg) {
		if (Constants.DEBUG_ERROR) {
			Log.e(tag, msg);
		}
	}

	public static void logURL(String tag, String msg) {
		if (Constants.DEBUG_URL) {
			Log.e(tag, msg);
		}
	}

	public static void logFlow(String tag, String msg) {
		if (Constants.DEBUG_FLOW) {
			Log.e(tag, msg);
		}
	}

	public static void logData(String tag, String msg) {
		if (Constants.DEBUG_DATA) {
			Log.e(tag, msg);
		}
	}

	public static void toast(Context mContext, String s) {
		Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
	}

	public static void toastfast(Context mContext, String s) {
		Toast a = Toast.makeText(mContext, s, Toast.LENGTH_SHORT);
		a.setDuration(10);
		a.show();
	}

	public static void toastDebug(Context mContext, String s) {
		if (Constants.DEBUG_TOAST)
			Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
	}
}