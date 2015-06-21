package com.nct.mv;

import android.os.Bundle;
import android.os.Handler;

import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.utils.Debug;
import com.nct.utils.Pref;
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

		int indexLangua = Pref.getIntObject(Constants.SAVE_ID_LANGUAGE,AtSplash.this);
		GlobalInstance.getInstance().idLanguage = Constants.ID_LANGUAGE[indexLangua];

		moveActivity.postDelayed(moveRunnable, 3000);
		String hash = Utils.showHashKeyFacebook(AtSplash.this);
		Debug.logData(tag,hash);
	}
}
