package com.nct.mv;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.nct.utils.Debug;
import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtUserProfile extends AtBase {
	private static final String tag = "AtUserProfile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_user_profile);

		setLanguge();
		initTopbar(getString(R.string.user_account));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.INVISIBLE);

	}
}
