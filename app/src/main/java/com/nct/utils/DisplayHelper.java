package com.nct.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DisplayHelper {
	public static int getScreenWidth(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		return displaymetrics.widthPixels;
	}

	public static int getScreenHeight(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		return displaymetrics.heightPixels;
	}

	public static float sp2Pixel(float value, Context context) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value,
				context.getResources().getDisplayMetrics());
	}

	public static float dip2Pixel(float value, Context context) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
				context.getResources().getDisplayMetrics());
	}

	public static int dip2Pixel(int value, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				value, context.getResources().getDisplayMetrics());
	}

}