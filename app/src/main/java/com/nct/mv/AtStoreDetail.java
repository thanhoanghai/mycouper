package com.nct.mv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.adapter.CouponCardPagerAdapter;
import com.nct.adapter.StampCardPagerAdapter;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.customview.DialogCustom;
import com.nct.customview.NavigationStateRelativeLayout;
import com.nct.customview.TfTextView;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.model.CouponCategory;
import com.nct.model.StampQrcode;
import com.nct.utils.BitmapUtils;
import com.nct.utils.Debug;
import com.nct.utils.Pref;
import com.nct.utils.Utils;

import org.apache.http.Header;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import thh.com.mycouper.R;

public class AtStoreDetail extends AtBase implements View.OnClickListener {

	private static final String tag = "AtStoreDetail";
	private ImageView companyLogo, imgQrcode;
	private TfTextView txtCompanyName;
	private ViewPager viewPager;
	private StampCardPagerAdapter adapter;
	private CouponCardPagerAdapter couponCardPagerAdapter;
	private TextView txtTitleName, txtName, txtStoreName, txtDate;
	private TfTextView btnCreate, btnSaveToGallery;
	private NavigationStateRelativeLayout btnStamp, btnCoupon;
	private LinearLayout contentTab;
	private LinearLayout containPager;

	private LinearLayout linearContent;
	private ScrollView scrollview;

	private enum TAB_CARD {StampCard, CouponCard};
	private TAB_CARD tab_card = TAB_CARD.StampCard;

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

		containPager = (LinearLayout) findViewById(R.id.containPager);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setClipChildren(false);
		viewPager.setPageMargin(-150);

		int mWidth = getResources().getDisplayMetrics().widthPixels * 7 / 10;
		FrameLayout.LayoutParams ly = (FrameLayout.LayoutParams) containPager.getLayoutParams();
		ly.width = ViewGroup.LayoutParams.MATCH_PARENT;
		ly.height = mWidth;

		txtTitleName = (TextView) findViewById(R.id.txt_title1);
		txtName = (TextView) findViewById(R.id.txt_text1);
		txtStoreName = (TextView) findViewById(R.id.txt_text2);
		txtDate = (TextView) findViewById(R.id.txt_text3);

		contentTab = (LinearLayout) findViewById(R.id.lyContentTab);
		btnStamp = (NavigationStateRelativeLayout) findViewById(R.id.stores_tab_square);
		btnStamp.setOnClickListener(this);
		btnStamp.setChecked(true);
		btnCoupon = (NavigationStateRelativeLayout) findViewById(R.id.stores_tab_coupon);
		btnCoupon.setOnClickListener(this);

		if (GlobalInstance.getInstance().storesInfo.company_name != null && !GlobalInstance.getInstance().storesInfo.equals("NULL"))
			txtStoreName.setText(GlobalInstance.getInstance().storesInfo.company_name);
		else
			txtStoreName.setText("");

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				if(tab_card == TAB_CARD.StampCard){
					StampQrcode item = GlobalInstance.getInstance().storesInfo.stamp_category.get(0).stamp_pos.get(position);
					showInfoStampCard(item);
				}else{
					CouponCategory item = GlobalInstance.getInstance().storesInfo.coupon_category.get(0);
					showInfoCouponCard(item);
				}
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
		adapter = new StampCardPagerAdapter(this, GlobalInstance.getInstance().storesInfo.stamp_category.get(0).stamp_pos);
		couponCardPagerAdapter = new CouponCardPagerAdapter(this, GlobalInstance.getInstance().storesInfo.coupon_category);
		loadStampCard();
		hideDialogLoading();
		scrollview.setVisibility(View.VISIBLE);
	}

	private void loadStampCard(){
		StampQrcode item = GlobalInstance.getInstance().storesInfo.stamp_category.get(0).stamp_pos.get(0);
		showInfoStampCard(item);
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(GlobalInstance.getInstance().storesInfo.stamp_category.get(0).stamp_pos.size());
		viewPager.setCurrentItem(0);
	}

	private void showInfoStampCard(StampQrcode item){
		if (item.pos_name != null && !item.pos_name.equals("NULL"))
			txtName.setText(item.pos_name);
		else
			txtName.setText("");
		if (item.last_update != null && !item.last_update.equals("NULL"))
			txtDate.setText(Utils.formatDate(item.last_update));
		else
			txtDate.setText("");
	}

	private void loadCouponCard(){
		CouponCategory item = GlobalInstance.getInstance().storesInfo.coupon_category.get(0);
		showInfoCouponCard(item);
		viewPager.setAdapter(couponCardPagerAdapter);
		viewPager.setOffscreenPageLimit(GlobalInstance.getInstance().storesInfo.coupon_category.size());
		viewPager.setCurrentItem(0);
	}

	private void showInfoCouponCard(CouponCategory item){
		if (item.card_name != null && !item.card_name.equals("NULL"))
			txtName.setText(item.card_name);
		else
			txtName.setText("");
		if (item.last_update != null && !item.last_update.equals("NULL"))
			txtDate.setText(Utils.formatDate(item.last_update));
		else
			txtDate.setText("");
	}

	public void onPagerPromotionItemClick(int position, boolean isCoupon) {

		Intent intent = new Intent(this, AtQRCodeDetail.class);
		intent.putExtra(Constants.KEY_BUNDLE_STORE_NAME, GlobalInstance.getInstance().storesInfo.company_name);
		if(isCoupon){
			if(GlobalInstance.getInstance().storesInfo.coupon_category != null
					&& GlobalInstance.getInstance().storesInfo.coupon_category.size() > 0){
				CouponCategory item = GlobalInstance.getInstance().storesInfo.coupon_category.get(position);
				intent.putExtra(Constants.KEY_BUNDLE_STORE_QRCODE_NAME, item.card_name);
				intent.putExtra(Constants.KEY_BUNDLE_STORE_QRCODE_DATE, item.last_update);
				intent.putExtra(Constants.KEY_BUNDLE_STORE_QRCODE_ID, item.qrcode_id);
			}

		}else{
			if (GlobalInstance.getInstance().storesInfo.stamp_category.get(0).stamp_pos != null) {
				StampQrcode tmpItem = GlobalInstance.getInstance().storesInfo.stamp_category.get(0).stamp_pos.get(position
						% GlobalInstance.getInstance().storesInfo.stamp_category.get(0).stamp_pos.size());
				intent.putExtra(Constants.KEY_BUNDLE_STORE_QRCODE_NAME, tmpItem.pos_name);
				intent.putExtra(Constants.KEY_BUNDLE_STORE_QRCODE_DATE, tmpItem.last_update);
				intent.putExtra(Constants.KEY_BUNDLE_STORE_QRCODE_ID, tmpItem.qrcode);
			}
		}

		startActivity(intent);
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
			case R.id.stores_tab_square:
				tab_card = TAB_CARD.StampCard;
				setActiveView(v.getId());
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								loadStampCard();
							}
						});
					}
				}, 1000);

				break;
			case R.id.stores_tab_coupon:
				tab_card = TAB_CARD.CouponCard;
				setActiveView(v.getId());
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								loadCouponCard();
							}
						});
					}
				}, 1000);

				break;
		}
	}

	private void setActiveView(int selectID){
		if (contentTab != null) {
			int count = contentTab.getChildCount();
			for (int i = 0; i < count; i++) {
				View v = contentTab.getChildAt(i);
				if (v instanceof NavigationStateRelativeLayout) {
					if(selectID == v.getId())
						((NavigationStateRelativeLayout) v).setChecked(true);
					else
						((NavigationStateRelativeLayout) v).setChecked(false);
				}
			}

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
