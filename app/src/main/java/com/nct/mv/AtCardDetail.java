package com.nct.mv;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.adapter.CardDetailCouponAdapter;
import com.nct.adapter.CardDetailPosAdapter;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.customview.AndroidBarcodeView;
import com.nct.customview.DialogCouponSaved;
import com.nct.customview.DialogCustom;
import com.nct.customview.DialogInfoCustom;
import com.nct.customview.DialogRate;
import com.nct.customview.ListViewCustom;
import com.nct.dataloader.DataHelper;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.model.CouponData;
import com.nct.model.CouponObject;
import com.nct.model.CouponReceiveData;
import com.nct.model.MemberCardObject;
import com.nct.model.PosData;
import com.nct.model.PosObject;
import com.nct.utils.BitmapUtils;
import com.nct.utils.Debug;
import com.nct.utils.Pref;
import com.nct.utils.Utils;

import org.apache.http.Header;

import java.util.Date;

import thh.com.mycouper.R;

public class AtCardDetail extends AtBase {

	private final int CONSTANT_ZOOM_MAP = 15;

	private static final String tag = "AtCardDetail";

	private MemberCardObject memberCard;

	private ImageView imgIcon;

	private RadioButton bnt1,bnt2,bnt3;

	private TextView imgCode;
	private ImageView imgFront,imgBack;
	private RelativeLayout lyImageCard;

	// Google Map
	private GoogleMap googleMap;

	private LinearLayout frameMapInfo;
	private FrameLayout frameMaps;
	private Button bntCloseMap;
	private TextView btOpenmap;
	private TextView btRotate;
	private PosData posData;
	private PosObject posObject;

	private TextView barCodeID;
	private RelativeLayout lyImgCode;
	private ImageView imgQRcode;
	private LinearLayout linearBarcode;

	private ListView lvPos;
	private CardDetailPosAdapter lvPosAdapter;


	private TextView tvNameMembercard;

	private LinearLayout linearCoupon;
	private CouponData couponData;
	private ListViewCustom lvCoupon;
	private CardDetailCouponAdapter lvCouponAdapter;

	private RelativeLayout couponDetailLinear;
	private ImageView couponDetailImgIcon;
	private ImageView couponDetailImgCoupon;
	private TextView couponDetailTvCompany;
	private TextView couponDetailTvCardName;
	private TextView couponDetailTvExpire;
	private TextView couponDetailTvTermDes;
	//private TextView couponDetailTvStoreDes;
	private ListView couponDetailLvStore;

	private ScrollView couponDetailScrollview;
	private int indexCoupon = 0;
	private CouponObject couponObject;

	private Button couponDetailBntReceive;
	private Button couponDetailBntDelete;
	private Button couponDetailBntClose;
	private CouponReceiveData couponReceiveData;

