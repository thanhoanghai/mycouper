package com.nct.mv;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import thh.com.mycouper.R;

public class AtStoreDetail extends AtBase {
	private static final String tag = "AtStoreDetail";


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

	}

}
