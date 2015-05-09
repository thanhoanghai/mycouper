package com.nct.mv;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtLogin extends AtBase {

	private EditText edtUsername,edtPass;
	private Button btLogin,btLoginFace;
	private TextView btForgot,btSignup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_login);

		edtUsername = (EditText) findViewById(R.id.login_edt_username);
		edtPass = (EditText) findViewById(R.id.login_edt_password);

		btLogin = (Button) findViewById(R.id.login_bt_login);
		btLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.gotoScreenMain(AtLogin.this);
			}
		});
		btLoginFace = (Button) findViewById(R.id.login_bt_login_fb);
		btLoginFace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		btForgot = (TextView) findViewById(R.id.login_bt_forgotpass);
		btForgot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.gotoScreenForgotPass(AtLogin.this);
			}
		});
		btSignup = (TextView) findViewById(R.id.login_bt_signup);
		btSignup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.gotoScreenSignUp(AtLogin.this);
			}
		});
	}
}