	private int bitmapWidth=0,bitmapHeight=0;
	private int SCRW = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_card_detail);

		if(Constants.SCREEN_WIDTH < Constants.SCREEN_HEIGHT)
			SCRW = Constants.SCREEN_WIDTH;
		else
			SCRW = Constants.SCREEN_HEIGHT;

			bitmapWidth = SCRW - SCRW / 10;
			bitmapHeight = 3 * bitmapWidth / 4;

		try {
			// Loading map
			initGoogleMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		memberCard = GlobalInstance.getInstance().memberCard;

		initValueItem();
		initCouponDetail();

		initImageLoader();
		displayImage(imgIcon, memberCard.company_logo);
		displayImage(imgFront,memberCard.front_of_the_card);
		displayImage(imgBack, memberCard.back_of_the_card);
		checkTypeCard();

		getPositionCompany();
		getEcoupon();
		
	}

	private void setVisibleItemSegment(int index)
	{
		if(frameMaps.getVisibility()==View.VISIBLE)
			frameMaps.setVisibility(View.GONE);

		if(index==0)
			setStatusItem(View.VISIBLE,View.GONE,View.GONE);
		else if(index ==1)
			setStatusItem(View.GONE,View.VISIBLE,View.GONE);
		else if(index==2)
			setStatusItem(View.GONE,View.GONE,View.VISIBLE);

		couponDetailLinear.setVisibility(View.GONE);
	}

	private void setStatusItem(int status1,int status2,int status3)
	{
		if(memberCard.card_number_type.equals(Constants.TYPE_CARD_SCAN_CODE[0])) {
			imgCode.setVisibility(status1);
			lyImgCode.setVisibility(View.GONE);
		}else
		{
			lyImgCode.setVisibility(status1);
			imgCode.setVisibility(View.GONE);
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
			}else {
				googleMap.setMyLocationEnabled(true);
				googleMap.getUiSettings().setMyLocationButtonEnabled(true);
				googleMap.getUiSettings().setZoomControlsEnabled(true);
				googleMap.getUiSettings().setMapToolbarEnabled(false);
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
					if(posData.statusCode == Constants.STATUS_CODE_OK && posData!=null && posData.data.size() > 0)
					{
						posObject = posData.data.get(0);
						lvPosAdapter = new CardDetailPosAdapter(AtCardDetail.this,posData.data);
						lvPos.setAdapter(lvPosAdapter);
						showPosCompany();
					}else
					{
						showPostDefault();
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
				new LatLng(posObject.latitude, posObject.longitude)).zoom(CONSTANT_ZOOM_MAP).build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

		frameMapInfo.setVisibility(View.VISIBLE);
	}

	private void showPostDefault()
	{
		CameraPosition cameraPosition = new CameraPosition.Builder().target(
				new LatLng(10.771330, 106.704611)).zoom(CONSTANT_ZOOM_MAP).build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}


	private void getEcoupon()
	{
		DataLoader.get(URLProvider.getEcouponBymemberCompany(memberCard.company_id, GlobalInstance.getInstance().getUserID()), new TextHttpResponseHandler() {
			@Override
			public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
			}

			@Override
			public void onSuccess(int i, Header[] headers, String s) {
				Debug.logError(tag, s);
				couponData = DataHelper.getCouponData(s);
				if (couponData.statusCode == Constants.STATUS_CODE_OK && couponData != null && couponData.data.size() > 0) {
					lvCoupon.setVisibility(View.VISIBLE);
					if (lvCouponAdapter == null) {
						lvCouponAdapter = new CardDetailCouponAdapter(AtCardDetail.this, couponData.data, memberCard.company_logo);
						lvCoupon.setAdapter(lvCouponAdapter);
					} else
						lvCouponAdapter.updateData(couponData.data);

					linearCoupon.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void checkTypeCard()
	{
		if(memberCard.card_number_type.equals(Constants.TYPE_CARD_SCAN_CODE[1]) || memberCard.card_number_type.equals(Constants.TYPE_CARD_SCAN_CODE[2]))
		{
			imgCode.setVisibility(View.INVISIBLE);
			lyImgCode.setVisibility(View.VISIBLE);
			showImgQRcode();

		}else
		{
			//if(memberCard.card_number_type.equals(Constants.TYPE_CARD_SCAN_CODE[0]))
				imgCode.setText(memberCard.member_card_number);
				lyImgCode.setVisibility(View.INVISIBLE);
		}
		barCodeID.setText(memberCard.member_card_number);
	}

	private void initCouponDetail()
	{
		couponDetailLvStore = (ListViewCustom) findViewById(R.id.layout_coupon_detail_lv_store);
		couponDetailScrollview = (ScrollView) findViewById(R.id.layout_coupon_detail_scrollview);
		couponDetailLinear = (RelativeLayout) findViewById(R.id.layout_coupon_detail_linear);
		couponDetailImgIcon = (ImageView) findViewById(R.id.item_icon_image_img1);
		couponDetailImgCoupon = (ImageView) findViewById(R.id.layout_coupon_detail_img_banner);
		couponDetailTvCompany = (TextView) findViewById(R.id.layout_coupon_detail_tvcompany);
		couponDetailTvCardName = (TextView) findViewById(R.id.layout_coupon_detail_tvcarname);
		couponDetailTvExpire = (TextView) findViewById(R.id.layout_coupon_detail_tv_expire_at);
		couponDetailTvTermDes = (TextView) findViewById(R.id.layout_coupon_detail_tv_term_des);
		//couponDetailTvStoreDes = (TextView) findViewById(R.id.layout_coupon_detail_tv_store_des);
		couponDetailBntReceive = (Button) findViewById(R.id.layout_coupon_detail_bnt_receive);
		couponDetailBntReceive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialogInfo();
			}
		});
		couponDetailBntDelete = (Button) findViewById(R.id.layout_coupon_detail_bnt_delete);
		couponDetailBntDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				showDialogDeleteCoupon();
			}
		});

		couponDetailBntClose = (Button) findViewById(R.id.layout_coupon_detail_bnt_close);
		couponDetailBntClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				couponDetailLinear.setVisibility(View.GONE);
			}
		});
	}

	private void clickCouponReceiveData()
	{
		DataLoader.postParam(URLProvider.getParamsUpdateEcouponActivie(couponObject.coupon_id), new TextHttpResponseHandler() {
			@Override
			public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
				hideDialogLoading();
			}

			@Override
			public void onSuccess(int i, Header[] headers, String result) {
				hideDialogLoading();
				couponReceiveData = DataHelper.getCouponReceiveData(result);
				showDialogCouponReceiveSaved();
			}
		});
	}

	private void showDialogDeleteCoupon()
	{
		DialogCustom dialog = new DialogCustom(AtCardDetail.this,2);
		dialog.setText(getString(R.string.confirm),getString(R.string.do_you_want_delete_this_coupon));
		dialog.setListenerFinishedDialog(new DialogCustom.FinishDialogConfirmListener() {
			@Override
			public void onFinishConfirmDialog(int i) {
				if(i==1)
					clickCouponDelete();
			}
		});
		dialog.show();
	}
	private void clickCouponDelete()
	{
		DataLoader.postParam(URLProvider.getParamsUpdateEcouponDelete(couponObject.coupon_id), new TextHttpResponseHandler() {
			@Override
			public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
				hideDialogLoading();
			}
			@Override
			public void onSuccess(int i, Header[] headers, String result) {
				hideDialogLoading();
				couponDetailLinear.setVisibility(View.GONE);
				getEcoupon();
			}
		});
	}

	private void showDialogCouponReceiveSaved()
	{
		if(couponReceiveData!=null)
		{
			DialogCouponSaved dialog = new DialogCouponSaved(AtCardDetail.this, couponReceiveData.data.coupon_serial_number, couponObject.valid_from, couponObject.valid_to);
			dialog.setListenerFinishedDialog(new DialogCouponSaved.FinishDialogConfirmListener() {
				@Override
				public void onFinishConfirmDialog(int i) {
					couponDetailLinear.setVisibility(View.GONE);
					getEcoupon();
				}
			});
			if (dialog != null)
				dialog.show();

		}
	}

	private void setDataCouponDetail()
	{
		couponObject = couponData.data.get(indexCoupon);
		displayImage(couponDetailImgIcon, memberCard.company_logo);
		if(!TextUtils.isEmpty(couponObject.front_of_the_card))
			displayImage(couponDetailImgCoupon,couponObject.front_of_the_card);
		couponDetailTvCompany.setText(memberCard.company_name);
		couponDetailTvCardName.setText(memberCard.member_card_name);
		String timeTo = "";
		if(couponObject.valid_to != null){
			Date mTimeTo = Utils.dateTimeFromString(couponObject.valid_to);
			timeTo = Utils.formatDateTime(mTimeTo);
		}
		couponDetailTvExpire.setText(getString(R.string.expire_at) + " " + timeTo);

		String timeFrom = "";
		if(couponObject.valid_from != null){
			Date mTimeTo = Utils.dateTimeFromString(couponObject.valid_from);
			timeFrom = Utils.formatDateTime(mTimeTo);
		}
		couponDetailTvTermDes.setText(couponObject.coupon_condition);

		couponDetailScrollview.scrollTo(0, 0);
		couponDetailLinear.setVisibility(View.VISIBLE);

		if(lvPosAdapter!=null)
		{
			couponDetailLvStore.setVisibility(View.VISIBLE);
			couponDetailLvStore.setAdapter(lvPosAdapter);
		}

	}

	private void initValueItem()
	{
		linearCoupon = (LinearLayout) findViewById(R.id.card_detail_linear_coupon);
		lvCoupon = (ListViewCustom) findViewById(R.id.card_detail_lv_coupon);
		lvCoupon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				indexCoupon = position;
				setDataCouponDetail();
			}
		});

		tvNameMembercard = (TextView) findViewById(R.id.at_card_detail_tv_name_membercard);
		if(memberCard!=null && !TextUtils.isEmpty(memberCard.member_card_name) && !memberCard.member_card_name.equals(" ")
				&& !Constants.ITEM_NULL.equals(memberCard.member_card_name))
		{
			tvNameMembercard.setText(memberCard.member_card_name);
			tvNameMembercard.setVisibility(View.VISIBLE);
		}


		lvPos = (ListView) findViewById(R.id.card_detail_lv_maps);
		lvPos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				posObject = lvPosAdapter.getItem(position);
				showPosCompany();
			}
		});


		linearBarcode = (LinearLayout) findViewById(R.id.card_detail_linear_barcode);

		barCodeID = (TextView) findViewById(R.id.card_detail_tv_idcode);
		lyImgCode = (RelativeLayout) findViewById(R.id.lyImgCode);
		imgQRcode = (ImageView) findViewById(R.id.card_detail_img_qrcode);

		btOpenmap = (TextView) findViewById(R.id.card_detail_bt_openmap);
		btOpenmap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				frameMaps.setVisibility(View.VISIBLE);
			}
		});

		btRotate = (TextView) findViewById(R.id.card_detail_bt_rotate);
		btRotate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(AtCardDetail.this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
					AtCardDetail.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				else
					AtCardDetail.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		});

		frameMapInfo = (LinearLayout) findViewById(R.id.card_detail_frame_maps_info);
		frameMapInfo.setVisibility(View.GONE);

		frameMaps = (FrameLayout) findViewById(R.id.card_detail_frame_maps);
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

		imgIcon = (ImageView) findViewById(R.id.item_icon_image_img);

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

		lyImageCard = (RelativeLayout) findViewById(R.id.lyImgcard);

		imgFront = (ImageView) findViewById(R.id.card_detail_img_front);
		RelativeLayout.LayoutParams ly = (RelativeLayout.LayoutParams) imgFront.getLayoutParams();
		ly.width = bitmapWidth;
		ly.height = bitmapHeight;
		imgFront.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!TextUtils.isEmpty(memberCard.front_of_the_card))
					Utils.gotoScreenPreviewImage(AtCardDetail.this,memberCard.front_of_the_card);
			}
		});

		imgBack = (ImageView) findViewById(R.id.card_detail_img_back);
		RelativeLayout.LayoutParams ly1 = (RelativeLayout.LayoutParams) imgBack.getLayoutParams();
		ly1.width = bitmapWidth;
		ly1.height = bitmapHeight;
		imgBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!TextUtils.isEmpty(memberCard.back_of_the_card))
					Utils.gotoScreenPreviewImage(AtCardDetail.this,memberCard.back_of_the_card);
			}
		});

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
				GlobalInstance.getInstance().isReloadMemberCard = true;
				hideDialogLoading();
				//Debug.toast(AtCardDetail.this, s);
				finish();
			}
		});
	}


	private void showImgQRcode()
	{
		if(memberCard.card_number_type.equals(Constants.TYPE_CARD_SCAN_CODE[1])) {
            generateQRCode();
		}if(memberCard.card_number_type.equals(Constants.TYPE_CARD_SCAN_CODE[2])) {
            generateBarCode();
		}
	}

    private void generateBarCode(){
        try{
            int bitmapWidth = 3 * SCRW / 4;
			int bitmapHeight = bitmapWidth / 2;

            MultiFormatWriter writeBarcode = new MultiFormatWriter();
            BitMatrix bitMatrix = writeBarcode.encode(memberCard.member_card_number, BarcodeFormat.CODE_128, bitmapWidth, bitmapHeight);
            Bitmap bmp = BitmapUtils.toBitmap(bitMatrix);
            imgQRcode.setImageBitmap(bmp);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void generateQRCode(){
        try{
            bitmapWidth = SCRW / 2;

            QRCodeWriter writeCode = new QRCodeWriter();
            BitMatrix bitMatrix = writeCode.encode(memberCard.member_card_number, BarcodeFormat.QR_CODE, bitmapWidth, bitmapWidth);
            Bitmap bmp = BitmapUtils.toBitmap(bitMatrix);
            imgQRcode.setImageBitmap(bmp);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

	public void showDialogInfo(){
		DialogInfoCustom dialog = new DialogInfoCustom(AtCardDetail.this);
		dialog.setText(getString(R.string.user_infomation),getString(R.string.user_dialog_title));
		dialog.setListenerFinishedDialog(new DialogInfoCustom.FinishDialogConfirmListener() {
			@Override
			public void onFinishConfirmDialog(int i) {
				if(i==1)
					clickCouponReceiveData();
			}
		});
		dialog.show();
	}

}
