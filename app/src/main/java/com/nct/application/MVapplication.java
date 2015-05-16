package com.nct.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

import com.facebook.SessionDefaultAudience;
import com.nct.background.TaskExecuter;
import com.nct.utils.Device;
import com.nct.utils.Utils;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

public class MVapplication extends Application {

	private ImageLoader imageLoader;

	@Override
	public void onCreate() {
		super.onCreate();

		initImageLoader(getApplicationContext());
		initFacebook();

		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		Constants.SCREEN_WIDTH = metrics.widthPixels;
		Constants.SCREEN_HEIGHT = metrics.heightPixels;


		Utils.updateDeviceInfo(this, "");

        new Device(this);

        initObjects();
	}

    private void initObjects() {
        // create async task
        com.nct.background.ImageLoader.getInstance(MVapplication.this);
        TaskExecuter.getInstance(MVapplication.this);

        // session
        //TODO
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


	private void initFacebook() {
		// initialize facebook configuration
		Permission[] permissions = new Permission[] {
				Permission.PUBLIC_PROFILE, Permission.PUBLISH_ACTION,
				Permission.EMAIL, Permission.USER_PHOTOS };
		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
				.setAppId(Constants.FACEBOOK_ID)
				.setNamespace(Constants.FACEBOOK_NAMESPACE)
				.setPermissions(permissions)
				.setDefaultAudience(SessionDefaultAudience.FRIENDS)
				.setAskForAllPermissionsAtOnce(false).build();
		SimpleFacebook.setConfiguration(configuration);
	}
}