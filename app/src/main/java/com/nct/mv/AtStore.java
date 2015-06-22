package com.nct.mv;

import android.os.Bundle;
import android.view.View;

import thh.com.mycouper.R;

public class AtStore extends AtBase {
	private static final String tag = "AtIntroduce";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_store);

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
