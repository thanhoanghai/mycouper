package com.nct.mv;

import android.os.Bundle;
import android.view.View;

import com.nct.customview.TfTextView;
import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtIntroduce extends AtBase {
	private static final String tag = "AtIntroduce";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_introduce);

		setLanguge();
		initTopbar(getString(R.string.introduce));
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
