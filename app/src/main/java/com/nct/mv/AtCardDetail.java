package com.nct.mv;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.customview.AndroidBarcodeView;
import com.nct.customview.DialogCustom;
import com.nct.customview.DialogRate;
import com.nct.dataloader.DataHelper;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.model.CouponData;
import com.nct.model.MemberCardObject;
import com.nct.model.PosData;
import com.nct.model.PosObject;
import com.nct.utils.Debug;
import com.nct.utils.Utils;

import org.apache.http.Header;
import org.w3c.dom.Text;

import thh.com.mycouper.R;

public class AtCardDetail extends AtBase {
	private static final String tag = "AtCardDetail";

	private MemberCardObject memberCard;

	private ImageView imgIcon;

	private RadioButton bnt1,bnt2,bnt3;

	private TextView imgCode;
	private ImageView imgFront,imgBack;

	// Google Map
	private GoogleMap googleMap;

	private LinearLayout frameMapInfo;
	private LinearLayout frameMaps;
	private Button bntCloseMap;
	private TextView btOpenmap;
	private PosData posData;
	private PosObject posObject;

	private TextView mapTitle,mapPos,mapStreets,mapPhone,mapOpen,mapClose;

	private TextView barCodeID;
	private ImageView imgQRcode;
	private LinearLayout linearBarcode;

