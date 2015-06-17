package com.nct.mv;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nct.application.MVapplication;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.customview.TfTextView;
import com.nct.customview.ViewResultLoad;
import com.nct.customview.ViewResultLoad.InterfaceNetworkListener;
import com.nct.utils.FontUtils;
import com.nct.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.util.Locale;

import thh.com.mycouper.R;

public class AtBase extends FragmentActivity {
	
	// /////UINIVERSAL IMAGE LOADER///////////////
	public ImageLoader mImageLoader;
	public DisplayImageOptions options;

	public Typeface mRoboRegular, mRoboLight, mRoboMedium;

	public void initImageLoader() {
		mImageLoader = ((MVapplication) getApplicationContext())
				.getImageLoader();
	}

	public void setDisplayImageOptions(int resId) {
		options = Utils.getDisplayImageOptions(resId);
	}

	protected void displayImage(ImageView img, String uri) {
		if(!TextUtils.isEmpty(uri))
		{
			if (options != null) {
				mImageLoader.displayImage(uri, img, options);
			} else {
				mImageLoader.displayImage(uri, img);
			}
		}
	}

	// ///////////RESULT LOAD///////////
	public ViewResultLoad viewResultLoad;
	public void initViewLoadResult() {
		viewResultLoad = (ViewResultLoad) findViewById(R.id.view_result_load);
		viewResultLoad.setListenerClick(new InterfaceNetworkListener() {
			@Override
			public void onclickNetwork() {
				reloadDisconnect();
			}
		});
	}

	public void reloadDisconnect() {
	};

	private Handler moveActivity = new Handler();
	private Runnable moveRunnable = new Runnable() {
		@Override
		public void run() {
			DelayTimeFinish();
		}
	};

	public void DelayTimeStart(int time) {
		moveActivity.postDelayed(moveRunnable, time);
	}

	public void DelayTimeFinish() {
	}

	// ///////LOADING DIALOG///////////
	private ProgressDialog progressDialog;
	public void showDialogLoading() {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(AtBase.this, "", "", false,
					false);
			progressDialog.setContentView(R.layout.dialog_loading);
			progressDialog.setCancelable(true);
		}
		progressDialog.show();
	}
	public void hideDialogLoading() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	/////////TOP BAR ///////////////////
	private TextView topBartitle;
	private ImageView topBarbtLeft,topBarBtRight;
    private TfTextView topBarTxtRight;
	public void initTopbar(String title)
	{
		topBartitle = (TextView) findViewById(R.id.item_topbar_tv_title);
		topBartitle.setText(title);
		topBarbtLeft = (ImageView) findViewById(R.id.item_topbar_bt_leftmenu);
		topBarBtRight = (ImageView) findViewById(R.id.item_topbar_bt_rightmenu);
        topBarTxtRight = (TfTextView) findViewById(R.id.item_topbar_tv_right);
	}
	public void setTopbarTitle(String text)
	{
		topBartitle.setText(text);
	}
	public void setTopbarLeftBtListener(View.OnClickListener listener){
		topBarbtLeft.setOnClickListener(listener);
	}
	public void setTopbarBtLeftImage(int resource)
	{
		topBarbtLeft.setImageResource(resource);
	}
	public void setTopbarRighttBtListener(View.OnClickListener listener){
		topBarBtRight.setOnClickListener(listener);
	}
	public void setTopbarBtRightImage(int resource)
	{
		topBarBtRight.setImageResource(resource);
	}
	public void setTopbarBtRightVisible(int visi)
	{
		topBarBtRight.setVisibility(visi);
	}

    public void setTopbarTxtRightVisible(int visi)
    {
        topBarTxtRight.setVisibility(visi);
    }

    public void setTopbarTxtRightTitle(String text)
    {
        topBarTxtRight.setText(text);
    }

    public void setTopbarRightTxtListener(View.OnClickListener listener){
        topBarTxtRight.setOnClickListener(listener);
    }

	////////////////// LANGUAGE ////////
	public void setLanguge() {
		if (!TextUtils.isEmpty(GlobalInstance.getInstance().idLanguage)) {
			String languageToLoad = GlobalInstance.getInstance().idLanguage;
			Locale locale = new Locale(languageToLoad);
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());
		}
	}

	public void setupFonts() {
		mRoboRegular = FontUtils.getFonts(this, Constants.FONT_ROBO_REGULAR);
		mRoboLight = FontUtils.getFonts(this, Constants.FONT_ROBO_LIGHT);
		mRoboMedium = FontUtils.getFonts(this, Constants.FONT_ROBO_MEDIUM);
	}
}
