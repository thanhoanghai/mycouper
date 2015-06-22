package com.nct.mv;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nct.adapter.AtLanguageAdapter;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.utils.Pref;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class AtContact extends AtBase {
	private static final String tag = "AtContact";


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


	}

}
