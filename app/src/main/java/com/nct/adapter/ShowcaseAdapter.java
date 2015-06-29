package com.nct.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.nct.constants.GlobalInstance;
import com.nct.customview.ShowcaseImageView;
import com.nct.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.util.ArrayList;

import thh.com.mycouper.R;

public class ShowcaseAdapter extends PagerAdapter {

	private Context mContext;
	private ArrayList<String> mList;
	private DisplayImageOptions options;

	public ShowcaseAdapter(Context context, ArrayList<String> list) {
		mContext = context;
		options =
		Utils.getDisplayImageOptions(R.drawable.splash_icon);
		mList = list;
		
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView view = new ImageView(mContext);
		view.setScaleType(ImageView.ScaleType.FIT_XY);
		((ViewPager) container).addView(view, 0);
		
		
		String item = mList.get(position);
		if(!TextUtils.isEmpty(item))
			ImageLoader.getInstance().displayImage(item, view, options);


		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((ImageView) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((ImageView) object);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mList != null)
			return mList.size();
		return 0;
	}
}