package com.nct.mv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.dataloader.DataHelper;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.fragment.FragCreateCardImage;
import com.nct.fragment.FragCreateCardInfo;
import com.nct.fragment.FragCreateCardSearch;
import com.nct.fragment.FragCreateCardSuccess;
import com.nct.fragment.FragHome;
import com.nct.model.StatusObject;
import com.nct.model.UserObject;
import com.nct.utils.Debug;
import com.nct.utils.Utils;
import com.soundcloud.android.crop.Crop;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
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
		changeFragment(Constants.TYPE_CREATE_CARD_SEARCH, new FragCreateCardSearch());
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


	public void changeFragment(int type, Fragment fragment)
	{
		if(type == Constants.TYPE_CREATE_CARD_SEARCH)
		{
			pushFragments(Constants.TAB_CREATE_CARD, fragment, false, true);
		}else if(type == Constants.TYPE_CREATE_CARD_INFO)
		{
			pushFragments(Constants.TAB_CREATE_CARD, fragment,true,true);
		}else if(type == Constants.TYPE_CREATE_CARD_IMAGE)
		{
			pushFragments(Constants.TAB_CREATE_CARD, fragment,true,true);
		}
		else if(type == Constants.TYPE_CREATE_CARD_SUCCESS)
		{
			pushFragments(Constants.TAB_CREATE_CARD, fragment,true,true);
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

    //    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.at_create_card_linear);
        if(fragment instanceof FragCreateCardInfo)
            ((FragCreateCardInfo)fragment).activityResult(requestCode, resultCode, data);
        else if(fragment instanceof FragCreateCardImage)
            ((FragCreateCardImage)fragment).activityResult(requestCode, resultCode, data);
    }

    public void createNewCard(){
        showDialogLoading();
//        DataLoader.postParam(URLProvider.getSignUp(sEmail, sPass), new TextHttpResponseHandler() {
//            @Override
//            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//                hideDialogLoading();
//            }
//
//            @Override
//            public void onSuccess(int i, Header[] headers, String s) {
//                hideDialogLoading();
//                String mUserID = "";
//                try {
//                    JSONObject object = new JSONObject(s);
//                    JSONObject obj = object.getJSONObject("data");
//                    mUserID = obj.optString("user_id");
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                StatusObject item = DataHelper.getStatusObject(s);
//                Debug.toast(AtSignUp.this, item.errorMessage);
//                UserObject data = new UserObject("", sEmail);
//                GlobalInstance.getInstance().userInfo = data;
//                Utils.gotoScreenMain(AtSignUp.this);
//                finish();
//
////                {"statusCode":"200","data":{"user_id":268}}
//            }
//        });
    }
}
