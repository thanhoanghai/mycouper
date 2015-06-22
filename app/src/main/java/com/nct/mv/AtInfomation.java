package com.nct.mv;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nct.utils.Utils;

import org.w3c.dom.Text;

import thh.com.mycouper.R;

public class AtInfomation extends AtBase {
	private static final String tag = "AtContact";

	private TextView tvVersion;
	private TextView tvIntroduce,tvTerm;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_infomation);

		setLanguge();
		initTopbar(getString(R.string.infomation));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.INVISIBLE);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});


		tvVersion = (TextView) findViewById(R.id.at_infomation_tvversion);
		String version = Utils.getVersionName(AtInfomation.this);
		tvVersion.setText(getString(R.string.version_user) + version);

		tvIntroduce = (TextView) findViewById(R.id.at_infomation_tv_introduce);
		tvIntroduce.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		tvTerm = (TextView) findViewById(R.id.at_infomation_tv_term);
		tvTerm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

	}

}
