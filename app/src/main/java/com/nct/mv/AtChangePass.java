package com.nct.mv;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.nct.utils.Debug;
import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtChangePass extends AtBase {
	private static final String tag = "AtChangePass";

	private Button bntClose,bntChange;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_change_pass);

		bntChange = (Button)findViewById(R.id.change_pass_bt_change);
		bntChange.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		bntClose = (Button) findViewById(R.id.change_pass_bt_close);
		bntClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}
}
