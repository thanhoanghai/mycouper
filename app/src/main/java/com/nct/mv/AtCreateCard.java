package com.nct.mv;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;

import com.nct.constants.Constants;
import com.nct.fragment.FragCreateCardImage;
import com.nct.fragment.FragCreateCardInfo;
import com.nct.fragment.FragCreateCardSearch;
import com.nct.fragment.FragCreateCardSuccess;
import com.nct.fragment.FragHome;
import com.nct.utils.Utils;

import java.util.HashMap;
import java.util.Stack;

import thh.com.mycouper.R;

public class AtCreateCard extends AtBase {

	private HashMap<String, Stack<Fragment>> mStacks;
	private String mCurrentTab = Constants.TAB_CREATE_CARD;

	private LinearLayout linear;

	private FragCreateCardSearch fragCreateCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_create_card);

		initSaveFragment();
		setLanguge();

		linear = (LinearLayout) findViewById(R.id.at_create_card_linear);
		changeFragment(Constants.TYPE_CREATE_CARD_SEARCH);
	}

	private void initSaveFragment()
	{
		mStacks = new HashMap<String, Stack<Fragment>>();
		mStacks.put(Constants.TAB_CREATE_CARD, new Stack<Fragment>());
	}

	public void pushFragments(String tag, Fragment fragment,
							  boolean shouldAnimate, boolean shouldAdd) {
		if (shouldAdd)
			mStacks.get(tag).push(fragment);
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		if (shouldAnimate)
			ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
		ft.replace(R.id.at_create_card_linear, fragment);
		ft.commit();
	}


	public void changeFragment(int type)
	{
		if(type == Constants.TYPE_CREATE_CARD_SEARCH)
		{
			pushFragments(Constants.TAB_CREATE_CARD,new FragCreateCardSearch(),false,true);
		}else if(type == Constants.TYPE_CREATE_CARD_INFO)
		{
			pushFragments(Constants.TAB_CREATE_CARD,new FragCreateCardInfo(),true,true);
		}else if(type == Constants.TYPE_CREATE_CARD_IMAGE)
		{
			pushFragments(Constants.TAB_CREATE_CARD,new FragCreateCardImage(),true,true);
		}
		else if(type == Constants.TYPE_CREATE_CARD_SUCCESS)
		{
			pushFragments(Constants.TAB_CREATE_CARD,new FragCreateCardSuccess(),true,true);
		}
	}

	@Override
	public void onBackPressed() {
		actionBackKey();
	}
	public void actionBackKey() {
		if (mStacks.get(mCurrentTab).size() == 1) {
			super.onBackPressed(); // or call finish..
		} else {
			popFragments();
		}
	}
	public void popFragments() {
		Fragment fragment = mStacks.get(mCurrentTab).elementAt(
				mStacks.get(mCurrentTab).size() - 2);

		/* pop current fragment from stack.. */
		mStacks.get(mCurrentTab).pop();
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
		ft.replace(R.id.at_create_card_linear, fragment);
		ft.commit();
	}

}
