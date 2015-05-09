package com.nct.fragment;

import thh.com.mycouper.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;


public class FragHome extends BaseMainFragment implements OnClickListener {
	//private static final String TAG = "FragHome";

	public static FragHome newInstance() {
		FragHome f = new FragHome();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.frag_home, container, false);

		initViewLoadResult(view);
		viewResultLoad.showProgress();
		
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
