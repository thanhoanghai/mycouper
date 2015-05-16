package com.nct.mv;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.model.UserObject;
import com.nct.utils.Debug;
import com.nct.utils.Pref;
import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtUserProfile extends AtBase {
	private static final String tag = "AtUserProfile";

	private TextView tvFistname,tvLastname,tvBirth,tvcivility,tvPhone,tvLastLogin;
	private TextView tvEmail,tvPass;
	private UserObject userObject;

	private Button bntLogout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_user_profile);

		setLanguge();
		initTopbar(getString(R.string.user_account));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.INVISIBLE);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		userObject = GlobalInstance.getInstance().userInfo;

		tvEmail = (TextView) findViewById(R.id.user_account_email);
		tvPass = (TextView) findViewById(R.id.user_account_password);

		tvFistname = (TextView) findViewById(R.id.user_account_firstname);
		tvLastname = (TextView) findViewById(R.id.user_account_lastname);
		tvBirth = (TextView) findViewById(R.id.user_account_birthday);
		tvcivility = (TextView) findViewById(R.id.user_account_civility);
		tvPhone = (TextView) findViewById(R.id.user_account_phone);
		tvLastLogin = (TextView) findViewById(R.id.user_account_lastlogin);

		bntLogout = (Button) findViewById(R.id.user_account_bt_logout);
		bntLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Pref.SaveStringObject(Constants.ID_SAVE_LOGIN,"",AtUserProfile.this);
				Intent intent = new Intent(AtUserProfile.this, AtLogin.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});

		tvPass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.gotoScreenChangepass(AtUserProfile.this);
			}
		});

		setData();
	}

	private void setData()
	{
		tvFistname.setText(userObject.first_name);
		tvLastname.setText(userObject.last_name);
		tvBirth.setText(userObject.birthday);
		tvcivility.setText(userObject.civility);
		tvPhone.setText(userObject.phone);
		tvLastLogin.setText(userObject.last_login_time);
		tvEmail.setText(userObject.user_email);
	}
}
