package com.nct.application;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

import com.nct.utils.Utils;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MVapplication extends Application {

	private ImageLoader imageLoader;

	@Override
	public void onCreate() {
		super.onCreate();

		initImageLoader(getApplicationContext());

		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		Constants.SCREEN_WIDTH = metrics.widthPixels;
		Constants.SCREEN_HEIGHT = metrics.heightPixels;

		Utils.updateDeviceInfo(this, GlobalInstance.getInstance().userID);
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		ImageLoader.getInstance().clearMemoryCache();
	}
	
}