package com.nct.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ShowcaseImageView extends ImageView {

	public ShowcaseImageView(Context context) {
		super(context);
	}

	public ShowcaseImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ShowcaseImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, (int) ((float) widthMeasureSpec / (float) 2));
	}
}