package com.nct.customview;

import java.text.DateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nct.model.StampQrcode;
import com.nct.utils.Utils;

public class ViewPagerQrcode extends FrameLayout implements
		ViewPager.OnPageChangeListener {

	boolean mNeedsRedraw = false;
	private ArrayList<StampQrcode> qrcodeList;
	private ArrayList<String> photos;
	private boolean endless = false;

	private TextView txtTitle, txtName, txtStoreName, txtDate;
	private String mStoreName;
	private String mTitle;

	public ViewPagerQrcode(Context context) {
		super(context);
		init();
	}

	public ViewPagerQrcode(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ViewPagerQrcode(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void storeName(String storeName, String title){
		this.mStoreName = storeName;
		this.mTitle = title;
	}

	public void setTitleTv(TextView tvTitle) {
		this.txtTitle = tvTitle;
	}

	public void setName(TextView tvName) {
		this.txtName = tvName;
	}

	public void setStoreName(TextView tvStoreName) {
		this.txtStoreName = tvStoreName;
	}

	public void setDate(TextView tvDate) {
		this.txtDate = tvDate;
	}

	public void isEndlessRounded(boolean flag) {
		endless = flag;
	}

	private void init() {
		setClipChildren(false);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	public void setListModule(ArrayList<StampQrcode> promotionList) {
		this.qrcodeList = promotionList;
	}

	public void setListPhoto(ArrayList<String> photos) {
		this.photos = photos;
	}

	private ViewPager mPager;

	@Override
	protected void onFinishInflate() {
		int mWidth = getResources().getDisplayMetrics().widthPixels * 7 / 10;
		mPager = (ViewPager) getChildAt(0);
		mPager.setOnPageChangeListener(this);
		LayoutParams ly = (LayoutParams) mPager.getLayoutParams();
		ly.width = mWidth;
		ly.height = mWidth;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return mPager.dispatchTouchEvent(ev);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// Force the container to redraw on scrolling.
		// Without this the outer pages render initially and then stay static
		if (mNeedsRedraw)
			invalidate();
	}

	@Override
	public void onPageSelected(int position) {
		if (endless) {
			//TODO:
			StampQrcode item = qrcodeList.get(getRealPosition(position));

			if(mTitle != null && !mTitle.equals("NULL"))
				txtTitle.setText(mTitle);
			else
				txtTitle.setText("");
			if(item.pos_name != null && !item.pos_name.equals("NULL"))
				txtName.setText(item.pos_name);
			else
				txtName.setText("");
			if(mStoreName != null && !mStoreName.equals("NULL"))
				txtStoreName.setText(mStoreName);
			else
				txtStoreName.setText("");
			String time = System.currentTimeMillis() + "";
			txtDate.setText(Utils.formatDate(time));
			if(item.last_update != null && !item.last_update.equals("NULL"))
				txtDate.setText(Utils.formatDate(time));
			else
				txtDate.setText("");
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		mNeedsRedraw = (state != ViewPager.SCROLL_STATE_IDLE);
	}

	private int getRealPosition(int pos) {
		return pos % qrcodeList.size();
	}
}
