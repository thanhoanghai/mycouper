package com.nct.mv;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;

import com.nct.fragment.FragCreateCardSearch;
import com.nct.fragment.FragHome;
import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtCreateCard extends AtBase {

	private LinearLayout linear;
	private int TYPE_CREATE_CARD_SEARCH = 0;
	private int typeFragment = 0;

	private FragCreateCardSearch fragCreateCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_create_card);

		setLanguge();

		linear = (LinearLayout) findViewById(R.id.at_create_card_linear);
		changeFragment();
	}


	private void changeFragment()
	{
		if(typeFragment == TYPE_CREATE_CARD_SEARCH)
		{
			try {
				FragmentTransaction fragmentTransaction = getSupportFragmentManager()
						.beginTransaction();
				if(fragCreateCard == null)
					fragCreateCard = new FragCreateCardSearch();
				fragmentTransaction.replace(R.id.at_create_card_linear,
						fragCreateCard);
				fragmentTransaction.commit();
			} catch (Exception ex) {
			}
		}
	}
}
