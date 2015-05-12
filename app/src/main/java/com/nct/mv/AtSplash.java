package com.nct.mv;

import android.os.Bundle;
import android.os.Handler;

import com.nct.utils.Debug;
import com.nct.utils.Utils;
import thh.com.mycouper.R;

public class AtSplash extends AtBase {
	private static final String tag = "AtSplash";

	private Handler moveActivity = new Handler();
	private Runnable moveRunnable = new Runnable() {
		@Override
		public void run() {
			Utils.gotoScreenLogin(AtSplash.this);
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_splash);

		moveActivity.postDelayed(moveRunnable, 2000);
		String hash = Utils.showHashKeyFacebook(AtSplash.this);
		Debug.logData(tag,hash);
	}
}
