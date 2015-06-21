package com.nct.mv;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nct.adapter.AtLanguageAdapter;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.utils.Debug;
import com.nct.utils.Pref;
import com.nct.utils.Utils;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class AtLanguage extends AtBase {
	private static final String tag = "AtLanguage";

	private ListView lv;
	private AtLanguageAdapter adapter;

	private int saveIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_language);

		setLanguge();
		initTopbar(getString(R.string.language));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.GONE);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		setTopbarTxtRightVisible(View.VISIBLE);
		setTopbarTxtRightTitle(getResources().getString(R.string.ok));
		setTopbarRightTxtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Pref.SaveIntObject(Constants.SAVE_ID_LANGUAGE,saveIndex,AtLanguage.this);
				GlobalInstance.getInstance().idLanguage = Constants.ID_LANGUAGE[saveIndex];
				setLanguge();
				setText();
				finish();
			}
		});

		ArrayList<String> list = new ArrayList<String>();
		list.add("English");
		list.add("Vietnam");
		lv = (ListView)findViewById(R.id.at_language_lv);
		adapter = new AtLanguageAdapter(AtLanguage.this,list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				saveIndex = position;
				adapter.setIndex(position);
			}
		});

	}

	private void setText()
	{
		setTopbarTitle(getString(R.string.language));
		setTopbarTxtRightTitle(getResources().getString(R.string.ok));
	}
}
