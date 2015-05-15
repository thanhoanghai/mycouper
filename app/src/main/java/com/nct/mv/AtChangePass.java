package com.nct.mv;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nct.utils.Debug;
import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtChangePass extends AtBase {
	private static final String tag = "AtChangePass";

	private Button bntClose,bntChange;

	private EditText edtCurrentpass,edtNewpass,edtConfirmpass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_change_pass);

		initTopbar(getString(R.string.change_pass));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.INVISIBLE);

		edtCurrentpass = (EditText) findViewById(R.id.change_pass_edt_currentpass);
		edtNewpass = (EditText) findViewById(R.id.change_pass_edt_newpass);
		edtConfirmpass = (EditText) findViewById(R.id.change_pass_edt_confirm);

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
