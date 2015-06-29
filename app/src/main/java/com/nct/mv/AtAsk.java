package com.nct.mv;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.nct.adapter.AtAskAdapter;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class AtAsk extends AtBase {
	private static final String tag = "AtAsk";

	private ListView lv;
	private AtAskAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_ask);

		setLanguge();
		initTopbar(getString(R.string.ask));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.INVISIBLE);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		lv = (ListView) findViewById(R.id.at_ask_lv);

		ArrayList<String> list = new ArrayList<>();
		list.add("a");
		list.add("a");
		list.add("a");
		list.add("a");
		list.add("a");
		list.add("a");
		list.add("a");
		list.add("a");
		list.add("a");
		list.add("a");
		list.add("a");
		adapter = new AtAskAdapter(AtAsk.this,list);
		lv.setAdapter(adapter);

	}

}
