package com.nct.mv;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.wearable.Asset;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nct.constants.Constants;
import com.nct.utils.BitmapUtils;
import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtQRCodeDetail extends AtBase {
	private static final String tag = "AtStoreDetail";

	private String mTitle, nameQrcode, nameStore, qrcodeId, qrcodeDate;
	private ImageView imgQrcode;
	private TextView txtTitle, txtName, txtStoreName, txtDate;
	private boolean isStampCard = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_store_qrcode_detail);

		if(getIntent() != null){
			isStampCard = getIntent().getBooleanExtra(Constants.KEY_BUNDLE_BOOLEAN_VALUE, false);
			mTitle = getIntent().getStringExtra(Constants.KEY_BUNDLE_STORE_QRCODE_TITLE);
			nameQrcode = getIntent().getStringExtra(Constants.KEY_BUNDLE_STORE_QRCODE_NAME);
			nameStore = getIntent().getStringExtra(Constants.KEY_BUNDLE_STORE_NAME);
			qrcodeId = getIntent().getStringExtra(Constants.KEY_BUNDLE_STORE_QRCODE_ID);
			qrcodeDate = getIntent().getStringExtra(Constants.KEY_BUNDLE_STORE_QRCODE_DATE);
		}

		setLanguge();
		initTopbar(getString(R.string.qrcode_on_mobile));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.INVISIBLE);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		imgQrcode = (ImageView) findViewById(R.id.stores_img_qrcode);
		if(qrcodeId != null && !qrcodeId.equals("NULL"))
			generateQRCode();

		txtTitle = (TextView) findViewById(R.id.txt_title1);
		txtTitle.setText(mTitle);
		txtName = (TextView) findViewById(R.id.txt_text1);
		if(nameQrcode != null && !nameQrcode.equals("NULL"))
			txtName.setText(nameQrcode);
		else
			txtName.setText("");
		txtStoreName = (TextView) findViewById(R.id.txt_text2);
		if(nameStore != null && !nameStore.equals("NULL"))
			txtStoreName.setText(nameStore);
		else
			txtStoreName.setText("");
		txtDate = (TextView) findViewById(R.id.txt_text3);
		if(qrcodeDate != null && !qrcodeDate.equals("NULL")) {
			if(isStampCard)
				txtDate.setText(qrcodeDate);
			else
				txtDate.setText(Utils.formatDate(qrcodeDate));
		}else
			txtDate.setText("");

	}

	private void generateQRCode(){
		try{
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int bitmapWidth = metrics.widthPixels / 2;

			QRCodeWriter writeCode = new QRCodeWriter();
			BitMatrix bitMatrix = writeCode.encode(qrcodeId, BarcodeFormat.QR_CODE, bitmapWidth, bitmapWidth);
			Bitmap bmp = BitmapUtils.toBitmap(bitMatrix);
			imgQrcode.setImageBitmap(bmp);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
