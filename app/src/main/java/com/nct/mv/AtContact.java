package com.nct.mv;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nct.adapter.AtLanguageAdapter;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.utils.Pref;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class AtContact extends AtBase {
	private static final String tag = "AtContact";

	private TextView tvMail ,tvPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_contact);

		setLanguge();
		initTopbar(getString(R.string.contact));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.INVISIBLE);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});


		tvMail = (TextView) findViewById(R.id.at_contact_tv_mail);
		tvMail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMailGopY();
			}
		});

		tvPhone = (TextView) findViewById(R.id.at_contact_tv_phone);
		tvPhone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callPhone();
			}
		});

	}

	private void callPhone()
	{
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + Constants.PHONE_SUPPORT));
		startActivity(callIntent);
	}


	private void sendMailGopY() {
		PackageInfo pInfo;
		String version = "";
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.MAIL_SUPPORT});
		i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
		try {
			startActivity(Intent.createChooser(i,
					getString(R.string.app_name)));
		} catch (android.content.ActivityNotFoundException ex) {
		}
	}

}
