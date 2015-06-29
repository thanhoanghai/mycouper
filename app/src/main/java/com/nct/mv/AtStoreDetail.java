package com.nct.mv;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.customview.DialogCustom;
import com.nct.customview.TfTextView;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.utils.BitmapUtils;
import com.nct.utils.Debug;
import com.nct.utils.Pref;
import com.nct.utils.Utils;

import org.apache.http.Header;

import thh.com.mycouper.R;

public class AtStoreDetail extends AtBase implements View.OnClickListener {

	private static final String tag = "AtStoreDetail";
	private ImageView imgQRCode;
	private ImageView companyLogo, imgQrcode;
	private TfTextView txtCompanyName;
	private Button btnLogout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_store_detail);

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

		initImageLoader();

		companyLogo = (ImageView) findViewById(R.id.store_card_icon_img);
		if(GlobalInstance.getInstance().storesInfo.company_logo != null)
			displayImage(companyLogo, GlobalInstance.getInstance().storesInfo.company_logo);

		imgQRCode = (ImageView) findViewById(R.id.stores_img_qrcode);
		if(GlobalInstance.getInstance().storesInfo.coupon_category != null)
			generateQRCode();

		txtCompanyName = (TfTextView) findViewById(R.id.frag_create_card_info_tv_title);
		if(GlobalInstance.getInstance().storesInfo.company_name != null
				&& !GlobalInstance.getInstance().storesInfo.company_name.equals(""))
			txtCompanyName.setText(GlobalInstance.getInstance().storesInfo.company_name);

		imgQRCode = (ImageView) findViewById(R.id.stores_img_qrcode);
		imgQRCode.setOnClickListener(this);

		btnLogout = (Button) findViewById(R.id.stores_bt_logout);
		btnLogout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.stores_img_qrcode:
				Utils.gotoScreenStoreQRCodeDetail(AtStoreDetail.this);
				break;
			case R.id.stores_bt_logout:
				showDialogConfirmLogout();
				break;
		}
	}

	private void showDialogConfirmLogout(){
		DialogCustom dialog = new DialogCustom(AtStoreDetail.this);
		dialog.setText(getString(R.string.confirm),getString(R.string.do_you_want_logout));
		dialog.setListenerFinishedDialog(new DialogCustom.FinishDialogConfirmListener() {
			@Override
			public void onFinishConfirmDialog(int i) {
				if (i == 1){
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

	private void generateQRCode(){
		try{
			String idQrcode = GlobalInstance.getInstance().storesInfo.coupon_category.get(0).qrcode_id;
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int bitmapWidth = metrics.widthPixels / 2;

			QRCodeWriter writeCode = new QRCodeWriter();
			BitMatrix bitMatrix = writeCode.encode(idQrcode, BarcodeFormat.QR_CODE, bitmapWidth, bitmapWidth);
			Bitmap bmp = BitmapUtils.toBitmap(bitMatrix);
			imgQRCode.setImageBitmap(bmp);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
