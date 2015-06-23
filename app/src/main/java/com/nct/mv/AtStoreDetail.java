package com.nct.mv;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtStoreDetail extends AtBase {

	private static final String tag = "AtStoreDetail";
	private ImageView imgQRCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_store_qrcode);

		setLanguge();
		initTopbar(getString(R.string.stores));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.INVISIBLE);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		imgQRCode = (ImageView) findViewById(R.id.stores_img_qrcode);
		imgQRCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.gotoScreenStoreQRCodeDetail(AtStoreDetail.this);
			}
		});

	}

}
