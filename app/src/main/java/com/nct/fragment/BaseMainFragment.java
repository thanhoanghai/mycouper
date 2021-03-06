/**
 * 
 */
package com.nct.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.application.MVapplication;
import com.nct.utils.Utils;
import com.nct.customview.ViewResultLoad;
import com.nct.customview.ViewResultLoad.InterfaceNetworkListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;




import org.apache.http.Header;

import thh.com.mycouper.R;

abstract class BaseMainFragment extends Fragment {

	protected ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;
	protected static final Handler sHandler = new Handler(
			Looper.getMainLooper());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageLoader = ((MVapplication) getActivity().getApplicationContext())
				.getImageLoader();
	}

	@Override
	public abstract View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mImageLoader = null;
		mOptions = null;
	}

	/**
	 * set DisplayImageOptions
	 * 
	 * @param resId
	 */
	protected void setDisplayImageOptions(int resId) {
		mOptions = Utils.getDisplayImageOptions(resId);
	}

	/**
	 * display Image
	 * 
	 * @param img
	 * @param uri
	 */
	protected void displayImage(ImageView img, String uri) {
		if (mOptions != null) {
			mImageLoader.displayImage(uri, img, mOptions);
		} else {
			mImageLoader.displayImage(uri, img);
		}
	}

	protected final TextHttpResponseHandler mTextHttpResponseHandler = new TextHttpResponseHandler() {
		@Override
		public void onSuccess(int arg0, Header[] arg1, String result) {
			if (!isAdded())
				return;
			onLoadDataSuccess(result);
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, String result,
				Throwable arg3) {
			if (!isAdded())
				return;
			onLoadDataFailure(result);
		}
	};

	protected void loadData() {
	}

	protected void onLoadDataSuccess(String result) {
	}

	protected void onLoadDataFailure(String result) {
	}

	public final void remove() {
		getActivity().getSupportFragmentManager().beginTransaction()
				.remove(this).commit();
	}

	public ViewResultLoad viewResultLoad;

	public void initViewLoadResult(View v) {
		viewResultLoad = (ViewResultLoad) v.findViewById(R.id.view_result_load);
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

	/////////TOP BAR ///////////////////
	private TextView topBartitle;
	private ImageView topBarbtLeft,topBarBtRight;
	public void initTopbar(View v,String title)
	{
		topBartitle = (TextView) v.findViewById(R.id.item_topbar_tv_title);
		topBartitle.setText(title);
		topBarbtLeft = (ImageView) v.findViewById(R.id.item_topbar_bt_leftmenu);
		topBarBtRight = (ImageView) v.findViewById(R.id.item_topbar_bt_rightmenu);
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
	public void setTopBarbtRightVisible(int visi)
	{
		topBarBtRight.setVisibility(visi);
	}
	public void setTopBarbtLeftVisible(int visi)
	{
		topBarbtLeft.setVisibility(visi);
	}


}
