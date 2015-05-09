package com.nct.mv;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import thh.com.mycouper.R;

public class AtForgotPass extends AtBase {

	private EditText edtEmail;
	private Button btResend;

	private TextView btAlready,btLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_forgot_pass);

		edtEmail = (EditText) findViewById(R.id.forgot_edt_email);
		btResend = (Button) findViewById(R.id.forgot_bt_resend);

		btAlready = (TextView) findViewById(R.id.forgot_bt_already);
		btAlready.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btLogin = (TextView) findViewById(R.id.forgot_bt_login);
		btLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}
}
