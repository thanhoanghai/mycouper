package com.nct.mv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.dataloader.DataHelper;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.TaskExecuter;
import com.nct.dataloader.URLProvider;
import com.nct.fragment.FragCreateCardImage;
import com.nct.fragment.FragCreateCardInfo;
import com.nct.fragment.FragCreateCardSearch;
import com.nct.fragment.FragCreateCardSuccess;
import com.nct.fragment.FragHome;
import com.nct.model.ItemCreateKard;
import com.nct.model.StatusObject;
import com.nct.model.UserObject;
import com.nct.utils.BitmapUtils;
import com.nct.utils.Debug;
import com.nct.utils.Utils;
import com.soundcloud.android.crop.Crop;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Stack;

import thh.com.mycouper.R;

public class AtCreateCard extends AtBase {

	private HashMap<String, Stack<Fragment>> mStacks;
	private String mCurrentTab = Constants.TAB_CREATE_CARD;

	private LinearLayout linear;

	private FragCreateCardSearch fragCreateCard;

    private boolean isEditCard = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_create_card);

        if(getIntent() != null)
            isEditCard = getIntent().getBooleanExtra(Constants.KEY_BUNDLE_CARD_EDIT_CARD, false);

		initSaveFragment();
		setLanguge();

		linear = (LinearLayout) findViewById(R.id.at_create_card_linear);
        if(isEditCard){
            FragCreateCardInfo fm = new FragCreateCardInfo();
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.KEY_BUNDLE_CARD_EDIT_CARD, isEditCard);
            fm.setArguments(bundle);
            changeFragment(Constants.TYPE_CREATE_CARD_INFO, fm);
        }else
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

    String mFrontUrl = "";
    String mBackUrl = "";
    public void savePhoto(final Bitmap mBitmapFront, final Bitmap mBitmapBack){
        showDialogLoading();
        mFrontUrl = "";
        mBackUrl = "";
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.at_create_card_linear);

        final String nameImageFront = "" + System.currentTimeMillis() + ".jpg";
        final String nameImageBack = "" + System.currentTimeMillis() + ".jpg";
        TaskExecuter.TaskTemplate<Void, String> upFrontBitmap = new TaskExecuter.TaskTemplate<Void, String>() {
            @Override
            public String doInBackground(Void... params) {
                String result = URLProvider.postPhoto(nameImageFront, BitmapUtils.getByteArrayOutputStream(mBitmapFront, 100));
                try {
                    JSONObject object = new JSONObject(result);
                    mFrontUrl = object.optString("image");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String mResult = URLProvider.postPhoto(nameImageBack, BitmapUtils.getByteArrayOutputStream(mBitmapBack, 100));
                try {
                    JSONObject object = new JSONObject(mResult);
                    mBackUrl = object.optString("image");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onPostExecute(String result) {
                ((FragCreateCardImage)fragment).callCreateKard(mFrontUrl, mBackUrl);
            }
        };
        TaskExecuter.getInstance(this).execute(upFrontBitmap, TaskExecuter.PRIORITY_BLOCKING);
    }

    public void createNewCard(String user_id, String company, String member_card_name, String member_card_number, String front_of_the_card,
                               String back_of_the_card, String description, String card_number_type, boolean isOther){
        if(isOther){
            DataLoader.postParam(URLProvider.getParamsCreateCardWithUser(user_id, company, member_card_name, member_card_number,
                    front_of_the_card, back_of_the_card, description, card_number_type), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    hideDialogLoading();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String result) {
                    hideDialogLoading();
                    createKardDone(result);
                }
            });
        }else{
            DataLoader.postParam(URLProvider.getParamsCreateCardWithCompanyByCategory(user_id, company, member_card_name, member_card_number,
                    front_of_the_card, back_of_the_card, description, card_number_type), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    hideDialogLoading();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String result) {
                    hideDialogLoading();
                    createKardDone(result);
                }
            });
        }
    }

    private void createKardDone(String result){
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.at_create_card_linear);
        int statusCode = 0;
        int cardID = -1;
        try {
            JSONObject object = new JSONObject(result);
            statusCode = object.optInt("statusCode");
            JSONObject json = object.getJSONObject("data");
            cardID = json.optInt("member_card_id", -1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(statusCode == Constants.API_REQUEST_STATUS_SUCCESS && cardID > 0){
            Debug.toast(this, getResources().getString(R.string.frag_createcard_info_successfully));
            if(fragment instanceof FragCreateCardImage)
                ((FragCreateCardImage)fragment).showSuccess();
        }else{
            Debug.toast(this, getResources().getString(R.string.frag_createcard_info_failed));
        }
    }

    public void updateCard(String user_id, String company, String member_card_name, String member_card_number, String front_of_the_card,
                              String back_of_the_card, String description, String card_number_type, boolean isOther){
        if(isOther){
            DataLoader.postParam(URLProvider.getParamsCreateCardWithUser(user_id, company, member_card_name, member_card_number,
                    front_of_the_card, back_of_the_card, description, card_number_type), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    hideDialogLoading();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String result) {
                    hideDialogLoading();
                    createKardDone(result);
                }
            });
        }else{
            DataLoader.postParam(URLProvider.getParamsCreateCardWithCompanyByCategory(user_id, company, member_card_name, member_card_number,
                    front_of_the_card, back_of_the_card, description, card_number_type), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    hideDialogLoading();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String result) {
                    hideDialogLoading();
                    createKardDone(result);
                }
            });
        }
    }
}
