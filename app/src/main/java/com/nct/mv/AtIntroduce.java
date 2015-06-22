package com.nct.mv;

import android.os.Bundle;
import android.view.View;

import com.nct.constants.Constants;
import com.nct.customview.TfTextView;
import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtIntroduce extends AtBase {
	private static final String tag = "AtIntroduce";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_introduce);

		Bundle extras = getIntent().getExtras();
		int index = extras.getInt(Constants.AT_INTRODUCE_INDEX);

		setLanguge();
		if(index==0)
			initTopbar(getString(R.string.introduce));
		else
			initTopbar(getString(R.string.term_and_condition));
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
