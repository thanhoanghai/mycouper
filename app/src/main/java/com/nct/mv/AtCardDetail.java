package com.nct.mv;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.nct.constants.GlobalInstance;
import com.nct.model.MemberCardObject;
import com.nct.utils.Debug;
import com.nct.utils.Utils;

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

	private LinearLayout frameMaps;
	private Button bntCloseMap;
	private TextView btOpenmap;

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

		btOpenmap = (TextView) findViewById(R.id.card_detail_bt_openmap);
		btOpenmap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				frameMaps.setVisibility(View.VISIBLE);
			}
		});

		frameMaps = (LinearLayout) findViewById(R.id.card_detail_frame_maps);
		bntCloseMap = (Button) findViewById(R.id.card_detail_bt_closemap);
		bntCloseMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				frameMaps.setVisibility(View.GONE);
			}
		});

		memberCard = GlobalInstance.getInstance().memberCard;

		initTopbar(memberCard.company_name);
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightImage(R.drawable.icon_menu_dot);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		imgIcon = (ImageView) findViewById(R.id.at_card_detail_img);
		initImageLoader();
		displayImage(imgIcon, memberCard.company_logo);

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
		imgBack = (ImageView) findViewById(R.id.card_detail_img_back);
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
		imgCode.setVisibility(status1);
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
				Debug.toast(AtCardDetail.this,
						"Sorry! unable to create maps");
			}
		}
	}
}
