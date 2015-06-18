package com.nct.mv;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.nct.constants.Constants;
import com.nct.customview.TouchImageView;

import thh.com.mycouper.R;

public class AtPreviewImage extends AtBase {

	private String linkImage = "";
	private TouchImageView img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_preview_image);

		initTopbar("Preview Image");
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.INVISIBLE);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			linkImage = extras.getString(Constants.AT_PREVIEW_ID_LINK_IMAGE);
			// and get whatever type user account id is
		}
		img = (TouchImageView) findViewById(R.id.preview_image_img);
		initImageLoader();
		if(!TextUtils.isEmpty(linkImage))
			displayImage(img,linkImage);
	}
}
