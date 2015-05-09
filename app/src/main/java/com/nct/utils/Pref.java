package com.nct.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Pref {
	
	public static void SaveLongObject(String keySave, long y, Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(keySave, y);
		editor.commit();
	}

	public static long getLongObject(String keySave, Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		long y = (long) prefs.getLong(keySave, 0);
		return y;
	}

	public static void SaveIntObject(String keySave, int y, Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(keySave, y);
		editor.commit();
	}

	public static int getIntObject(String keySave, Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int y = (int) prefs.getInt(keySave, 0);
		return y;
	}

	public static void SaveFloatObject(String keySave, float y, Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putFloat(keySave, y);
		editor.commit();
	}

	public static float getFloatObject(String keySave, Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		float y = prefs.getFloat(keySave, -1);
		return y;
	}

	public static boolean getBooleanObject(String keySave, Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Boolean y = prefs.getBoolean(keySave, false);
		return y;
	}

	public static void SaveBooleanObject(String keySave, boolean y, Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(keySave, y);
		editor.commit();
	}

	public static void removeStringObject(String keySave, Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(keySave);
		editor.commit();
	}

	public static void SaveStringObject(String keySave, String y,
			Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(keySave, y);
		editor.commit();
	}

	public static String getStringObject(String keySave, Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String y = prefs.getString(keySave, "");
		return y;
	}
}