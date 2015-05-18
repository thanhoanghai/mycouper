package com.nct.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class Device
{
	protected static String DEVICE_NAME;
	protected static String DEVICE_MODEL;
	protected static int 		DEVICE_WIDTH;
	protected static int 		DEVICE_HEIGHT;
	protected static float 		DEVICE_DENSITY;
	protected static String DEVICE_OS;
	protected static double 	LATITUDE;
	protected static double 	LONGITUDE;
	
	final static String TAG = "system.Device";
	
	public Device(Context act)
	{
		DisplayMetrics displaymetrics = act.getResources().getDisplayMetrics();;
		DEVICE_WIDTH = displaymetrics.widthPixels;
		DEVICE_HEIGHT = displaymetrics.heightPixels;
		DEVICE_DENSITY= displaymetrics.density;
		DEVICE_NAME = android.os.Build.DEVICE;
		DEVICE_MODEL = android.os.Build.MODEL;
		DEVICE_OS = System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
		
	}
	
	public static String getDeviceName()
	{
		return DEVICE_NAME;
	}
	
	public static int getDeviceWidth()
	{
		return DEVICE_WIDTH;
	}
	
	public static int getDeviceHeight()
	{
		return DEVICE_HEIGHT;
	}
	
	public static float getDeviceDensity()
	{
		return DEVICE_DENSITY;
	}
}
