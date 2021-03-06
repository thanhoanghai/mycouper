package com.nct.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.loopj.android.http.TextHttpResponseHandler;

import com.nct.adapter.FragMemberCardAdapter;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.customview.TfTextView;
import com.nct.dataloader.DataHelper;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.gridview.SuperListview;
import com.nct.model.CardObject;
import com.nct.model.MemberCardData;
import com.nct.model.MemberCardObject;
import com.nct.utils.Pref;
import com.nct.utils.Utils;


import org.apache.http.Header;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import thh.com.mycouper.R;

public class FragMemberCard extends BaseGridFragment<MemberCardObject> {

	private TfTextView bntAddmore;
	private String idLang=GlobalInstance.getInstance().idLanguage;

	public static FragMemberCard newInstance() {
		FragMemberCard f = new FragMemberCard();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		if(!idLang.equals(GlobalInstance.getInstance().idLanguage))
		{
			idLang = GlobalInstance.getInstance().idLanguage;
			bntAddmore.setText(getString(R.string.frag_membercard_addmore));
		}
		if(GlobalInstance.getInstance().isReloadMemberCard)
		{
			GlobalInstance.getInstance().isReloadMemberCard = false;
			DelayTimeFinish();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.frag_member_card, container, false);

		disableLoadmore();

		bntAddmore = (TfTextView) v.findViewById(R.id.frag_membercard_bt_admore);
		bntAddmore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.gotoScreenCreateCard(getActivity(), false);
			}
		});

		return v;
	}

	@SuppressLint("InflateParams")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (isAdapterNull()) {
			DelayTimeStart(Constants.API_DELAY_TIME);
		} else
			setDataDefault();

	}

	public void DelayTimeFinish() {
		if(Utils.isNetworkAvailable(getActivity()))
			loadData();
		else
		{
			loadDataOffline();
		}
	}

	@Override
	protected void loadData() {
		if(!TextUtils.isEmpty(GlobalInstance.getInstance().getUserID()))
			DataLoader.get(URLProvider.getMemberCardForUser(GlobalInstance.getInstance().getUserID()),
				mTextHttpResponseHandler);
	}

	@Override
	protected boolean handleLoadingDataSuccess(String result) {

		Pref.SaveStringObject(Constants.FRAG_MEMBER_CARD_SAVE_DATA, result, getActivity());
		showDataApp(result);
		return true;
	}

	@Override
	protected FragMemberCardAdapter initAdapter(ArrayList<MemberCardObject> list) {
		FragMemberCardAdapter adapter = new FragMemberCardAdapter(getActivity(), list);
		return adapter;
	}


	private void showDataApp(String result)
	{
		MemberCardData object = DataHelper.getMemberCardData(result);
		if(object!=null && object.data!=null)
			if(object.data.size() > 0) {
				Collections.sort(object.data, new Comparator<MemberCardObject>() {
					@Override
					public int compare(MemberCardObject s1, MemberCardObject s2) {
						return s1.company_name.compareToIgnoreCase(s2.company_name);
					}
				});
			}
		setData(object.data,false);
	}

	private void loadDataOffline()
	{
		String result = Pref.getStringObject(Constants.FRAG_MEMBER_CARD_SAVE_DATA,getActivity());
		if(!TextUtils.isEmpty(result))
			showDataApp(result);
	}



}