	private LinearLayout linearCoupon;
	private ImageView couponImg;
	private TextView couponTitle;
	private TextView couponExpire;
	private CouponData couponData;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_card_detail);

		try {
			// Loading map
			initGoogleMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		memberCard = GlobalInstance.getInstance().memberCard;

		initValueItem();

		initImageLoader();
		displayImage(imgIcon, memberCard.company_logo);
		displayImage(imgFront,memberCard.front_of_the_card);
		displayImage(imgBack,memberCard.back_of_the_card);
		checkTypeCard();

		getPositionCompany();
		getEcoupon();
	}


	private void setVisibleItemSegment(int index)
	{
		if(frameMaps.getVisibility()==View.VISIBLE)
			frameMaps.setVisibility(View.GONE);

		if(index==0)
			setStatusItem(View.VISIBLE,View.INVISIBLE,View.INVISIBLE);
		else if(index ==1)
			setStatusItem(View.INVISIBLE,View.VISIBLE,View.INVISIBLE);
		else if(index==2)
			setStatusItem(View.INVISIBLE,View.INVISIBLE,View.VISIBLE);
	}

	private void setStatusItem(int status1,int status2,int status3)
	{
		if(memberCard.card_number_type.equals(Constants.TYPE_CARD_SCAN_CODE[0])) {
			imgCode.setVisibility(status1);
			imgQRcode.setVisibility(View.INVISIBLE);
		}else
		{
			imgQRcode.setVisibility(status1);
			imgCode.setVisibility(View.INVISIBLE);
		}
		imgFront.setVisibility(status2);
		imgBack.setVisibility(status3);
	}

	private void initGoogleMap()
	{
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			// check if map is created successfully or not
			if (googleMap == null) {
				Debug.toast(AtCardDetail.this, "Sorry! unable to create maps");
			}
		}
	}

	private void getPositionCompany()
	{
		if(!TextUtils.isEmpty(memberCard.company_id)) {
			DataLoader.get(URLProvider.getPosCompany(memberCard.company_id), new TextHttpResponseHandler() {
				@Override
				public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
				}

				@Override
				public void onSuccess(int i, Header[] headers, String s) {
					posData = DataHelper.getPosData(s);
					if(posData!=null && posData.data.size() > 0)
					{
						posObject = posData.data.get(0);
						showPosCompany();
					}

				}
			});
		}
	}

	private void showPosCompany()
	{
		MarkerOptions marker = new MarkerOptions().position(new LatLng(posObject.latitude, posObject.longitude)).title("");
		marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		googleMap.addMarker(marker);
		CameraPosition cameraPosition = new CameraPosition.Builder().target(
				new LatLng(posObject.latitude, posObject.longitude)).zoom(12).build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

		mapTitle.setText(posObject.pos_name);
		mapPos.setText(posObject.pos_id);
		mapStreets.setText(posObject.address + " | " + posObject.address2);
		mapPhone.setText(posObject.phone);

		frameMapInfo.setVisibility(View.VISIBLE);


	}


	private void getEcoupon()
	{
		DataLoader.get(URLProvider.getEcouponBymemberCompany(memberCard.company_id, GlobalInstance.getInstance().getUserID()), new TextHttpResponseHandler() {
			@Override
			public void onFailure(int i, Header[] headers, String s, Throwable throwable) {}
			@Override
			public void onSuccess(int i, Header[] headers, String s) {
				Debug.logError(tag, s);
				couponData = DataHelper.getCouponData(s);
				if(couponData.statusCode == Constants.STATUS_CODE_OK && couponData!=null && couponData.data.size() > 0)
				{
					linearCoupon.setVisibility(View.VISIBLE);
					displayImage(couponImg, memberCard.company_logo);
					couponTitle.setText(couponData.data.get(0).card_name);
					couponExpire.setText(getString(R.string.expire_at) +couponData.data.get(0).valid_to);
				}
			}
		});
	}

	private void checkTypeCard()
	{
		if(memberCard.card_number_type.equals(Constants.TYPE_CARD_SCAN_CODE[0]))
		{
			imgCode.setText(memberCard.member_card_number);
			imgQRcode.setVisibility(View.INVISIBLE);
		}else if(memberCard.card_number_type.equals(Constants.TYPE_CARD_SCAN_CODE[1]) || memberCard.card_number_type.equals(Constants.TYPE_CARD_SCAN_CODE[2]))
		{
			imgCode.setVisibility(View.INVISIBLE);
			imgQRcode.setVisibility(View.VISIBLE);
			showImgQRcode();

		}
		barCodeID.setText(memberCard.member_card_number);
	}

	private void initValueItem()
	{

		linearCoupon = (LinearLayout) findViewById(R.id.card_detail_linear_coupon);
		couponImg = (ImageView) findViewById(R.id.card_detail_coupon_img_icon);
		couponTitle = (TextView) findViewById(R.id.card_detail_coupon_tv_title);
		couponExpire = (TextView) findViewById(R.id.card_detail_coupon_tv_expire);

		linearBarcode = (LinearLayout) findViewById(R.id.card_detail_linear_barcode);

		barCodeID = (TextView) findViewById(R.id.card_detail_tv_idcode);
		imgQRcode = (ImageView) findViewById(R.id.card_detail_img_qrcode);

		btOpenmap = (TextView) findViewById(R.id.card_detail_bt_openmap);
		btOpenmap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				frameMaps.setVisibility(View.VISIBLE);
			}
		});

		frameMapInfo = (LinearLayout) findViewById(R.id.card_detail_frame_maps_info);
		frameMapInfo.setVisibility(View.GONE);

		frameMaps = (LinearLayout) findViewById(R.id.card_detail_frame_maps);
		frameMaps.setVisibility(View.GONE);
		bntCloseMap = (Button) findViewById(R.id.card_detail_bt_closemap);
		bntCloseMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				frameMaps.setVisibility(View.GONE);
			}
		});

		initTopbar(memberCard.company_name);
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightImage(R.drawable.icon_menu_dot);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setTopbarRighttBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialogDeletCard();
			}
		});

		imgIcon = (ImageView) findViewById(R.id.at_card_detail_img);

		bnt1= (RadioButton) findViewById(R.id.layout_segment_bt1);
		bnt1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setVisibleItemSegment(0);
			}
		});
		bnt2= (RadioButton) findViewById(R.id.layout_segment_bt2);
		bnt2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setVisibleItemSegment(1);
			}
		});
		bnt3= (RadioButton) findViewById(R.id.layout_segment_bt3);
		bnt3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setVisibleItemSegment(2);
			}
		});

		imgCode = (TextView) findViewById(R.id.card_detail_img_idcode);
		imgFront = (ImageView) findViewById(R.id.card_detail_img_front);
		imgFront.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!TextUtils.isEmpty(memberCard.front_of_the_card))
					Utils.gotoScreenPreviewImage(AtCardDetail.this,memberCard.front_of_the_card);
			}
		});
		imgBack = (ImageView) findViewById(R.id.card_detail_img_back);
		imgBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!TextUtils.isEmpty(memberCard.back_of_the_card))
					Utils.gotoScreenPreviewImage(AtCardDetail.this,memberCard.back_of_the_card);
			}
		});

		mapTitle = (TextView) findViewById(R.id.card_detail_tv_maptitle);
		mapPos = (TextView) findViewById(R.id.card_detail_tv_mappos);
		mapStreets = (TextView) findViewById(R.id.card_detail_tv_mapstreet);
		mapPhone = (TextView) findViewById(R.id.card_detail_tv_mapphone);
	}

	private DialogRate dialogRate;
	private void showDialogDeletCard()
	{
		if(dialogRate == null)
		{
			dialogRate = new DialogRate(AtCardDetail.this,3,getString(R.string.edit),getString(R.string.delete),getString(R.string.skip),"");
			dialogRate.setListenerFinishedDialog(new DialogCustom.FinishDialogConfirmListener() {
				@Override
				public void onFinishConfirmDialog(int i) {
                    switch (i){
                        case 0:
                            Utils.gotoScreenCreateCard(AtCardDetail.this, true);
                            break;
                        case 1:
                            showDialogConfirmDelete();
                            break;
                        case 2:

                            break;
                    }
				}
			});
		}
		dialogRate.show();
	}
	private void showDialogConfirmDelete()
	{
		DialogCustom dialog = new DialogCustom(AtCardDetail.this);
		dialog.setText(getString(R.string.confirm),getString(R.string.do_you_want_delete));
		dialog.setListenerFinishedDialog(new DialogCustom.FinishDialogConfirmListener() {
			@Override
			public void onFinishConfirmDialog(int i) {
				if (i == 1)
					loadApiDeleteThisCard();
			}
		});
		dialog.show();
	}

	private void loadApiDeleteThisCard()
	{
		showDialogLoading();
		DataLoader.postParam(URLProvider.getParamsDeleteMemberCard(memberCard.member_card_id), new TextHttpResponseHandler() {
			@Override
			public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
				hideDialogLoading();
			}

			@Override
			public void onSuccess(int i, Header[] headers, String s) {
				hideDialogLoading();
				Debug.toast(AtCardDetail.this, s);
				finish();
			}
		});
	}


	private void showImgQRcode()
	{
		String qr = "qr";
		if(memberCard.card_number_type.equals(Constants.TYPE_CARD_SCAN_CODE[1])) {
			displayImage(imgQRcode, "http://chart.apis.google.com/chart?chs=150x150&cht=qr&chl=" + memberCard.member_card_number);
		}if(memberCard.card_number_type.equals(Constants.TYPE_CARD_SCAN_CODE[2])) {
			AndroidBarcodeView view = new AndroidBarcodeView(AtCardDetail.this,memberCard.member_card_number);
			imgQRcode.setVisibility(View.INVISIBLE);
		 	linearBarcode.addView(view);
			linearBarcode.setVisibility(View.VISIBLE);
		}
	}
}
