package com.nct.adapter;

import com.nct.fragment.FragHome;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AtMainViewpagerAdapter extends FragmentPagerAdapter {

	public AtMainViewpagerAdapter(FragmentManager fm, Context context) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		// Constants.idCate[position]

		return new FragHome();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "title";
	}

	@Override
	public int getCount() {
		return 2;
	}
}
