package com.nct.mv;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.nct.adapter.ShowcaseAdapter;
import com.nct.customview.CirclePageIndicator;
import com.nct.customview.ShowcaseViewPager;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class AtTutorials extends AtBase {
	private static final String tag = "AtTutorials";

	private android.support.v4.view.ViewPager showcaseViewpager;
	private ShowcaseAdapter showcaseAdapter;
	private CirclePageIndicator indicator;
	private TextView tvSkip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_tutorials);

		setLanguge();
		initTopbar(getString(R.string.tutorials));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.INVISIBLE);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		tvSkip = (TextView) findViewById(R.id.layout_showcase_tv_skip);
		tvSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		showcaseViewpager = (android.support.v4.view.ViewPager)
				findViewById(R.id.layout_showcase_viewpager);
		indicator = (CirclePageIndicator)
				findViewById(R.id.layout_showcase_indicator);
		initDataLink();
	}

	private void initDataLink()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("http://cdn.savings.com/img/Kmart-Coupon.jpeg");
		list.add("http://cdn.savings.com/img/Kmart-Coupon.jpeg");
		list.add("http://cdn.savings.com/img/Kmart-Coupon.jpeg");
		list.add("http://cdn.savings.com/img/Kmart-Coupon.jpeg");
		list.add("http://cdn.savings.com/img/Kmart-Coupon.jpeg");
		list.add("http://cdn.savings.com/img/Kmart-Coupon.jpeg");
		list.add("http://cdn.savings.com/img/Kmart-Coupon.jpeg");
		list.add("http://cdn.savings.com/img/Kmart-Coupon.jpeg");
		list.add("http://cdn.savings.com/img/Kmart-Coupon.jpeg");
		list.add("http://cdn.savings.com/img/Kmart-Coupon.jpeg");
		list.add("http://cdn.savings.com/img/Kmart-Coupon.jpeg");
		list.add("http://cdn.savings.com/img/Kmart-Coupon.jpeg");
		list.add("http://cdn.savings.com/img/Kmart-Coupon.jpeg");
		if (showcaseAdapter == null) {
			showcaseAdapter = new ShowcaseAdapter(AtTutorials.this,
					list);
		}
		showcaseViewpager.setAdapter(showcaseAdapter);
		indicator.setViewPager(showcaseViewpager);
	}
}
