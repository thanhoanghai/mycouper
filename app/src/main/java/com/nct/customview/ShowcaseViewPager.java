package com.nct.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class ShowcaseViewPager extends ViewPager {

	public ShowcaseViewPager(Context context) {
		super(context);
	}

	public ShowcaseViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec,
				(int) ((float) widthMeasureSpec / (float) 2));
	}
}
