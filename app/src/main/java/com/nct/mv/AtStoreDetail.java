package com.nct.mv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.adapter.QrcodePagerAdapter;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.customview.DialogCustom;
import com.nct.customview.TfTextView;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.model.StampQrcode;
import com.nct.utils.BitmapUtils;
import com.nct.utils.Debug;
import com.nct.utils.Pref;
import com.nct.utils.Utils;

import org.apache.http.Header;

import java.io.File;

import thh.com.mycouper.R;

public class AtStoreDetail extends AtBase implements View.OnClickListener {

	private static final String tag = "AtStoreDetail";
	private ImageView companyLogo, imgQrcode;
	private TfTextView txtCompanyName;
	private ViewPager viewPager;
	private QrcodePagerAdapter adapter;
	private TextView txtTitleName, txtName, txtStoreName, txtDate;
	private TfTextView btnCreate, btnSaveToGallery;

	private LinearLayout linearContent;
	private ScrollView scrollview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_store_detail);

		setLanguge();
		initTopbar(getString(R.string.stores));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		setTopbarBtRightImage(R.drawable.icon_logout_store);
		setTopbarRighttBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialogConfirmLogout();
			}
		});

		initImageLoader();

		linearContent = (LinearLayout) findViewById(R.id.at_store_detail_linear);
		scrollview = (ScrollView) findViewById(R.id.at_store_detail_scrollview);
		scrollview.setVisibility(View.INVISIBLE);

		companyLogo = (ImageView) findViewById(R.id.store_card_icon_img);
		if(GlobalInstance.getInstance().storesInfo.company_logo != null)
			displayImage(companyLogo, GlobalInstance.getInstance().storesInfo.company_logo);

		txtCompanyName = (TfTextView) findViewById(R.id.frag_create_card_info_tv_title);
		if(GlobalInstance.getInstance().storesInfo.company_name != null
				&& !GlobalInstance.getInstance().storesInfo.company_name.equals(""))
			txtCompanyName.setText(GlobalInstance.getInstance().storesInfo.company_name);

		btnCreate = (TfTextView) findViewById(R.id.stores_tv_create);
		btnCreate.setOnClickListener(this);
		btnSaveToGallery = (TfTextView) findViewById(R.id.stores_tv_save);
		btnSaveToGallery.setOnClickListener(this);

		viewPager = (ViewPager) findViewById(R.id.viewPager);

		int mWidth = getResources().getDisplayMetrics().widthPixels * 7 / 10;
		LinearLayout.LayoutParams ly = (LinearLayout.LayoutParams) viewPager.getLayoutParams();
		ly.width = ViewGroup.LayoutParams.MATCH_PARENT;
		ly.height = mWidth;

		txtTitleName = (TextView) findViewById(R.id.txt_title1);
		txtName = (TextView) findViewById(R.id.txt_text1);
		txtStoreName = (TextView) findViewById(R.id.txt_text2);
		txtDate = (TextView) findViewById(R.id.txt_text3);

		viewPager.setOffscreenPageLimit(GlobalInstance.getInstance().storesInfo.stamp_category.get(0).stamp_pos.size());
		viewPager.setClipChildren(false);
		viewPager.setPageMargin(-20);
		viewPager.setCurrentItem(0);

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {

				StampQrcode item = GlobalInstance.getInstance().storesInfo.stamp_category.get(0).stamp_pos.get(position);

				if (GlobalInstance.getInstance().storesInfo.company_name != null && !GlobalInstance.getInstance().storesInfo.equals("NULL"))
					txtStoreName.setText(GlobalInstance.getInstance().storesInfo.company_name);
				else
					txtStoreName.setText("");
				if (item.pos_name != null && !item.pos_name.equals("NULL"))
					txtName.setText(item.pos_name);
				else
					txtName.setText("");
				if (item.last_update != null && !item.last_update.equals("NULL"))
					txtDate.setText(Utils.formatDate(item.last_update));
				else
					txtDate.setText("");
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		showDialogLoading();
		DelayTimeStart(100);
	}

	@Override
	public void DelayTimeFinish() {
		adapter = new QrcodePagerAdapter(AtStoreDetail.this, GlobalInstance.getInstance().storesInfo.stamp_category.get(0).stamp_pos);
		viewPager.setAdapter(adapter);
		hideDialogLoading();
		scrollview.setVisibility(View.VISIBLE);
	}

	public void onPagerPromotionItemClick(int position) {
		if (GlobalInstance.getInstance().storesInfo.stamp_category.get(0).stamp_pos != null) {
			StampQrcode tmpItem = GlobalInstance.getInstance().storesInfo.stamp_category.get(0).stamp_pos.get(position
					% GlobalInstance.getInstance().storesInfo.stamp_category.get(0).stamp_pos.size());

			Intent intent = new Intent(this, AtQRCodeDetail.class);
			intent.putExtra(Constants.KEY_BUNDLE_STORE_QRCODE_NAME, tmpItem.pos_name);
			intent.putExtra(Constants.KEY_BUNDLE_STORE_NAME, GlobalInstance.getInstance().storesInfo.company_name);
			intent.putExtra(Constants.KEY_BUNDLE_STORE_QRCODE_DATE, tmpItem.last_update);
			intent.putExtra(Constants.KEY_BUNDLE_STORE_QRCODE_ID, tmpItem.qrcode);

			startActivity(intent);
//			Utils.gotoScreenStoreQRCodeDetail(AtStoreDetail.this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.stores_tv_create:
				break;
			case R.id.stores_tv_save:
				Bitmap bitmap = takeScreenshot();
				saveBitmap(bitmap);
				break;
		}
	}

	public Bitmap takeScreenshot() {
		linearContent.setDrawingCacheEnabled(true);
		return linearContent.getDrawingCache();
	}

	public void saveBitmap(Bitmap bitmap) {
		String fileName = System.currentTimeMillis() + ".jpg";
		File filePath = BitmapUtils.saveBitmapInSDCard(this, fileName, bitmap);
		if(filePath != null) {
			BitmapUtils.addImageToGallery(this, filePath.getPath(), "", "");
			Debug.toastDebug(this, getResources().getString(R.string.stores_message_save_gallery_success));
		}else
			Debug.toastDebug(this, getResources().getString(R.string.stores_message_save_gallery_failed));
	}

	private void showDialogConfirmLogout(){
		DialogCustom dialog = new DialogCustom(AtStoreDetail.this);
		dialog.setText(getString(R.string.confirm),getString(R.string.do_you_want_logout));
		dialog.setListenerFinishedDialog(new DialogCustom.FinishDialogConfirmListener() {
			@Override
			public void onFinishConfirmDialog(int i) {
				if (i == 1) {
					callLogout();
				}
			}
		});
		dialog.show();
	}

	private void callLogout(){
		RequestParams params = URLProvider.getParamStoreLogout(GlobalInstance.getInstance().storesInfo.company_id,
				GlobalInstance.getInstance().storesInfo.session_id, GlobalInstance.getInstance().storesInfo.session_id_code);

		showDialogLoading();
		DataLoader.postParam(params, new TextHttpResponseHandler() {
			@Override
			public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
				hideDialogLoading();
			}

			@Override
			public void onSuccess(int i, Header[] headers, String s) {

				Pref.SaveStringObject(Constants.ID_SAVE_STORE_LOGIN, "", AtStoreDetail.this);
				Debug.toast(AtStoreDetail.this, getResources().getString(R.string.logout_success));
				finish();
				hideDialogLoading();
			}
		});
	}


}
